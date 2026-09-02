package com.digitalstokvel.contribution.repository;

import com.digitalstokvel.contribution.entity.Contribution;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, UUID> {

    List<Contribution> findByCycleId(UUID cycleId);

    List<Contribution> findByMemberId(UUID memberId);

    List<Contribution> findByCycleIdAndMemberId(UUID cycleId, UUID memberId);

    List<Contribution> findByStatus(ContributionStatus status);

    Optional<Contribution> findByPaymentReference(String paymentReference);
}
