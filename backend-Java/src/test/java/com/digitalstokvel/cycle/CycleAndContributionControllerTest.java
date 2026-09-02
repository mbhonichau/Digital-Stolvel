package com.digitalstokvel.cycle;

import com.digitalstokvel.contribution.dto.CreateContributionRequest;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import com.digitalstokvel.cycle.dto.CreateCycleRequest;
import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.entity.GroupMember;
import com.digitalstokvel.group.entity.GroupRole;
import com.digitalstokvel.group.entity.GroupStatus;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class CycleAndContributionControllerTest {

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

    private Group group;
    private Member member;

    @BeforeEach
    void setUp() {
        group = groupRepository.save(new Group(
                "Soweto Savers",
                "Monthly stokvel",
                GroupType.ROTATING,
                new BigDecimal("1000.00"),
                ContributionFrequency.MONTHLY,
                10
        ));

        member = memberRepository.save(new Member(
                "+27829998888", "Lerato", "Khumalo", "lerato@example.com"
        ));

        groupMemberRepository.save(new GroupMember(group, member, GroupRole.ADMIN, 1));
    }

    @Test
    @DisplayName("Should create cycle successfully via POST /cycles")
    void testCreateCycle() throws Exception {
        CreateCycleRequest request = new CreateCycleRequest(
                group.getId(),
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("10000.00")
        );

        mockMvc.perform(post("/cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.groupId", equalTo(group.getId().toString())))
                .andExpect(jsonPath("$.data.cycleNumber", equalTo(1)))
                .andExpect(jsonPath("$.data.targetAmount", equalTo(10000.0)));
    }

    @Test
    @DisplayName("Should record contributions with PENDING, SUCCESSFUL, FAILED statuses")
    void testRecordContributionsWithStatuses() throws Exception {
        // 1. Create cycle
        CreateCycleRequest cycleReq = new CreateCycleRequest(
                group.getId(),
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("10000.00")
        );

        String cycleRes = mockMvc.perform(post("/cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cycleReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String cycleIdStr = objectMapper.readTree(cycleRes).get("data").get("id").asText();

        // 2. Record SUCCESSFUL contribution
        CreateContributionRequest contrib1 = new CreateContributionRequest(
                java.util.UUID.fromString(cycleIdStr),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.EFT,
                "REF-SUCCESS-001",
                ContributionStatus.SUCCESSFUL
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contrib1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("SUCCESSFUL")))
                .andExpect(jsonPath("$.data.paidAt", notNullValue()));

        // 3. Record PENDING contribution
        CreateContributionRequest contrib2 = new CreateContributionRequest(
                java.util.UUID.fromString(cycleIdStr),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.EFT,
                "REF-PENDING-002",
                ContributionStatus.PENDING
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contrib2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("PENDING")));

        // 4. Record FAILED contribution
        CreateContributionRequest contrib3 = new CreateContributionRequest(
                java.util.UUID.fromString(cycleIdStr),
                member.getId(),
                new BigDecimal("1000.00"),
                "ZAR",
                PaymentMethod.CARD,
                "REF-FAILED-003",
                ContributionStatus.FAILED
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contrib3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", equalTo("FAILED")));

        // 5. Retrieve all contributions for cycle via GET /cycles/{id}/contributions
        mockMvc.perform(get("/cycles/" + cycleIdStr + "/contributions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    @DisplayName("Should reject contribution from non-group member")
    void testRejectNonGroupMemberContribution() throws Exception {
        CreateCycleRequest cycleReq = new CreateCycleRequest(
                group.getId(),
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("10000.00")
        );

        String cycleRes = mockMvc.perform(post("/cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cycleReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String cycleIdStr = objectMapper.readTree(cycleRes).get("data").get("id").asText();

        // Create outsider member not in group
        Member outsider = memberRepository.save(new Member(
                "+27820001111", "Outsider", "User", "outsider@example.com"
        ));

        CreateContributionRequest contrib = new CreateContributionRequest(
                java.util.UUID.fromString(cycleIdStr),
                outsider.getId(),
                new BigDecimal("1000.00"),
                PaymentMethod.MOMO,
                "REF-OUTSIDER"
        );

        mockMvc.perform(post("/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contrib)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("MEMBER_NOT_IN_GROUP")));
    }
}
