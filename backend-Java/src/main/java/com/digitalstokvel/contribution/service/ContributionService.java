package com.digitalstokvel.contribution.service;

import com.digitalstokvel.common.exception.BusinessException;
import com.digitalstokvel.common.exception.ResourceNotFoundException;
import com.digitalstokvel.contribution.dto.CreateContributionRequest;
import com.digitalstokvel.contribution.dto.ContributionResponse;
import com.digitalstokvel.contribution.entity.Contribution;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import com.digitalstokvel.contribution.repository.ContributionRepository;
import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.repository.GroupMemberRepository;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.digitalstokvel.momo.MomoCollectionsClient;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContributionService {

    private static final Logger log = LoggerFactory.getLogger(ContributionService.class);

    private final ContributionRepository contributionRepository;
    private final CycleRepository cycleRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MomoCollectionsClient momoCollectionsClient;

    public ContributionService(ContributionRepository contributionRepository,
                               CycleRepository cycleRepository,
                               MemberRepository memberRepository,
                               GroupMemberRepository groupMemberRepository,
                               MomoCollectionsClient momoCollectionsClient) {
        this.contributionRepository = contributionRepository;
        this.cycleRepository = cycleRepository;
        this.memberRepository = memberRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.momoCollectionsClient = momoCollectionsClient;
    }

    public ContributionResponse recordContribution(CreateContributionRequest request) {
        log.info("Recording contribution of {} for member {} in cycle {}",
                request.getAmount(), request.getMemberId(), request.getCycleId());

        Cycle cycle = cycleRepository.findById(request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with id: " + request.getCycleId()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        if (!groupMemberRepository.existsByGroupIdAndMemberId(cycle.getGroup().getId(), member.getId())) {
            throw new BusinessException("MEMBER_NOT_IN_GROUP",
                    "Member is not a member of group " + cycle.getGroup().getName() + " for this cycle");
        }

        ContributionStatus initialStatus = request.getStatus() != null ? request.getStatus() : ContributionStatus.PENDING;

        Contribution contribution = new Contribution(
                cycle,
                member,
                request.getAmount(),
                request.getCurrency() != null ? request.getCurrency() : "ZAR",
                initialStatus,
                request.getPaymentMethod(),
                request.getPaymentReference()
        );

        Contribution savedContribution = contributionRepository.save(contribution);

        // If payment method is MOMO, initiate real MTN MoMo Request-To-Pay
        if (request.getPaymentMethod() == PaymentMethod.MOMO) {
            log.info("Triggering MTN MoMo Request-To-Pay for contribution {}", savedContribution.getId());

            MoMoRequest momoRequest = new MoMoRequest(
                    request.getAmount(),
                    request.getCurrency() != null ? request.getCurrency() : "ZAR",
                    member.getPhoneNumber(),
                    savedContribution.getId().toString(),
                    "Contribution for " + cycle.getGroup().getName(),
                    "Cycle " + cycle.getCycleNumber()
            );

            MoMoResponse momoResponse = momoCollectionsClient.requestToPay(momoRequest);

            if (momoResponse != null && momoResponse.getReferenceId() != null) {
                savedContribution.setPaymentReference(momoResponse.getReferenceId());
            }

            // Note: 202 Accepted != SUCCESSFUL. Status remains PENDING until callback/verification.
            savedContribution.setStatus(ContributionStatus.PENDING);
            savedContribution = contributionRepository.save(savedContribution);
        } else if (savedContribution.getStatus() == ContributionStatus.SUCCESSFUL) {
            if (savedContribution.getPaidAt() == null) {
                savedContribution.setPaidAt(Instant.now());
            }
            savedContribution = contributionRepository.save(savedContribution);
            updateCycleProgressOnContribution(cycle);
        }

        log.info("Successfully recorded contribution {} with payment reference {} and status {}",
                savedContribution.getId(), savedContribution.getPaymentReference(), savedContribution.getStatus());
        return ContributionResponse.fromEntity(savedContribution);
    }

    public ContributionResponse syncContributionStatus(UUID contributionId) {
        log.info("Synchronizing MoMo transaction status for contribution {}", contributionId);

        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new ResourceNotFoundException("Contribution not found with id: " + contributionId));

        if (contribution.getPaymentReference() != null && !contribution.getPaymentReference().isBlank()) {
            MoMoTransactionStatus momoStatus = momoCollectionsClient.getCollectionStatus(contribution.getPaymentReference());

            if (momoStatus == MoMoTransactionStatus.SUCCESSFUL) {
                contribution.setStatus(ContributionStatus.SUCCESSFUL);
                if (contribution.getPaidAt() == null) {
                    contribution.setPaidAt(Instant.now());
                }
                log.info("Contribution {} status updated to SUCCESSFUL", contribution.getId());
                updateCycleProgressOnContribution(contribution.getCycle());
            } else if (momoStatus == MoMoTransactionStatus.FAILED) {
                contribution.setStatus(ContributionStatus.FAILED);
                log.info("Contribution {} status updated to FAILED", contribution.getId());
            }

            contribution = contributionRepository.save(contribution);
        }

        return ContributionResponse.fromEntity(contribution);
    }

    public void updateContributionStatusFromWebhook(String referenceOrId, ContributionStatus newStatus) {
        log.info("Processing webhook status update for reference/id {}: {}", referenceOrId, newStatus);

        Optional<Contribution> optionalContribution = contributionRepository.findByPaymentReference(referenceOrId);
        if (optionalContribution.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(referenceOrId);
                optionalContribution = contributionRepository.findById(uuid);
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (optionalContribution.isPresent()) {
            Contribution contribution = optionalContribution.get();
            contribution.setStatus(newStatus);
            if (newStatus == ContributionStatus.SUCCESSFUL) {
                if (contribution.getPaidAt() == null) {
                    contribution.setPaidAt(Instant.now());
                }
                updateCycleProgressOnContribution(contribution.getCycle());
            }
            contributionRepository.save(contribution);
            log.info("Successfully updated contribution {} status to {} via webhook", contribution.getId(), newStatus);
        } else {
            log.warn("No contribution found for reference/id {} during webhook processing", referenceOrId);
        }
    }

    private void updateCycleProgressOnContribution(Cycle cycle) {
        if (cycle == null) return;
        List<Contribution> cycleContributions = contributionRepository.findByCycleId(cycle.getId());

        BigDecimal totalCollected = cycleContributions.stream()
                .filter(c -> c.getStatus() == ContributionStatus.SUCCESSFUL)
                .map(Contribution::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Cycle {} progress: total collected {} / target {}",
                cycle.getId(), totalCollected, cycle.getTargetAmount());

        if (totalCollected.compareTo(cycle.getTargetAmount()) >= 0 && cycle.getStatus() == CycleStatus.ACTIVE) {
            cycle.setStatus(CycleStatus.COMPLETED);
            cycleRepository.save(cycle);
            log.info("Cycle {} target amount met! Status set to COMPLETED", cycle.getId());
        }
    }

    @Transactional(readOnly = true)
    public ContributionResponse getContributionById(UUID id) {
        Contribution contribution = contributionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contribution not found with id: " + id));
        return ContributionResponse.fromEntity(contribution);
    }

    @Transactional(readOnly = true)
    public List<ContributionResponse> getContributionsByCycleId(UUID cycleId) {
        if (!cycleRepository.existsById(cycleId)) {
            throw new ResourceNotFoundException("Cycle not found with id: " + cycleId);
        }
        return contributionRepository.findByCycleId(cycleId).stream()
                .map(ContributionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContributionResponse> getContributionsByMemberId(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with id: " + memberId);
        }
        return contributionRepository.findByMemberId(memberId).stream()
                .map(ContributionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
