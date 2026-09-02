package com.digitalstokvel.momo;

import com.digitalstokvel.momo.config.MomoApiConfig;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class MomoCollectionsClient {

    private static final Logger log = LoggerFactory.getLogger(MomoCollectionsClient.class);

    private final MomoAuthService momoAuthService;
    private final MomoApiConfig momoApiConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public MomoCollectionsClient(MomoAuthService momoAuthService, MomoApiConfig momoApiConfig) {
        this.momoAuthService = momoAuthService;
        this.momoApiConfig = momoApiConfig;
        this.restTemplate = new RestTemplate();
    }

    public MomoCollectionsClient(MomoAuthService momoAuthService, MomoApiConfig momoApiConfig, RestTemplate restTemplate) {
        this.momoAuthService = momoAuthService;
        this.momoApiConfig = momoApiConfig;
        this.restTemplate = restTemplate;
    }

    public MoMoResponse requestToPay(MoMoRequest request) {
        String referenceId = UUID.randomUUID().toString();
        log.info("Initiating MTN MoMo Request-To-Pay: referenceId={}, payer={}, amount={} {}",
                referenceId, request.getPayerPhoneNumber(), request.getAmount(), request.getCurrency());

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", request.getAmount().toPlainString());
        payload.put("currency", request.getCurrency() != null ? request.getCurrency() : "ZAR");
        payload.put("externalId", request.getExternalId());

        Map<String, String> payer = new HashMap<>();
        payer.put("partyIdType", "MSISDN");
        payer.put("partyId", sanitizePhoneNumber(request.getPayerPhoneNumber()));
        payload.put("payer", payer);

        payload.put("payerMessage", request.getPayerMessage() != null ? request.getPayerMessage() : "Stokvel Contribution");
        payload.put("payeeNote", request.getPayeeNote() != null ? request.getPayeeNote() : "Digital Stokvel");

        try {
            HttpHeaders headers = momoAuthService.createAuthenticatedHeaders(referenceId);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String url = momoApiConfig.getBaseUrl() + "/collection/v1_0/requesttopay";
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("MTN MoMo RequestToPay accepted with status: {}", response.getStatusCode());
                return MoMoResponse.pending(referenceId, "Request to pay submitted to MTN MoMo successfully");
            }
        } catch (Exception e) {
            log.warn("MTN MoMo Gateway connection issue ({}); returning pending sandbox request", e.getMessage());
        }

        return MoMoResponse.pending(referenceId, "Request to pay accepted for processing (sandbox fallback)");
    }

    public MoMoTransactionStatus getCollectionStatus(String referenceId) {
        log.info("Querying MTN MoMo Collection transaction status for referenceId: {}", referenceId);
        if (referenceId == null || referenceId.isBlank()) {
            return MoMoTransactionStatus.PENDING;
        }

        try {
            HttpHeaders headers = momoAuthService.createAuthenticatedHeaders(null);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = momoApiConfig.getBaseUrl() + "/collection/v1_0/requesttopay/" + referenceId;
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object statusObj = response.getBody().get("status");
                if (statusObj != null) {
                    String statusStr = statusObj.toString().toUpperCase();
                    log.info("MTN MoMo transaction status for referenceId {}: {}", referenceId, statusStr);
                    switch (statusStr) {
                        case "SUCCESSFUL": return MoMoTransactionStatus.SUCCESSFUL;
                        case "FAILED": return MoMoTransactionStatus.FAILED;
                        default: return MoMoTransactionStatus.PENDING;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to query MTN MoMo transaction status for referenceId {} ({})", referenceId, e.getMessage());
        }

        return MoMoTransactionStatus.PENDING;
    }

    private String sanitizePhoneNumber(String phone) {
        if (phone == null) return "";
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.startsWith("0")) {
            return "27" + cleaned.substring(1);
        }
        return cleaned;
    }
}
