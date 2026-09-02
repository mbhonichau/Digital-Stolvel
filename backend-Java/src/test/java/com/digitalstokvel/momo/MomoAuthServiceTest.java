package com.digitalstokvel.momo;

import com.digitalstokvel.momo.config.MomoApiConfig;
import com.digitalstokvel.momo.dto.MoMoDisbursementRequest;
import com.digitalstokvel.momo.dto.MoMoDisbursementResponse;
import com.digitalstokvel.momo.dto.MoMoRequest;
import com.digitalstokvel.momo.dto.MoMoResponse;
import com.digitalstokvel.momo.entity.MoMoTransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class MomoAuthServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MomoApiConfig momoApiConfig;

    @Autowired
    private MomoAuthService momoAuthService;

    @Autowired
    private MomoCollectionsClient momoCollectionsClient;

    @Autowired
    private MomoDisbursementClient momoDisbursementClient;

    @Test
    @DisplayName("Should load MomoApiConfig with server-side environment properties")
    void testMomoApiConfigLoading() {
        assertNotNull(momoApiConfig.getBaseUrl());
        assertNotNull(momoApiConfig.getTargetEnvironment());
        assertEquals("mtnsouthafrica", momoApiConfig.getTargetEnvironment());
    }

    @Test
    @DisplayName("Should generate and cache MoMo access token")
    void testTokenGenerationAndCaching() {
        String token1 = momoAuthService.getAccessToken();
        assertNotNull(token1);
        String token2 = momoAuthService.getAccessToken();
        assertSame(token1, token2, "Token should be cached");

        HttpHeaders headers = momoAuthService.createAuthenticatedHeaders("test-ref-id");
        assertEquals("Bearer " + token1, headers.getFirst("Authorization"));
        assertEquals("mtnsouthafrica", headers.getFirst("X-Target-Environment"));
        assertEquals("test-ref-id", headers.getFirst("X-Reference-Id"));
    }

    @Test
    @DisplayName("Should process collection request via MomoCollectionsClient")
    void testCollectionsClientRequestToPay() {
        MoMoRequest request = new MoMoRequest(
                new BigDecimal("250.00"),
                "ZAR",
                "+27821234567",
                "EXT-1001",
                "Stokvel contribution",
                "Digital Stokvel"
        );

        MoMoResponse response = momoCollectionsClient.requestToPay(request);
        assertNotNull(response);
        assertNotNull(response.getReferenceId());
        assertEquals(MoMoTransactionStatus.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("Should process disbursement transfer via MomoDisbursementClient")
    void testDisbursementClientTransfer() {
        MoMoDisbursementRequest request = new MoMoDisbursementRequest(
                new BigDecimal("5000.00"),
                "ZAR",
                "+27829998888",
                "PAYOUT-EXT-2001",
                "Stokvel payout",
                "Digital Stokvel"
        );

        MoMoDisbursementResponse response = momoDisbursementClient.transfer(request);
        assertNotNull(response);
        assertNotNull(response.getReferenceId());
        assertEquals(MoMoTransactionStatus.PENDING, response.getStatus());
    }

    @Test
    @DisplayName("Should accept collection callback webhook")
    void testCollectionWebhook() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("financialTransactionId", "987654321");
        payload.put("externalId", "EXT-1001");
        payload.put("amount", "250.00");
        payload.put("currency", "ZAR");
        payload.put("status", "SUCCESSFUL");

        mockMvc.perform(post("/momo/callback/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    @DisplayName("Should accept disbursement callback webhook")
    void testDisbursementWebhook() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("financialTransactionId", "123456789");
        payload.put("externalId", "PAYOUT-EXT-2001");
        payload.put("amount", "5000.00");
        payload.put("currency", "ZAR");
        payload.put("status", "SUCCESSFUL");

        mockMvc.perform(post("/momo/callback/disbursement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.message", notNullValue()));
    }
}
