package com.digitalstokvel.momo.service;

import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import com.digitalstokvel.momo.config.MoMoConfig;
import com.digitalstokvel.momo.MomoPhoneUtils;
import java.time.Instant;
import java.util.Map;

import java.util.UUID;

@Service
public class MoMoServiceImpl implements MoMoService {

    private static final Logger log = LoggerFactory.getLogger(MoMoServiceImpl.class);
    private final RestClient client;
    private final MoMoConfig config;
    private Token collectionToken;
    private Token disbursementToken;

    public MoMoServiceImpl(RestClient momoRestClient, MoMoConfig config) {
        this.client = momoRestClient;
        this.config = config;
    }

    @Override
    public MoMoResponse requestToPay(MoMoRequest request) {
        String referenceId = UUID.randomUUID().toString();
        log.info("Initiating MoMo RequestToPay: referenceId={}, payer={}, amount={} {}",
                referenceId, request.getPayerPhoneNumber(), request.getAmount(), request.getCurrency());

        if (!config.isMockMode()) initiate("collection", referenceId, Map.of(
                "amount", request.getAmount().stripTrailingZeros().toPlainString(), "currency", config.getCurrency(),
                "externalId", request.getExternalId(), "payer", Map.of("partyIdType", "MSISDN", "partyId", MomoPhoneUtils.normalize(request.getPayerPhoneNumber())),
                "payerMessage", "Stokvel contribution", "payeeNote", "Stokvel contribution"));
        return MoMoResponse.pending(referenceId, "Payment request accepted for processing");
    }

    @Override
    public MoMoDisbursementResponse disburse(MoMoDisbursementRequest request) {
        String referenceId = UUID.randomUUID().toString();
        log.info("Initiating MoMo Disbursement: referenceId={}, payee={}, amount={} {}",
                referenceId, request.getPayeePhoneNumber(), request.getAmount(), request.getCurrency());

        if (!config.isMockMode()) initiate("disbursement", referenceId, Map.of(
                "amount", request.getAmount().stripTrailingZeros().toPlainString(), "currency", config.getCurrency(),
                "externalId", request.getExternalId(), "payee", Map.of("partyIdType", "MSISDN", "partyId", MomoPhoneUtils.normalize(request.getPayeePhoneNumber())),
                "payerMessage", "Stokvel payout", "payeeNote", "Stokvel payout"));
        return MoMoDisbursementResponse.pending(referenceId, "Disbursement request accepted for processing");
    }

    @Override
    public MoMoTransactionStatus getTransactionStatus(String referenceId) {
        log.info("Querying MoMo transaction status: referenceId={}", referenceId);
        return config.isMockMode() ? MoMoTransactionStatus.SUCCESSFUL : check("collection", referenceId);
    }

    @Override
    public MoMoTransactionStatus getDisbursementStatus(String referenceId) {
        return config.isMockMode() ? MoMoTransactionStatus.SUCCESSFUL : check("disbursement", referenceId);
    }

    private void initiate(String product, String reference, Map<String, Object> body) {
        String path = product.equals("collection") ? "/collection/v1_0/requesttopay" : "/disbursement/v1_0/transfer";
        executeWithTokenRetry(product, token -> client.post().uri(path).header("X-Reference-Id", reference)
                .header("X-Target-Environment", config.getTargetEnvironment()).header("Ocp-Apim-Subscription-Key", subscription(product))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().toBodilessEntity());
    }

    @SuppressWarnings("unchecked")
    private MoMoTransactionStatus check(String product, String reference) {
        String path = product.equals("collection") ? "/collection/v1_0/requesttopay/" : "/disbursement/v1_0/transfer/";
        Map<String, Object> response = executeWithTokenRetry(product, token -> client.get().uri(path + reference)
                .header("X-Target-Environment", config.getTargetEnvironment()).header("Ocp-Apim-Subscription-Key", subscription(product))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve().body(Map.class));
        String status = String.valueOf(response.get("status"));
        return "SUCCESSFUL".equalsIgnoreCase(status) ? MoMoTransactionStatus.SUCCESSFUL : "FAILED".equalsIgnoreCase(status) ? MoMoTransactionStatus.FAILED : MoMoTransactionStatus.PENDING;
    }

    private <T> T executeWithTokenRetry(String product, java.util.function.Function<String, T> call) {
        try { return call.apply(token(product)); }
        catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 401) { clear(product); return call.apply(token(product)); }
            if (e.getStatusCode().value() >= 500) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY, "MoMo service is unavailable", e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized String token(String product) {
        Token cached = product.equals("collection") ? collectionToken : disbursementToken;
        if (cached != null && cached.expiresAt().isAfter(Instant.now().plusSeconds(60))) return cached.value();
        Map<String, Object> response = client.post().uri("/" + product + "/token/")
                .header("Ocp-Apim-Subscription-Key", subscription(product)).header("X-Target-Environment", config.getTargetEnvironment())
                .header(HttpHeaders.AUTHORIZATION, product.equals("collection") ? config.getCollectionBasicAuth() : config.getDisbursementBasicAuth())
                .retrieve().body(Map.class);
        Token fresh = new Token(String.valueOf(response.get("access_token")), Instant.now().plusSeconds(Long.parseLong(String.valueOf(response.getOrDefault("expires_in", 3600)))));
        if (product.equals("collection")) collectionToken = fresh; else disbursementToken = fresh;
        return fresh.value();
    }
    private String subscription(String product) { return product.equals("collection") ? config.getCollectionSubscriptionKey() : config.getDisbursementSubscriptionKey(); }
    private void clear(String product) { if (product.equals("collection")) collectionToken = null; else disbursementToken = null; }
    private record Token(String value, Instant expiresAt) { }
}
