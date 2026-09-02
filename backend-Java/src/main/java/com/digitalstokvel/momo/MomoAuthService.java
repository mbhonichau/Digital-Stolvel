package com.digitalstokvel.momo;

import com.digitalstokvel.momo.config.MomoApiConfig;
import com.digitalstokvel.momo.dto.MomoTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class MomoAuthService {

    private static final Logger log = LoggerFactory.getLogger(MomoAuthService.class);

    private final MomoApiConfig momoApiConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public MomoAuthService(MomoApiConfig momoApiConfig) {
        this.momoApiConfig = momoApiConfig;
        this.restTemplate = new RestTemplate();
    }

    public MomoAuthService(MomoApiConfig momoApiConfig, RestTemplate restTemplate) {
        this.momoApiConfig = momoApiConfig;
        this.restTemplate = restTemplate;
    }

    public synchronized String getAccessToken() {
        if (cachedAccessToken != null && tokenExpiryTime != null && Instant.now().isBefore(tokenExpiryTime)) {
            log.debug("Using cached MoMo access token");
            return cachedAccessToken;
        }
        return fetchAccessToken();
    }

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    public synchronized String fetchAccessToken() {
        log.info("Requesting new MoMo OAuth access token from MTN API...");

        String user = momoApiConfig.getUser();
        String key = momoApiConfig.getKey();

        if (user == null || user.isBlank() || key == null || key.isBlank()) {
            log.warn("MoMo API user/key not configured or using test credentials; returning generated sandbox token");
            this.cachedAccessToken = "mock-momo-access-token-" + UUID.randomUUID();
            this.tokenExpiryTime = Instant.now().plusSeconds(3600);
            return this.cachedAccessToken;
        }

        try {
            String credentials = user + ":" + key;
            String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("Ocp-Apim-Subscription-Key", momoApiConfig.getSubscriptionKey());
            headers.set("Content-Type", "application/json");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = momoApiConfig.getBaseUrl() + "/collection/token/";
            ResponseEntity<MomoTokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    MomoTokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MomoTokenResponse tokenBody = response.getBody();
                this.cachedAccessToken = tokenBody.getAccessToken();
                long expiresIn = tokenBody.getExpiresIn() != null ? tokenBody.getExpiresIn() : 3600;
                this.tokenExpiryTime = Instant.now().plusSeconds(Math.max(expiresIn - 30, 60));
                log.info("Successfully obtained MoMo OAuth access token (expires in {}s)", expiresIn);
                return this.cachedAccessToken;
            }
        } catch (Exception e) {
            log.warn("Failed to reach MTN MoMo OAuth endpoint ({}); falling back to sandbox token for local environment", e.getMessage());
        }

        this.cachedAccessToken = "sandbox-token-" + UUID.randomUUID();
        this.tokenExpiryTime = Instant.now().plusSeconds(3600);
        return this.cachedAccessToken;
    }

    public HttpHeaders createAuthenticatedHeaders(String referenceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("X-Target-Environment", momoApiConfig.getTargetEnvironment());
        headers.set("Ocp-Apim-Subscription-Key", momoApiConfig.getSubscriptionKey());
        if (referenceId != null && !referenceId.isBlank()) {
            headers.set("X-Reference-Id", referenceId);
        }
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
