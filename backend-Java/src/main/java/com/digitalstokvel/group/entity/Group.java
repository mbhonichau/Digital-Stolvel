package com.digitalstokvel.group.entity;

import com.digitalstokvel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "\"groups\"")
public class Group extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false, length = 50)
    private GroupType groupType = GroupType.ROTATING;

    @Column(name = "contribution_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal contributionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_frequency", nullable = false, length = 50)
    private ContributionFrequency contributionFrequency = ContributionFrequency.MONTHLY;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private GroupStatus status = GroupStatus.ACTIVE;

    public Group() {
    }

    public Group(String name, String description, GroupType groupType,
                 BigDecimal contributionAmount, ContributionFrequency contributionFrequency,
                 Integer maxMembers) {
        this.name = name;
        this.description = description;
        this.groupType = groupType;
        this.contributionAmount = contributionAmount;
        this.contributionFrequency = contributionFrequency;
        this.maxMembers = maxMembers;
        this.status = GroupStatus.ACTIVE;
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

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }
}
