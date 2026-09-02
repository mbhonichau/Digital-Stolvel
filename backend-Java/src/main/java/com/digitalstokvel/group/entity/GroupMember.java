package com.digitalstokvel.group.entity;

import com.digitalstokvel.common.entity.BaseEntity;
import com.digitalstokvel.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_member", columnNames = {"group_id", "member_id"})
        }
)
public class GroupMember extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private GroupRole role = GroupRole.MEMBER;

    @Column(name = "payout_order")
    private Integer payoutOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "ACTIVE";

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public GroupMember() {
        this.joinedAt = Instant.now();
    }

    public GroupMember(Group group, Member member, GroupRole role, Integer payoutOrder) {
        this.group = group;
        this.member = member;
        this.role = role;
        this.payoutOrder = payoutOrder;
        this.status = "ACTIVE";
        this.joinedAt = Instant.now();
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }

    public Integer getPayoutOrder() {
        return payoutOrder;
    }

    public void setPayoutOrder(Integer payoutOrder) {
        this.payoutOrder = payoutOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
