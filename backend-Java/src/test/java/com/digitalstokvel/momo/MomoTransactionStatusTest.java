package com.digitalstokvel.momo;

import com.digitalstokvel.contribution.dto.CreateContributionRequest;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import com.digitalstokvel.cycle.repository.CycleRepository;
import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.entity.GroupMember;
import com.digitalstokvel.group.entity.GroupRole;
import com.digitalstokvel.group.entity.GroupType;
import com.digitalstokvel.group.repository.GroupMemberRepository;
import com.digitalstokvel.group.repository.GroupRepository;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class MomoTransactionStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private CycleRepository cycleRepository;

    private Group group;
    private Member member;
    private Cycle cycle;

    @BeforeEach
    void setUp() {
        group = groupRepository.save(new Group(
                "Polokwane Savers",
                "Savings club",
                GroupType.ROTATING,
                new BigDecimal("500.00"),
                ContributionFrequency.MONTHLY,
                10
        ));

        member = memberRepository.save(new Member(
                "+27834567890", "Nathi", "Mthethwa", "nathi@example.com"
        ));

        groupMemberRepository.save(new GroupMember(group, member, GroupRole.MEMBER, 1));

        cycle = cycleRepository.save(new Cycle(
                group,
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("5000.00"),
                CycleStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("Should sync status for PENDING MoMo contribution via POST /contributions/{id}/sync-status")
    void testSyncContributionStatus() throws Exception {
        // 1. Create contribution (starts as PENDING with MTN reference)
        CreateContributionRequest request = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("500.00"),
                "ZAR",
                PaymentMethod.MOMO,
                null,
                ContributionStatus.PENDING
        );

        String responseJson = mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("PENDING")))
                .andExpect(jsonPath("$.data.paymentReference", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        String contribId = objectMapper.readTree(responseJson).get("data").get("id").asText();

        // 2. Call sync-status endpoint
        mockMvc.perform(post("/contributions/" + contribId + "/sync-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.id", equalTo(contribId)));
    }

    @Test
    @DisplayName("Should update contribution status to SUCCESSFUL via MoMo webhook")
    void testWebhookUpdatesContributionStatus() throws Exception {
        // 1. Create PENDING MoMo contribution
        CreateContributionRequest request = new CreateContributionRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("500.00"),
                "ZAR",
                PaymentMethod.MOMO,
                null,
                ContributionStatus.PENDING
        );

        String responseJson = mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String contribId = objectMapper.readTree(responseJson).get("data").get("id").asText();
        String mtnReference = objectMapper.readTree(responseJson).get("data").get("paymentReference").asText();

        // 2. Post webhook notification with status SUCCESSFUL
        java.util.Map<String, Object> webhookPayload = new java.util.HashMap<>();
        webhookPayload.put("externalId", mtnReference);
        webhookPayload.put("amount", "500.00");
        webhookPayload.put("currency", "ZAR");
        webhookPayload.put("status", "SUCCESSFUL");

        mockMvc.perform(post("/momo/callback/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(webhookPayload)))
                .andExpect(status().isOk());

        // 3. Verify contribution status is now SUCCESSFUL and paidAt is set
        mockMvc.perform(get("/contributions/" + contribId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", equalTo("SUCCESSFUL")))
                .andExpect(jsonPath("$.data.paidAt", notNullValue()));
    }
}
