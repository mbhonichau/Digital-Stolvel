package com.digitalstokvel.group.dto;

import com.digitalstokvel.group.entity.ContributionFrequency;
import com.digitalstokvel.group.entity.GroupType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 150, message = "Group name must not exceed 150 characters")
    private String name;

    private String description;

    private GroupType groupType = GroupType.ROTATING;

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(value = "0.01", message = "Contribution amount must be greater than 0")
    private BigDecimal contributionAmount;

    private ContributionFrequency contributionFrequency = ContributionFrequency.MONTHLY;

    @NotNull(message = "Maximum members is required")
    @Min(value = 1, message = "Maximum members must be at least 1")
    private Integer maxMembers;

    private UUID creatorMemberId;

    public CreateGroupRequest() {
    }

    public CreateGroupRequest(String name, String description, GroupType groupType,
                              BigDecimal contributionAmount, ContributionFrequency contributionFrequency,
                              Integer maxMembers, UUID creatorMemberId) {
        this.name = name;
        this.description = description;
        this.groupType = groupType;
        this.contributionAmount = contributionAmount;
        this.contributionFrequency = contributionFrequency;
        this.maxMembers = maxMembers;
        this.creatorMemberId = creatorMemberId;
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

    public UUID getCreatorMemberId() {
        return creatorMemberId;
    }

    public void setCreatorMemberId(UUID creatorMemberId) {
        this.creatorMemberId = creatorMemberId;
    }
}
