package com.digitalstokvel.contribution.dto;

import com.digitalstokvel.contribution.entity.Contribution;
import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ContributionResponse {

    private UUID id;
    private UUID cycleId;
    private UUID memberId;
    private String memberName;
    private String memberPhoneNumber;
    private BigDecimal amount;
    private String currency;
    private ContributionStatus status;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    public ContributionResponse() {
    }

    public static ContributionResponse fromEntity(Contribution contribution) {
        ContributionResponse response = new ContributionResponse();
        response.setId(contribution.getId());
        if (contribution.getCycle() != null) {
            response.setCycleId(contribution.getCycle().getId());
        }
        if (contribution.getMember() != null) {
            response.setMemberId(contribution.getMember().getId());
            response.setMemberName(contribution.getMember().getFullName());
            response.setMemberPhoneNumber(contribution.getMember().getPhoneNumber());
        }
        response.setAmount(contribution.getAmount());
        response.setCurrency(contribution.getCurrency());
        response.setStatus(contribution.getStatus());
        response.setPaymentMethod(contribution.getPaymentMethod());
        response.setPaymentReference(contribution.getPaymentReference());
        response.setPaidAt(contribution.getPaidAt());
        response.setCreatedAt(contribution.getCreatedAt());
        response.setUpdatedAt(contribution.getUpdatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCycleId() {
        return cycleId;
    }

    public void setCycleId(UUID cycleId) {
        this.cycleId = cycleId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ContributionStatus getStatus() {
        return status;
    }

    public void setStatus(ContributionStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
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
