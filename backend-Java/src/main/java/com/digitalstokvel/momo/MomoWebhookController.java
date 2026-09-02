package com.digitalstokvel.momo;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.service.ContributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({"/webhooks/momo", "/momo/callback", "/api/v1/webhooks/momo", "/api/v1/momo/callback"})
public class MomoWebhookController {

    private static final Logger log = LoggerFactory.getLogger(MomoWebhookController.class);

    private final ContributionService contributionService;

    public MomoWebhookController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping({"/collections", "/collection"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleCollectionCallback(@RequestBody Map<String, Object> payload) {
        log.info("Received MTN MoMo Collection Webhook Callback: {}", payload);

        Object refObj = payload.get("externalId");
        if (refObj == null) {
            refObj = payload.get("financialTransactionId");
        }

        Object statusObj = payload.get("status");
        if (refObj != null && statusObj != null) {
            String statusStr = statusObj.toString().toUpperCase();
            ContributionStatus newStatus;
            if ("SUCCESSFUL".equals(statusStr)) {
                newStatus = ContributionStatus.SUCCESSFUL;
            } else if ("FAILED".equals(statusStr)) {
                newStatus = ContributionStatus.FAILED;
            } else {
                newStatus = ContributionStatus.PENDING;
            }

            contributionService.updateContributionStatusFromWebhook(refObj.toString(), newStatus);
        }

        return ResponseEntity.ok(ApiResponse.ok("Collection callback processed successfully", payload));
    }

    @PostMapping({"/disbursement", "/disbursements"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDisbursementCallback(@RequestBody Map<String, Object> payload) {
        log.info("Received MTN MoMo Disbursement Webhook Callback: {}", payload);
        return ResponseEntity.ok(ApiResponse.ok("Disbursement callback processed successfully", payload));
    }
}
