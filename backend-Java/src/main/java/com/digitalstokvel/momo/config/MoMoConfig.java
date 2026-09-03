package com.digitalstokvel.momo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Configuration
public class MoMoConfig {

    @Value("${momo.base-url}")
    private String baseUrl;

    @Value("${momo.target-environment}")
    private String targetEnvironment;

    @Value("${momo.collection.subscription-key}")
    private String collectionSubscriptionKey;

    @Value("${momo.collection.api-user}")
    private String collectionApiUser;

    @Value("${momo.collection.api-key}")
    private String collectionApiKey;

    @Value("${momo.disbursement.subscription-key}")
    private String disbursementSubscriptionKey;

    @Value("${momo.disbursement.api-user}")
    private String disbursementApiUser;

    @Value("${momo.disbursement.api-key}")
    private String disbursementApiKey;

    @Value("${momo.mock-mode}")
    private boolean mockMode;

    @Value("${momo.currency}")
    private String currency;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTargetEnvironment() {
        return targetEnvironment;
    }

    public String getCollectionSubscriptionKey() {
        return collectionSubscriptionKey;
    }

    public String getDisbursementSubscriptionKey() {
        return disbursementSubscriptionKey;
    }
    public boolean isMockMode() { return mockMode; }
    public String getCurrency() { return currency; }

    public String getCollectionBasicAuth() {
        String credentials = collectionApiUser + ":" + collectionApiKey;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public String getDisbursementBasicAuth() {
        String credentials = disbursementApiUser + ":" + disbursementApiKey;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Bean
    public RestClient momoRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
