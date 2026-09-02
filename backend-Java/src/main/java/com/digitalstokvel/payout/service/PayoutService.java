package com.digitalstokvel.payout.service;

import com.digitalstokvel.common.exception.BusinessException;
import com.digitalstokvel.common.exception.ResourceNotFoundException;
import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.repository.GroupMemberRepository;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.digitalstokvel.momo.MomoDisbursementClient;
import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.payout.dto.CreatePayoutRequest;
import com.digitalstokvel.payout.dto.PayoutResponse;
import com.digitalstokvel.payout.entity.Payout;
import com.digitalstokvel.payout.entity.PayoutMethod;
import com.digitalstokvel.payout.entity.PayoutStatus;
import com.digitalstokvel.payout.repository.PayoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PayoutService {

    private static final Logger log = LoggerFactory.getLogger(PayoutService.class);

    private final PayoutRepository payoutRepository;
    private final CycleRepository cycleRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MomoDisbursementClient momoDisbursementClient;

    public PayoutService(PayoutRepository payoutRepository,
                         CycleRepository cycleRepository,
                         MemberRepository memberRepository,
                         GroupMemberRepository groupMemberRepository,
                         MomoDisbursementClient momoDisbursementClient) {
        this.payoutRepository = payoutRepository;
        this.cycleRepository = cycleRepository;
        this.memberRepository = memberRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.momoDisbursementClient = momoDisbursementClient;
    }

    public PayoutResponse createPayout(CreatePayoutRequest request) {
        log.info("Creating payout of {} for member {} in cycle {}",
                request.getAmount(), request.getMemberId(), request.getCycleId());

        Cycle cycle = cycleRepository.findById(request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with id: " + request.getCycleId()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        if (!groupMemberRepository.existsByGroupIdAndMemberId(cycle.getGroup().getId(), member.getId())) {
            throw new BusinessException("MEMBER_NOT_IN_GROUP",
                    "Member is not a member of group " + cycle.getGroup().getName() + " for this cycle");
        }

        LocalDate scheduledDate = request.getScheduledDate() != null ? request.getScheduledDate() : LocalDate.now();
        PayoutStatus initialStatus = request.getStatus() != null ? request.getStatus() : PayoutStatus.PENDING;

        Payout payout = new Payout(
                cycle,
                member,
                request.getAmount(),
                request.getCurrency() != null ? request.getCurrency() : "ZAR",
                initialStatus,
                request.getPayoutMethod(),
                request.getPayoutReference(),
                scheduledDate
        );

        Payout savedPayout = payoutRepository.save(payout);

        if (request.getPayoutMethod() == PayoutMethod.MOMO) {
            log.info("Triggering MTN MoMo Disbursement transfer for payout {}", savedPayout.getId());

            MoMoDisbursementRequest momoRequest = new MoMoDisbursementRequest(
                    request.getAmount(),
                    request.getCurrency() != null ? request.getCurrency() : "ZAR",
                    member.getPhoneNumber(),
                    savedPayout.getId().toString(),
                    "Stokvel Payout for " + cycle.getGroup().getName(),
                    "Cycle " + cycle.getCycleNumber()
            );

            MoMoDisbursementResponse momoResponse = momoDisbursementClient.transfer(momoRequest);

            if (momoResponse != null && momoResponse.getReferenceId() != null) {
                savedPayout.setPayoutReference(momoResponse.getReferenceId());
            }

            savedPayout.setStatus(PayoutStatus.PENDING);
            savedPayout = payoutRepository.save(savedPayout);
        } else if (savedPayout.getStatus() == PayoutStatus.PAID && savedPayout.getPaidAt() == null) {
            savedPayout.setPaidAt(Instant.now());
            savedPayout = payoutRepository.save(savedPayout);
        }

        log.info("Successfully created payout {} with reference {} and status {}",
                savedPayout.getId(), savedPayout.getPayoutReference(), savedPayout.getStatus());
        return PayoutResponse.fromEntity(savedPayout);
    }

    @Transactional(readOnly = true)
    public PayoutResponse getPayoutById(UUID id) {
        Payout payout = payoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payout not found with id: " + id));
        return PayoutResponse.fromEntity(payout);
    }

    @Transactional(readOnly = true)
    public List<PayoutResponse> getPayoutsByCycleId(UUID cycleId) {
        if (!cycleRepository.existsById(cycleId)) {
            throw new ResourceNotFoundException("Cycle not found with id: " + cycleId);
        }
        return payoutRepository.findByCycleId(cycleId).stream()
                .map(PayoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayoutResponse> getPayoutsByMemberId(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        return payoutRepository.findByMemberId(memberId).stream()
                .map(PayoutResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
