package com.digitalstokvel.api;

import com.digitalstokvel.api.dto.ApiDtos.*;
import com.digitalstokvel.auth.CurrentUser;
import com.digitalstokvel.auth.entity.AppUser;
import com.digitalstokvel.common.exception.*;
import com.digitalstokvel.contribution.entity.*;
import com.digitalstokvel.contribution.repository.ContributionRepository;
import com.digitalstokvel.cycle.entity.*;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.entity.*;
import com.digitalstokvel.group.repository.*;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.digitalstokvel.momo.dto.*;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import com.digitalstokvel.momo.service.MoMoService;
import com.digitalstokvel.payout.entity.*;
import com.digitalstokvel.payout.repository.PayoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@Transactional
public class StokvelService {
    private final GroupRepository groups;
    private final GroupMemberRepository groupMembers;
    private final MemberRepository members;
    private final CycleRepository cycles;
    private final ContributionRepository contributions;
    private final PayoutRepository payouts;
    private final MoMoService momo;
    private final CurrentUser currentUser;

    public StokvelService(GroupRepository groups, GroupMemberRepository groupMembers,
                          MemberRepository members, CycleRepository cycles,
                          ContributionRepository contributions, PayoutRepository payouts,
                          MoMoService momo, CurrentUser currentUser) {
        this.groups = groups;
        this.groupMembers = groupMembers;
        this.members = members;
        this.cycles = cycles;
        this.contributions = contributions;
        this.payouts = payouts;
        this.momo = momo;
        this.currentUser = currentUser;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        AppUser user = currentUser.require();
        Group group = new Group(request.name(), null, GroupType.ROTATING,
                request.contributionAmount(), parseFrequency(request.frequency()),
                request.maxMembers() == null ? 20 : request.maxMembers());
        group.setStartDate(request.startDate());
        group = groups.save(group);
        groupMembers.save(new GroupMember(group, member(user.getMsisdn(), user.getDisplayName()), GroupRole.ADMIN, 1));
        createInitialCycle(group);
        return groupResponse(group);
    }

    public GroupResponse join(UUID groupId, JoinGroupRequest request) {
        Group group = group(groupId);
        if (request.inviteCode() != null && !group.getInviteCode().equalsIgnoreCase(request.inviteCode())) {
            throw new BusinessException("INVALID_INVITE", "Invite code does not match this group");
        }
        return join(group, currentUser.require());
    }

    public GroupResponse addMember(UUID groupId, AddGroupMemberRequest request) {
        Group group = group(groupId);
        requireAdmin(group);
        return join(group, new JoinGroupRequest(null, request.msisdn(), request.displayName()));
    }

