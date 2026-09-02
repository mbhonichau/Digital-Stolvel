package com.digitalstokvel.payout.dto;

import com.digitalstokvel.payout.entity.Payout;
import com.digitalstokvel.payout.entity.PayoutMethod;
import com.digitalstokvel.payout.entity.PayoutStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PayoutResponse {

    private UUID id;
    private UUID cycleId;
    private UUID memberId;
    private String memberName;
    private String memberPhoneNumber;
    private BigDecimal amount;
    private String currency;
    private PayoutStatus status;
    private PayoutMethod payoutMethod;
    private String payoutReference;
    private LocalDate scheduledDate;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    public PayoutResponse() {
    }

    public static PayoutResponse fromEntity(Payout payout) {
        PayoutResponse response = new PayoutResponse();
        response.setId(payout.getId());
        if (payout.getCycle() != null) {
            response.setCycleId(payout.getCycle().getId());
        }
        if (payout.getMember() != null) {
            response.setMemberId(payout.getMember().getId());
            response.setMemberName(payout.getMember().getFullName());
            response.setMemberPhoneNumber(payout.getMember().getPhoneNumber());
        }
        response.setAmount(payout.getAmount());
        response.setCurrency(payout.getCurrency());
        response.setStatus(payout.getStatus());
        response.setPayoutMethod(payout.getPayoutMethod());
        response.setPayoutReference(payout.getPayoutReference());
        response.setScheduledDate(payout.getScheduledDate());
        response.setPaidAt(payout.getPaidAt());
        response.setCreatedAt(payout.getCreatedAt());
        response.setUpdatedAt(payout.getUpdatedAt());
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

    public PayoutStatus getStatus() {
        return status;
    }

    public void setStatus(PayoutStatus status) {
        this.status = status;
    }

    public PayoutMethod getPayoutMethod() {
        return payoutMethod;
    }

    public void setPayoutMethod(PayoutMethod payoutMethod) {
        this.payoutMethod = payoutMethod;
    }

    public String getPayoutReference() {
        return payoutReference;
    }

    public void setPayoutReference(String payoutReference) {
        this.payoutReference = payoutReference;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
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
