package com.digitalstokvel.payout.controller;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.payout.dto.CreatePayoutRequest;
import com.digitalstokvel.payout.dto.PayoutResponse;
import com.digitalstokvel.payout.service.PayoutService;
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
@RequestMapping({"/payouts", "/api/v1/payouts"})
public class PayoutController {

    private final PayoutService payoutService;

    public PayoutController(PayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PayoutResponse>> createPayout(@Valid @RequestBody CreatePayoutRequest request) {
        PayoutResponse response = payoutService.createPayout(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payout created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PayoutResponse>> getPayoutById(@PathVariable UUID id) {
        PayoutResponse response = payoutService.getPayoutById(id);
        return ResponseEntity.ok(ApiResponse.ok("Payout retrieved successfully", response));
    }

    @GetMapping("/cycle/{cycleId}")
    public ResponseEntity<ApiResponse<List<PayoutResponse>>> getPayoutsByCycleId(@PathVariable UUID cycleId) {
        List<PayoutResponse> response = payoutService.getPayoutsByCycleId(cycleId);
        return ResponseEntity.ok(ApiResponse.ok("Cycle payouts retrieved successfully", response));
    }
}
