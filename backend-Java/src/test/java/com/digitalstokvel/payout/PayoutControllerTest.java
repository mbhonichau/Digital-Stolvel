package com.digitalstokvel.payout;

import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;
import com.digitalstokvel.cycle.repository.CycleRepository;
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
import com.digitalstokvel.payout.dto.CreatePayoutRequest;
import com.digitalstokvel.payout.entity.PayoutMethod;
import com.digitalstokvel.payout.entity.PayoutStatus;
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
class PayoutControllerTest {

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
                "Durban Savers",
                "Rotational savings group",
                GroupType.ROTATING,
                new BigDecimal("1000.00"),
                ContributionFrequency.MONTHLY,
                10
        ));

        member = memberRepository.save(new Member(
                "+27827776666", "Bantu", "Biko", "bantu@example.com"
        ));

        groupMemberRepository.save(new GroupMember(group, member, GroupRole.MEMBER, 1));

        cycle = cycleRepository.save(new Cycle(
                group,
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("10000.00"),
                CycleStatus.ACTIVE
        ));
    }

    @Test
    @DisplayName("Should create MOMO payout and trigger MoMoDisbursementClient")
    void testCreateMoMoPayout() throws Exception {
        CreatePayoutRequest request = new CreatePayoutRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("10000.00"),
                PayoutMethod.MOMO,
                LocalDate.now()
        );

        String responseJson = mockMvc.perform(post("/payouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", equalTo(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.payoutMethod", equalTo("MOMO")))
                .andExpect(jsonPath("$.data.payoutReference", notNullValue()))
                .andExpect(jsonPath("$.data.status", equalTo("PENDING")))
                .andReturn().getResponse().getContentAsString();

        String payoutId = objectMapper.readTree(responseJson).get("data").get("id").asText();

        // Get payout by ID
        mockMvc.perform(get("/payouts/" + payoutId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount", equalTo(10000.0)))
                .andExpect(jsonPath("$.data.memberName", equalTo("Bantu Biko")));
    }

    @Test
    @DisplayName("Should create offline EFT payout")
    void testCreateEftPayout() throws Exception {
        CreatePayoutRequest request = new CreatePayoutRequest(
                cycle.getId(),
                member.getId(),
                new BigDecimal("5000.00"),
                "ZAR",
                PayoutMethod.BANK_TRANSFER,
                "EFT-PAYOUT-999",
                LocalDate.now(),
                PayoutStatus.PAID
        );

        mockMvc.perform(post("/payouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.payoutMethod", equalTo("BANK_TRANSFER")))
                .andExpect(jsonPath("$.data.payoutReference", equalTo("EFT-PAYOUT-999")))
                .andExpect(jsonPath("$.data.status", equalTo("PAID")))
                .andExpect(jsonPath("$.data.paidAt", notNullValue()));
    }
}
