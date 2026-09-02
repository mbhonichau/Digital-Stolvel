package com.digitalstokvel.payout.dto;

import com.digitalstokvel.payout.entity.PayoutMethod;
import com.digitalstokvel.payout.entity.PayoutStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreatePayoutRequest {

    @NotNull(message = "Cycle ID is required")
    private UUID cycleId;

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Payout amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String currency = "ZAR";

    @NotNull(message = "Payout method is required")
    private PayoutMethod payoutMethod;

    private String payoutReference;

    private LocalDate scheduledDate;

    private PayoutStatus status = PayoutStatus.PENDING;

    public CreatePayoutRequest() {
    }

    public CreatePayoutRequest(UUID cycleId, UUID memberId, BigDecimal amount,
                               PayoutMethod payoutMethod, LocalDate scheduledDate) {
        this.cycleId = cycleId;
        this.memberId = memberId;
        this.amount = amount;
        this.currency = "ZAR";
        this.payoutMethod = payoutMethod;
        this.scheduledDate = scheduledDate;
        this.status = PayoutStatus.PENDING;
    }

    public CreatePayoutRequest(UUID cycleId, UUID memberId, BigDecimal amount, String currency,
                               PayoutMethod payoutMethod, String payoutReference,
                               LocalDate scheduledDate, PayoutStatus status) {
        this.cycleId = cycleId;
        this.memberId = memberId;
        this.amount = amount;
        this.currency = currency != null ? currency : "ZAR";
        this.payoutMethod = payoutMethod;
        this.payoutReference = payoutReference;
        this.scheduledDate = scheduledDate;
        this.status = status != null ? status : PayoutStatus.PENDING;
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

    public PayoutStatus getStatus() {
        return status;
    }

    public void setStatus(PayoutStatus status) {
        this.status = status;
    }
}
