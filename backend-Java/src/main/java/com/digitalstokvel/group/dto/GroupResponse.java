package com.digitalstokvel.group.dto;

import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.entity.GroupStatus;
import com.digitalstokvel.group.entity.GroupType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupResponse {

    private UUID id;
    private String name;
    private String description;
    private GroupType groupType;
    private BigDecimal contributionAmount;
    private ContributionFrequency contributionFrequency;
    private Integer maxMembers;
    private int currentMemberCount;
    private GroupStatus status;
    private List<GroupMemberResponse> members = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public GroupResponse() {
    }

    public static GroupResponse fromEntity(Group group, List<GroupMemberResponse> members) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setGroupType(group.getGroupType());
        response.setContributionAmount(group.getContributionAmount());
        response.setContributionFrequency(group.getContributionFrequency());
        response.setMaxMembers(group.getMaxMembers());
        response.setStatus(group.getStatus());
        response.setCreatedAt(group.getCreatedAt());
        response.setUpdatedAt(group.getUpdatedAt());
        if (members != null) {
            response.setMembers(members);
            response.setCurrentMemberCount(members.size());
        } else {
            response.setCurrentMemberCount(0);
        }
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public ContributionFrequency getContributionFrequency() {
        return contributionFrequency;
    }

    public void setContributionFrequency(ContributionFrequency contributionFrequency) {
        this.contributionFrequency = contributionFrequency;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getCurrentMemberCount() {
        return currentMemberCount;
    }

    public void setCurrentMemberCount(int currentMemberCount) {
        this.currentMemberCount = currentMemberCount;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public List<GroupMemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberResponse> members) {
        this.members = members;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
