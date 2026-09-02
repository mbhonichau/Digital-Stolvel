package com.digitalstokvel.group.service;

import com.digitalstokvel.common.exception.BusinessException;
import com.digitalstokvel.common.exception.ResourceNotFoundException;
import com.digitalstokvel.group.dto.CreateGroupRequest;
import com.digitalstokvel.group.dto.GroupMemberResponse;
import com.digitalstokvel.group.dto.GroupResponse;
import com.digitalstokvel.group.dto.JoinGroupRequest;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        MemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.memberRepository = memberRepository;
    }

    public GroupResponse createGroup(CreateGroupRequest request) {
        log.info("Creating new group: {}", request.getName());

        GroupType groupType = request.getGroupType() != null ? request.getGroupType() : GroupType.ROTATING;
        ContributionFrequency frequency = request.getContributionFrequency() != null
                ? request.getContributionFrequency()
                : ContributionFrequency.MONTHLY;

        Group group = new Group(
                request.getName().trim(),
                request.getDescription(),
                groupType,
                request.getContributionAmount(),
                frequency,
                request.getMaxMembers()
        );
        group.setStatus(GroupStatus.ACTIVE);

        Group savedGroup = groupRepository.save(group);

        if (request.getCreatorMemberId() != null) {
            Member creator = memberRepository.findById(request.getCreatorMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Creator member not found with id: " + request.getCreatorMemberId()));

            GroupMember creatorMember = new GroupMember(savedGroup, creator, GroupRole.ADMIN, 1);
            groupMemberRepository.save(creatorMember);
            log.info("Added creator member {} as ADMIN to group {}", creator.getId(), savedGroup.getId());
        }

        return getGroupById(savedGroup.getId());
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        List<GroupMemberResponse> members = groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMemberResponse::fromEntity)
                .collect(Collectors.toList());

        return GroupResponse.fromEntity(group, members);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(group -> {
                    List<GroupMemberResponse> members = groupMemberRepository.findByGroupId(group.getId()).stream()
                            .map(GroupMemberResponse::fromEntity)
                            .collect(Collectors.toList());
                    return GroupResponse.fromEntity(group, members);
                })
                .collect(Collectors.toList());
    }

    public GroupMemberResponse joinGroup(UUID groupId, JoinGroupRequest request) {
        log.info("Member {} attempting to join group {}", request.getMemberId(), groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (group.getStatus() != GroupStatus.ACTIVE) {
            throw new BusinessException("GROUP_INACTIVE", "Cannot join group that is not active");
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + request.getMemberId()));

        if (groupMemberRepository.existsByGroupIdAndMemberId(groupId, member.getId())) {
            throw new BusinessException("MEMBER_ALREADY_IN_GROUP", "Member is already a member of this group");
        }

        long currentCount = groupMemberRepository.countByGroupId(groupId);
        if (currentCount >= group.getMaxMembers()) {
            throw new BusinessException("GROUP_FULL", "Group has reached maximum member capacity (" + group.getMaxMembers() + ")");
        }

        int payoutOrder = (int) currentCount + 1;
        GroupRole role = request.getRole() != null ? request.getRole() : GroupRole.MEMBER;

        GroupMember groupMember = new GroupMember(group, member, role, payoutOrder);
        GroupMember savedMember = groupMemberRepository.save(groupMember);

        log.info("Member {} successfully joined group {} as {} with payout order {}",
                member.getId(), group.getId(), role, payoutOrder);

        return GroupMemberResponse.fromEntity(savedMember);
    }
}
