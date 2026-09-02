package com.digitalstokvel.group.dto;

import com.digitalstokvel.group.entity.GroupRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class JoinGroupRequest {

    @NotNull(message = "Member ID is required to join a group")
    private UUID memberId;

    private GroupRole role;

    public JoinGroupRequest() {
    }

    public JoinGroupRequest(UUID memberId) {
        this.memberId = memberId;
        this.role = GroupRole.MEMBER;
    }

    public JoinGroupRequest(UUID memberId, GroupRole role) {
        this.memberId = memberId;
        this.role = role;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public GroupRole getRole() {
        return role;
    }

    public void setRole(GroupRole role) {
        this.role = role;
    }
}
