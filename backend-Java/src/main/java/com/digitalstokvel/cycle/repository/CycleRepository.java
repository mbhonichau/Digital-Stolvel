package com.digitalstokvel.cycle.repository;

import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, UUID> {

    List<Cycle> findByGroupId(UUID groupId);

    Optional<Cycle> findByGroupIdAndCycleNumber(UUID groupId, Integer cycleNumber);

    List<Cycle> findByGroupIdAndStatus(UUID groupId, CycleStatus status);

    List<Cycle> findByStatus(CycleStatus status);

    List<Cycle> findByGroupIdOrderByCycleNumberAsc(UUID groupId);
}
