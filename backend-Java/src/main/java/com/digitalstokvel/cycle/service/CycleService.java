package com.digitalstokvel.cycle.service;

import com.digitalstokvel.common.exception.BusinessException;
import com.digitalstokvel.common.exception.ResourceNotFoundException;
import com.digitalstokvel.cycle.dto.CreateCycleRequest;
import com.digitalstokvel.cycle.dto.CycleResponse;
import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CycleService {

    private static final Logger log = LoggerFactory.getLogger(CycleService.class);

    private final CycleRepository cycleRepository;
    private final GroupRepository groupRepository;

    public CycleService(CycleRepository cycleRepository, GroupRepository groupRepository) {
        this.cycleRepository = cycleRepository;
        this.groupRepository = groupRepository;
    }

    public CycleResponse createCycle(CreateCycleRequest request) {
        log.info("Creating cycle for group: {}", request.getGroupId());

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + request.getGroupId()));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("INVALID_CYCLE_DATES", "Cycle start date cannot be after end date");
        }

        List<Cycle> existingCycles = cycleRepository.findByGroupId(group.getId());

        int cycleNumber = (request.getCycleNumber() != null && request.getCycleNumber() > 0)
                ? request.getCycleNumber()
                : existingCycles.size() + 1;

        if (cycleRepository.findByGroupIdAndCycleNumber(group.getId(), cycleNumber).isPresent()) {
            throw new BusinessException("DUPLICATE_CYCLE_NUMBER",
                    "Cycle number " + cycleNumber + " already exists for group " + group.getName());
        }

        BigDecimal targetAmount = request.getTargetAmount();
        if (targetAmount == null) {
            targetAmount = group.getContributionAmount().multiply(new BigDecimal(group.getMaxMembers()));
        }

        CycleStatus status = request.getStatus() != null ? request.getStatus() : CycleStatus.ACTIVE;

        Cycle cycle = new Cycle(
                group,
                cycleNumber,
                request.getStartDate(),
                request.getEndDate(),
                targetAmount,
                status
        );

        Cycle savedCycle = cycleRepository.save(cycle);
        log.info("Successfully created cycle {} for group {}", savedCycle.getId(), group.getId());
        return CycleResponse.fromEntity(savedCycle);
    }

    @Transactional(readOnly = true)
    public CycleResponse getCycleById(UUID id) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with id: " + id));
        return CycleResponse.fromEntity(cycle);
    }

    @Transactional(readOnly = true)
    public List<CycleResponse> getCyclesByGroupId(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }
        return cycleRepository.findByGroupId(groupId).stream()
                .map(CycleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CycleResponse> getAllCycles() {
        return cycleRepository.findAll().stream()
                .map(CycleResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
