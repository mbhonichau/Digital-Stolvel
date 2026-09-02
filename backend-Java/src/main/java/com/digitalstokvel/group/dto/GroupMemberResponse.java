package com.digitalstokvel.group.dto;

import com.digitalstokvel.group.entity.GroupMember;
import com.digitalstokvel.group.entity.GroupRole;

import java.time.Instant;
import java.util.UUID;

public class GroupMemberResponse {

    private UUID id;
    private UUID memberId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String email;
    private GroupRole role;
    private Integer payoutOrder;
    private String status;
    private Instant joinedAt;

    public GroupMemberResponse() {
    }

    public static GroupMemberResponse fromEntity(GroupMember groupMember) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(groupMember.getId());
        if (groupMember.getMember() != null) {
            response.setMemberId(groupMember.getMember().getId());
            response.setFirstName(groupMember.getMember().getFirstName());
            response.setLastName(groupMember.getMember().getLastName());
            response.setFullName(groupMember.getMember().getFullName());
            response.setPhoneNumber(groupMember.getMember().getPhoneNumber());
            response.setEmail(groupMember.getMember().getEmail());
        }
        response.setRole(groupMember.getRole());
        response.setPayoutOrder(groupMember.getPayoutOrder());
        response.setStatus(groupMember.getStatus());
        response.setJoinedAt(groupMember.getJoinedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