    public GroupResponse joinByInvite(JoinGroupRequest request) {
        if (request.inviteCode() == null || request.inviteCode().isBlank()) {
            throw new BusinessException("Invite code is required");
        }
        Group group = groups.findByInviteCode(request.inviteCode()).orElseThrow(
                () -> new ResourceNotFoundException("Group", "invite code", request.inviteCode()));
        return join(group, currentUser.require());
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> myGroups() {
        AppUser user = currentUser.require();
        return members.findByPhoneNumber(user.getMsisdn())
                .map(member -> groupMembers.findByMemberId(member.getId()).stream()
                        .map(GroupMember::getGroup)
                        .sorted(Comparator.comparing(Group::getCreatedAt).reversed())
                        .map(this::groupResponse)
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(UUID id) { Group group = group(id); requireMember(group); return groupResponse(group); }

    @Transactional(readOnly = true)
    public List<CycleResponse> cycles(UUID groupId) {
        requireMember(group(groupId));
        return cycles.findByGroupIdOrderByCycleNumberAsc(groupId).stream().map(this::cycleResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CycleHistoryResponse> history(UUID groupId) {
        requireMember(group(groupId));
        return cycles.findByGroupIdOrderByCycleNumberAsc(groupId).stream()
                .map(cycle -> new CycleHistoryResponse(cycle.getCycleNumber(), cycle.getEndDate(),
                        totalPaidFor(cycle), payoutRecipientFor(cycle),
                        cycle.getStatus() == CycleStatus.COMPLETED ? "closed" : "open"))
                .toList();
    }

    public CycleResponse createCycle(CreateCycleRequest request) {
        Group group = group(request.groupId());
        requireAdmin(group);
        Optional<Cycle> activeCycle = cycles.findByGroupIdAndStatus(group.getId(), CycleStatus.ACTIVE).stream().findFirst();
        if (activeCycle.isPresent()) return cycleResponse(activeCycle.get());
        int number = cycles.findByGroupIdOrderByCycleNumberAsc(group.getId()).stream()
                .mapToInt(Cycle::getCycleNumber).max().orElse(0) + 1;
        return cycleResponse(createCycle(group, number, request.dueDate()));
    }

    public ContributionStatusResponse contribute(TriggerContributionRequest request) {
        Cycle cycle = cycle(request.cycleId());
        requireMember(cycle.getGroup());
        Member member = members.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", request.memberId()));
        if (!member.getPhoneNumber().equals(currentUser.require().getMsisdn())) {
            throw new BusinessException("FORBIDDEN", "A contribution can only be created for the signed-in member");
        }
        if (!groupMembers.existsByGroupIdAndMemberId(cycle.getGroup().getId(), member.getId())) {
            throw new BusinessException("Member does not belong to the cycle group");
        }
        boolean exists = contributions.findByCycleIdAndMemberId(cycle.getId(), member.getId()).stream()
                .anyMatch(c -> c.getStatus() == ContributionStatus.SUCCESSFUL || c.getStatus() == ContributionStatus.PENDING);
        if (exists) throw new ConflictException("Contribution already exists for this member and cycle");

        Contribution contribution = contributions.save(new Contribution(cycle, member,
                cycle.getGroup().getContributionAmount(), "ZAR", ContributionStatus.PENDING,
                PaymentMethod.MOMO, null));
        MoMoResponse response = momo.requestToPay(new MoMoRequest(contribution.getAmount(),
                contribution.getCurrency(), member.getPhoneNumber(), contribution.getId().toString(), null, null));
        contribution.setPaymentReference(response.getReferenceId());
        return contributionResponse(contributions.save(contribution));
    }

    @Transactional(readOnly = true)
    public List<ContributionStatusResponse> contributions(UUID cycleId) {
        Cycle cycle = cycle(cycleId);
        requireMember(cycle.getGroup());
        return contributions.findByCycleId(cycleId).stream().map(this::contributionResponse).toList();
    }

    public ContributionStatusResponse refresh(UUID id) {
        Contribution contribution = contribution(id);
        requireContributionOwner(contribution);
        updateContribution(contribution);
        return contributionResponse(contribution);
    }

    @Transactional(readOnly = true)
    public ContributionStatusResponse contributionStatus(UUID id) { Contribution contribution = contribution(id); requireContributionOwner(contribution); return contributionResponse(contribution); }

    public TriggerPayoutResponse payout(TriggerPayoutRequest request) {
        Cycle cycle = cycle(request.cycleId());
        requireAdmin(cycle.getGroup());
        if (!payouts.findByCycleId(cycle.getId()).isEmpty()) throw new ConflictException("A payout already exists for this cycle");
        List<GroupMember> ordered = groupMembers.findByGroupId(cycle.getGroup().getId()).stream()
                .sorted(Comparator.comparing(GroupMember::getPayoutOrder)).toList();
        if (ordered.isEmpty()) throw new BusinessException("Cannot pay out an empty group");
        Member recipient = ordered.get((cycle.getCycleNumber() - 1) % ordered.size()).getMember();
        BigDecimal amount = totalPaidFor(cycle);
        if (amount.signum() <= 0) throw new BusinessException("No paid contributions are available for payout");

        Payout payout = payouts.save(new Payout(cycle, recipient, amount, "ZAR", PayoutStatus.PENDING,
                PayoutMethod.MOMO, null, LocalDate.now()));
        MoMoDisbursementResponse response = momo.disburse(new MoMoDisbursementRequest(amount, "ZAR",
                recipient.getPhoneNumber(), payout.getId().toString(), null, null));
        payout.setPayoutReference(response.getReferenceId());
        return payoutResponse(payouts.save(payout));
    }

    public void poll() {
        contributions.findByStatus(ContributionStatus.PENDING).forEach(this::updateContribution);
        payouts.findByStatus(PayoutStatus.PENDING).forEach(this::updatePayout);
    }

    @Transactional(readOnly = true)
    public TriggerPayoutResponse payoutStatus(UUID id) {
        Payout payout = payouts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payout", "id", id));
        requireMember(payout.getCycle().getGroup());
        return payoutResponse(payout);
    }

    private GroupResponse join(Group group, JoinGroupRequest request) {
        Member member = member(request.msisdn(), request.displayName());
        if (groupMembers.existsByGroupIdAndMemberId(group.getId(), member.getId())) throw new ConflictException("Member already belongs to this group");
        long count = groupMembers.countByGroupId(group.getId());
        if (count >= group.getMaxMembers()) throw new ConflictException("Group is at capacity");
        groupMembers.save(new GroupMember(group, member, GroupRole.MEMBER, (int) count + 1));
        ensureActiveCycle(group);
        refreshActiveCyclePool(group);
        return groupResponse(group);
    }

    private GroupResponse join(Group group, AppUser user) {
        return join(group, new JoinGroupRequest(null, user.getMsisdn(), user.getDisplayName()));
    }

    private void requireAdmin(Group group) {
        Member admin = requireMember(group);
        boolean isAdmin = groupMembers.findByGroupIdAndMemberId(group.getId(), admin.getId())
                .map(groupMember -> groupMember.getRole() == GroupRole.ADMIN)
                .orElse(false);
        if (!isAdmin) throw new BusinessException("ADMIN_REQUIRED", "Only the group creator can add members");
    }

    private Member requireMember(Group group) {
        String msisdn = currentUser.require().getMsisdn();
        Member member = members.findByPhoneNumber(msisdn)
                .orElseThrow(() -> new BusinessException("FORBIDDEN", "The signed-in user is not a member of this group"));
        if (!groupMembers.existsByGroupIdAndMemberId(group.getId(), member.getId())) {
            throw new BusinessException("FORBIDDEN", "The signed-in user is not a member of this group");
        }
        return member;
    }

    private void requireContributionOwner(Contribution contribution) {
        Member member = requireMember(contribution.getCycle().getGroup());
        if (!member.getId().equals(contribution.getMember().getId())) {
            throw new BusinessException("FORBIDDEN", "Only the contribution owner can view or refresh it");
        }
    }

    private void updateContribution(Contribution contribution) {
        if (contribution.getPaymentReference() == null) return;
        MoMoTransactionStatus status = momo.getTransactionStatus(contribution.getPaymentReference());
        if (status == MoMoTransactionStatus.SUCCESSFUL) {
            contribution.setStatus(ContributionStatus.SUCCESSFUL);
            contribution.setPaidAt(Instant.now());
        } else if (status == MoMoTransactionStatus.FAILED) contribution.setStatus(ContributionStatus.FAILED);
        contributions.save(contribution);
    }

    private void updatePayout(Payout payout) {
        if (payout.getPayoutReference() == null) return;
        MoMoTransactionStatus status = momo.getDisbursementStatus(payout.getPayoutReference());
        if (status == MoMoTransactionStatus.SUCCESSFUL) {
            payout.setStatus(PayoutStatus.PAID);
            payout.setPaidAt(Instant.now());
            payout.getCycle().setStatus(CycleStatus.COMPLETED);
        } else if (status == MoMoTransactionStatus.FAILED) payout.setStatus(PayoutStatus.FAILED);
        payouts.save(payout);
    }

    private void createInitialCycle(Group group) {
        if (cycles.findByGroupIdAndStatus(group.getId(), CycleStatus.ACTIVE).isEmpty()) {
            createCycle(group, 1, group.getStartDate());
        }
    }
    private void ensureActiveCycle(Group group) {
        if (cycles.findByGroupIdAndStatus(group.getId(), CycleStatus.ACTIVE).isEmpty()) {
            int number = cycles.findByGroupIdOrderByCycleNumberAsc(group.getId()).stream()
                    .mapToInt(Cycle::getCycleNumber).max().orElse(0) + 1;
            createCycle(group, number, null);
        }
    }
    private Cycle createCycle(Group group, int number, LocalDate requestedStart) {
        LocalDate start = requestedStart != null ? requestedStart : nextCycleStart(group, number);
        LocalDate end = group.getContributionFrequency() == ContributionFrequency.WEEKLY
                ? start.plusWeeks(1) : start.plusMonths(1);
        BigDecimal target = group.getContributionAmount()
                .multiply(BigDecimal.valueOf(groupMembers.countByGroupId(group.getId())));
        return cycles.save(new Cycle(group, number, start, end, target, CycleStatus.ACTIVE));
    }
    private void refreshActiveCyclePool(Group group) {
        BigDecimal target = group.getContributionAmount()
                .multiply(BigDecimal.valueOf(groupMembers.countByGroupId(group.getId())));
        cycles.findByGroupIdAndStatus(group.getId(), CycleStatus.ACTIVE)
                .forEach(cycle -> cycle.setTargetAmount(target));
    }
    private BigDecimal totalPaidFor(Cycle cycle) { return contributions.findByCycleId(cycle.getId()).stream().filter(c -> c.getStatus() == ContributionStatus.SUCCESSFUL).map(Contribution::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private String payoutRecipientFor(Cycle cycle) { return payouts.findByCycleId(cycle.getId()).stream().findFirst().map(p -> p.getMember().getDisplayName()).orElse(null); }
    private LocalDate nextCycleStart(Group group, int number) { return group.getContributionFrequency() == ContributionFrequency.WEEKLY ? group.getStartDate().plusWeeks(number - 1L) : group.getStartDate().plusMonths(number - 1L); }
    private Group group(UUID id) { return groups.findById(id).orElseThrow(() -> new ResourceNotFoundException("Group", "id", id)); }
    private Cycle cycle(UUID id) { return cycles.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", id)); }
    private Contribution contribution(UUID id) { return contributions.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", id)); }
    private Member member(String phone, String name) { return members.findByPhoneNumber(phone).orElseGet(() -> members.save(new Member(phone, name))); }
    private ContributionFrequency parseFrequency(String value) { try { return ContributionFrequency.valueOf(value.toUpperCase()); } catch (IllegalArgumentException e) { throw new BusinessException("frequency must be weekly or monthly"); } }
    private GroupResponse groupResponse(Group group) { List<MemberSummary> members = groupMembers.findByGroupId(group.getId()).stream().sorted(Comparator.comparing(GroupMember::getPayoutOrder)).map(m -> new MemberSummary(m.getMember().getId(), m.getMember().getDisplayName(), m.getMember().getPhoneNumber(), m.getPayoutOrder())).toList(); return new GroupResponse(group.getId(), group.getName(), group.getContributionAmount(), group.getContributionFrequency().name().toLowerCase(), group.getStartDate(), group.getInviteCode(), group.getMaxMembers(), group.getCreatedAt(), members); }
    private CycleResponse cycleResponse(Cycle cycle) { return new CycleResponse(cycle.getId(), cycle.getGroup().getId(), cycle.getCycleNumber(), cycle.getEndDate(), cycle.getStatus().name().toLowerCase()); }
    private ContributionStatusResponse contributionResponse(Contribution c) { String status = c.getStatus() == ContributionStatus.SUCCESSFUL ? "paid" : c.getStatus() == ContributionStatus.FAILED ? "failed" : "pending"; return new ContributionStatusResponse(c.getId(), c.getMember().getId(), c.getMember().getDisplayName(), c.getAmount(), status, c.getPaidAt(), c.getPaymentReference()); }
    private TriggerPayoutResponse payoutResponse(Payout p) { String status = p.getStatus() == PayoutStatus.PAID ? "paid" : p.getStatus() == PayoutStatus.FAILED ? "failed" : "pending"; return new TriggerPayoutResponse(p.getId(), p.getMember().getId(), p.getMember().getDisplayName(), p.getAmount(), status); }
}
