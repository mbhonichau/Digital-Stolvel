package com.digitalstokvel.cycle.controller;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.contribution.dto.ContributionResponse;
import com.digitalstokvel.contribution.service.ContributionService;
import com.digitalstokvel.cycle.dto.CreateCycleRequest;
import com.digitalstokvel.cycle.dto.CycleResponse;
import com.digitalstokvel.cycle.service.CycleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/cycles", "/api/v1/cycles"})
public class CycleController {

    private final CycleService cycleService;
    private final ContributionService contributionService;

    public CycleController(CycleService cycleService, ContributionService contributionService) {
        this.cycleService = cycleService;
        this.contributionService = contributionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CycleResponse>> createCycle(@Valid @RequestBody CreateCycleRequest request) {
        CycleResponse response = cycleService.createCycle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cycle created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CycleResponse>> getCycleById(@PathVariable UUID id) {
        CycleResponse response = cycleService.getCycleById(id);
        return ResponseEntity.ok(ApiResponse.ok("Cycle retrieved successfully", response));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<CycleResponse>>> getCyclesByGroupId(@PathVariable UUID groupId) {
        List<CycleResponse> response = cycleService.getCyclesByGroupId(groupId);
        return ResponseEntity.ok(ApiResponse.ok("Cycles retrieved successfully", response));
    }

    @GetMapping("/{id}/contributions")
    public ResponseEntity<ApiResponse<List<ContributionResponse>>> getContributionsForCycle(@PathVariable UUID id) {
        List<ContributionResponse> response = contributionService.getContributionsByCycleId(id);
        return ResponseEntity.ok(ApiResponse.ok("Cycle contributions retrieved successfully", response));
    }
}
