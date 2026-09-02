package com.digitalstokvel.contribution.controller;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.contribution.dto.CreateContributionRequest;
import com.digitalstokvel.contribution.dto.ContributionResponse;
import com.digitalstokvel.contribution.service.ContributionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping({"/contributions", "/api/v1/contributions"})
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContributionResponse>> recordContribution(@Valid @RequestBody CreateContributionRequest request) {
        ContributionResponse response = contributionService.recordContribution(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Contribution recorded successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContributionResponse>> getContributionById(@PathVariable UUID id) {
        ContributionResponse response = contributionService.getContributionById(id);
        return ResponseEntity.ok(ApiResponse.ok("Contribution retrieved successfully", response));
    }

    @PostMapping("/{id}/sync-status")
    public ResponseEntity<ApiResponse<ContributionResponse>> syncContributionStatus(@PathVariable UUID id) {
        ContributionResponse response = contributionService.syncContributionStatus(id);
        return ResponseEntity.ok(ApiResponse.ok("Contribution status synchronized successfully", response));
    }
}
