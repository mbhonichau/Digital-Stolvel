package com.digitalstokvel.member.dto;

import com.digitalstokvel.member.entity.KycStatus;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.entity.MemberStatus;

import java.time.Instant;
import java.util.UUID;

public class MemberResponse {

    private UUID id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private KycStatus kycStatus;
    private MemberStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public MemberResponse() {
    }

    public static MemberResponse fromEntity(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setPhoneNumber(member.getPhoneNumber());
        response.setFirstName(member.getFirstName());
        response.setLastName(member.getLastName());
        response.setFullName(member.getFullName());
        response.setEmail(member.getEmail());
        response.setKycStatus(member.getKycStatus());
        response.setStatus(member.getStatus());
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
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
