package com.digitalstokvel.payout.repository;

import com.digitalstokvel.payout.entity.Payout;
import com.digitalstokvel.payout.entity.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, UUID> {

    List<Payout> findByCycleId(UUID cycleId);

    List<Payout> findByMemberId(UUID memberId);

    List<Payout> findByCycleIdAndMemberId(UUID cycleId, UUID memberId);

    List<Payout> findByStatus(PayoutStatus status);

    Optional<Payout> findByPayoutReference(String payoutReference);
}
