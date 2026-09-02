package com.digitalstokvel.group;

import com.digitalstokvel.group.dto.CreateGroupRequest;
import com.digitalstokvel.group.dto.JoinGroupRequest;
import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.GroupRole;
import com.digitalstokvel.group.entity.GroupType;
import com.digitalstokvel.member.dto.CreateMemberRequest;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Should create group successfully and retrieve group with members")
    void testCreateAndGetGroup() throws Exception {
        // Create creator member
        Member creator = memberRepository.save(new Member("+27831112222", "Thabo", "Mbeki", "thabo@example.com"));

        CreateGroupRequest request = new CreateGroupRequest(
                "Johannesburg Savers",
                "Monthly rotational group",
                GroupType.ROTATING,
                new BigDecimal("500.00"),
                ContributionFrequency.MONTHLY,
                10,
                creator.getId()
        );

        String responseJson = mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.name", equalTo("Johannesburg Savers")))
                .andExpect(jsonPath("$.data.contributionAmount", equalTo(500.0)))
                .andExpect(jsonPath("$.data.currentMemberCount", equalTo(1)))
                .andExpect(jsonPath("$.data.members", hasSize(1)))
                .andExpect(jsonPath("$.data.members[0].role", equalTo("ADMIN")))
                .andReturn().getResponse().getContentAsString();

        String groupIdStr = objectMapper.readTree(responseJson).get("data").get("id").asText();

        // Get group by ID
        mockMvc.perform(get("/groups/" + groupIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.name", equalTo("Johannesburg Savers")))
                .andExpect(jsonPath("$.data.currentMemberCount", equalTo(1)));
    }

    @Test
    @DisplayName("Should allow members to join group and track payout order")
    void testJoinGroup() throws Exception {
        // Create group without initial creator
        CreateGroupRequest createRequest = new CreateGroupRequest(
                "Cape Town Stokvel",
                "Savings group",
                GroupType.ROTATING,
                new BigDecimal("1000.00"),
                ContributionFrequency.MONTHLY,
                2,
                null
        );

        String createRes = mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String groupIdStr = objectMapper.readTree(createRes).get("data").get("id").asText();

        // Create 2 members
        Member m1 = memberRepository.save(new Member("+27830000001", "Member", "One", "m1@test.com"));
        Member m2 = memberRepository.save(new Member("+27830000002", "Member", "Two", "m2@test.com"));

        // Join member 1
        JoinGroupRequest joinReq1 = new JoinGroupRequest(m1.getId(), GroupRole.ADMIN);
        mockMvc.perform(post("/groups/" + groupIdStr + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReq1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.payoutOrder", equalTo(1)))
                .andExpect(jsonPath("$.data.role", equalTo("ADMIN")));

        // Join member 2
        JoinGroupRequest joinReq2 = new JoinGroupRequest(m2.getId(), GroupRole.MEMBER);
        mockMvc.perform(post("/groups/" + groupIdStr + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReq2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.payoutOrder", equalTo(2)))
                .andExpect(jsonPath("$.data.role", equalTo("MEMBER")));

        // Verify group now has 2 members
        mockMvc.perform(get("/groups/" + groupIdStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentMemberCount", equalTo(2)))
                .andExpect(jsonPath("$.data.members", hasSize(2)));

        // Try to join a 3rd member when maxMembers is 2 -> should fail with GROUP_FULL
        Member m3 = memberRepository.save(new Member("+27830000003", "Member", "Three", "m3@test.com"));
        JoinGroupRequest joinReq3 = new JoinGroupRequest(m3.getId(), GroupRole.MEMBER);
        mockMvc.perform(post("/groups/" + groupIdStr + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReq3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("GROUP_FULL")));
    }

    @Test
    @DisplayName("Should prevent duplicate member join")
    void testDuplicateJoinPrevented() throws Exception {
        CreateGroupRequest createRequest = new CreateGroupRequest(
                "Durban Stokvel",
                "Savings group",
                GroupType.SAVINGS,
                new BigDecimal("250.00"),
                ContributionFrequency.WEEKLY,
                5,
                null
        );

        String createRes = mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String groupIdStr = objectMapper.readTree(createRes).get("data").get("id").asText();

        Member m1 = memberRepository.save(new Member("+27840000001", "Unique", "Member", "unique@test.com"));

        JoinGroupRequest joinReq = new JoinGroupRequest(m1.getId());
        mockMvc.perform(post("/groups/" + groupIdStr + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReq)))
                .andExpect(status().isCreated());

        // Join again
        mockMvc.perform(post("/groups/" + groupIdStr + "/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", equalTo("MEMBER_ALREADY_IN_GROUP")));
    }
}
