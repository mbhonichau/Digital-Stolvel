package com.digitalstokvel.integration;

import com.digitalstokvel.contribution.dto.CreateContributionRequest;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import com.digitalstokvel.cycle.dto.CreateCycleRequest;
import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.dto.CreateGroupRequest;
import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.entity.GroupMember;
import com.digitalstokvel.group.entity.GroupRole;
import com.digitalstokvel.group.entity.GroupStatus;
import com.digitalstokvel.group.entity.GroupType;
import com.digitalstokvel.group.repository.GroupMemberRepository;
import com.digitalstokvel.group.repository.GroupRepository;
import com.digitalstokvel.member.dto.CreateMemberRequest;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.digitalstokvel.momo.MomoAuthService;
import com.digitalstokvel.momo.MomoCollectionsClient;
import com.digitalstokvel.momo.config.MomoApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private CycleRepository cycleRepository;

    @Autowired
    private MomoAuthService momoAuthService;

    @Autowired
    private MomoCollectionsClient momoCollectionsClient;

    private Member member;
    private Group group;
    private Cycle cycle;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("+27821002003", "Sizwe", "Dlamini", "sizwe@example.com"));

        group = groupRepository.save(new Group(
                "Soweto Apex Stokvel",
                "Full E2E Stokvel",
                GroupType.ROTATING,
                new BigDecimal("1000.00"),
                ContributionFrequency.MONTHLY,
                5
        ));

        groupMemberRepository.save(new GroupMember(group, member, GroupRole.ADMIN, 1));

        cycle = cycleRepository.save(new Cycle(
                group,
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("1000.00"), // Target = 1000
                CycleStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("1. Complete E2E Flow: Member -> Group -> Cycle -> Contribution (MOMO) -> RequestToPay -> Webhook -> Cycle Completed")
    void testCompleteEndToEndFlow() throws Exception {
        // Step 1: Record MoMo contribution (starts PENDING)
        CreateContributionRequest contribReq = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.MOMO,
                null,
                ContributionStatus.PENDING
        );

        String contribResStr = mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contribReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("PENDING")))
                .andExpect(jsonPath("$.data.paymentReference", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        String contribId = objectMapper.readTree(contribResStr).get("data").get("id").asText();
        String mtnRef = objectMapper.readTree(contribResStr).get("data").get("paymentReference").asText();

        // Verify cycle is still ACTIVE before webhook
        Cycle cycleBefore = cycleRepository.findById(cycle.getId()).orElseThrow();
        assertEquals(CycleStatus.ACTIVE, cycleBefore.getStatus());

        // Step 2: Post webhook from MTN indicating SUCCESSFUL payment
        Map<String, Object> webhookPayload = new HashMap<>();
        webhookPayload.put("externalId", mtnRef);
        webhookPayload.put("amount", "1000.00");
        webhookPayload.put("currency", "ZAR");
        webhookPayload.put("status", "SUCCESSFUL");

        mockMvc.perform(post("/webhooks/momo/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webhookPayload)))
                .andExpect(status().isOk());

        // Step 3: Verify contribution status updated to SUCCESSFUL
        mockMvc.perform(get("/contributions/" + contribId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", equalTo("SUCCESSFUL")))
                .andExpect(jsonPath("$.data.paidAt", notNullValue()));

        // Step 4: Verify cycle status automatically updated to COMPLETED
        Cycle cycleAfter = cycleRepository.findById(cycle.getId()).orElseThrow();
        assertEquals(CycleStatus.COMPLETED, cycleAfter.getStatus());
    }

    @Test
    @DisplayName("2. Test Successful Contribution (Direct EFT)")
    void testSuccessfulContribution() throws Exception {
        CreateContributionRequest request = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.EFT,
                "EFT-REF-SUCCESS",
                ContributionStatus.SUCCESSFUL
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("SUCCESSFUL")))
                .andExpect(jsonPath("$.data.paidAt", notNullValue()));
    }

    @Test
    @DisplayName("3. Test Failed Contribution")
    void testFailedContribution() throws Exception {
        CreateContributionRequest request = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.CARD,
                "CARD-REF-FAIL",
                ContributionStatus.FAILED
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("FAILED")));
    }

    @Test
    @DisplayName("4. Test Pending Contribution")
    void testPendingContribution() throws Exception {
        CreateContributionRequest request = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.CASH,
                "CASH-REF-PENDING",
                ContributionStatus.PENDING
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("PENDING")));
    }

    @Test
    @DisplayName("5. Test Duplicate Phone Number Validation")
    void testDuplicatePhoneNumberValidation() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest(
                "+27821002003", // Same phone number as existing member
                "Duplicate",
                "User",
                "dup@example.com"
        );

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("DUPLICATE_PHONE_NUMBER")));
    }

    @Test
    @DisplayName("6. Test Invalid MSISDN Validation")
    void testInvalidMsisdnValidation() throws Exception {
        CreateMemberRequest request = new CreateMemberRequest(
                "invalid-phone",
                "Invalid",
                "Phone",
                "invalid@example.com"
        );

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("VALIDATION_FAILED")));
    }

    @Test
    @DisplayName("7. Test Expired Token Re-fetching")
    void testExpiredTokenRefetching() {
        String token1 = momoAuthService.getAccessToken();
        assertNotNull(token1);
        String token2 = momoAuthService.fetchAccessToken();
        assertNotNull(token2);
    }

    @Test
    @DisplayName("8. Test Invalid Transaction Reference Query")
    void testInvalidTransactionReferenceQuery() {
        var status = momoCollectionsClient.getCollectionStatus("non-existent-ref-id-99999");
        assertNotNull(status);
    }

    @Test
    @DisplayName("9. Test MTN Gateway Connection Fallback when Unavailable")
    void testMtnGatewayUnavailableFallback() {
        RestTemplate throwingRestTemplate = new RestTemplate() {
            @Override
            public <T> ResponseEntity<T> exchange(String url, org.springframework.http.HttpMethod method,
                                                  HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
                throw new RestClientException("Connection timed out");
            }
        };

        MomoApiConfig config = new MomoApiConfig();
        MomoAuthService authService = new MomoAuthService(config, throwingRestTemplate);
        MomoCollectionsClient client = new MomoCollectionsClient(authService, config, throwingRestTemplate);

        com.digitalstokvel.momo.dto.MoMoRequest momoRequest = new com.digitalstokvel.momo.dto.MoMoRequest(
                new BigDecimal("500.00"),
                "ZAR",
                "+27821234567",
                "TEST-EXT",
                "Message",
                "Note"
        );

        var response = client.requestToPay(momoRequest);
        assertNotNull(response);
        assertNotNull(response.getReferenceId());
        assertEquals(com.digitalstokvel.momo.entity.MoMoTransactionStatus.PENDING, response.getStatus());
    }
}
