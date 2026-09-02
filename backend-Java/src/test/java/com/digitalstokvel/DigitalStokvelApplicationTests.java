package com.digitalstokvel;

import com.digitalstokvel.contribution.entity.Contribution;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import com.digitalstokvel.contribution.repository.ContributionRepository;
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
import com.digitalstokvel.member.entity.KycStatus;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.entity.MemberStatus;
import com.digitalstokvel.member.repository.MemberRepository;
import com.digitalstokvel.payout.entity.Payout;
import com.digitalstokvel.payout.entity.PayoutMethod;
import com.digitalstokvel.payout.entity.PayoutStatus;
import com.digitalstokvel.payout.repository.PayoutRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class DigitalStokvelApplicationTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private CycleRepository cycleRepository;

    @Autowired
    private ContributionRepository contributionRepository;

    @Autowired
    private PayoutRepository payoutRepository;

    @Test
    @DisplayName("Context loads successfully and Flyway migrations execute")
    void contextLoads() {
        // Verifies Spring context, datasource, and Flyway migration execution
    }

    @Test
    @DisplayName("Full Stokvel domain lifecycle: group, member, cycle, contribution, payout")
    void testDomainLifecycle() {
        // 1. Create a member
        Member member = new Member("+27821234567", "Sipho", "Nkosi", "sipho@example.co.za");
        member.setKycStatus(KycStatus.VERIFIED);
        member.setStatus(MemberStatus.ACTIVE);
        Member savedMember = memberRepository.save(member);
        assertNotNull(savedMember.getId());
        assertNotNull(savedMember.getCreatedAt());

        // 2. Create a stokvel group
        Group group = new Group(
                "Soweto Family Stokvel",
                "Monthly rotational savings club",
                GroupType.ROTATING,
                new BigDecimal("1000.00"),
                ContributionFrequency.MONTHLY,
                12
        );
        group.setStatus(GroupStatus.ACTIVE);
        Group savedGroup = groupRepository.save(group);
        assertNotNull(savedGroup.getId());

        // 3. Add member to group
        GroupMember groupMember = new GroupMember(savedGroup, savedMember, GroupRole.ADMIN, 1);
        GroupMember savedGroupMember = groupMemberRepository.save(groupMember);
        assertNotNull(savedGroupMember.getId());

        // 4. Create a cycle
        Cycle cycle = new Cycle(
                savedGroup,
                1,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("12000.00"),
                CycleStatus.ACTIVE
        );
        Cycle savedCycle = cycleRepository.save(cycle);
        assertNotNull(savedCycle.getId());

        // 5. Record a contribution
        Contribution contribution = new Contribution(
                savedCycle,
                savedMember,
                new BigDecimal("1000.00"),
                "ZAR",
                ContributionStatus.SUCCESSFUL,
                PaymentMethod.MOMO,
                "MOMO-REF-123456"
        );
        Contribution savedContribution = contributionRepository.save(contribution);
        assertNotNull(savedContribution.getId());

        // 6. Record a payout
        Payout payout = new Payout(
                savedCycle,
                savedMember,
                new BigDecimal("12000.00"),
                "ZAR",
                PayoutStatus.PENDING,
                PayoutMethod.MOMO,
                "PAYOUT-REF-789012",
                LocalDate.now().plusMonths(1)
        );
        Payout savedPayout = payoutRepository.save(payout);
        assertNotNull(savedPayout.getId());

        // Assert lookups
        Optional<Member> foundMember = memberRepository.findByPhoneNumber("+27821234567");
        assertTrue(foundMember.isPresent());
        assertEquals("Sipho", foundMember.get().getFirstName());

        assertEquals(1, groupMemberRepository.findByGroupId(savedGroup.getId()).size());
        assertEquals(1, cycleRepository.findByGroupId(savedGroup.getId()).size());
        assertEquals(1, contributionRepository.findByCycleId(savedCycle.getId()).size());
        assertEquals(1, payoutRepository.findByCycleId(savedCycle.getId()).size());
    }
}
