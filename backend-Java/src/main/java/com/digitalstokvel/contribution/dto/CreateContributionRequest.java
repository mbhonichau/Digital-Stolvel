package com.digitalstokvel.contribution.dto;

import com.digitalstokvel.contribution.entity.ContributionStatus;
import com.digitalstokvel.contribution.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateContributionRequest {

    @NotNull(message = "Cycle ID is required")
    private UUID cycleId;

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotNull(message = "Contribution amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String currency = "ZAR";

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private String paymentReference;

    private ContributionStatus status = ContributionStatus.PENDING;

    public CreateContributionRequest() {
    }

    public CreateContributionRequest(UUID cycleId, UUID memberId, BigDecimal amount,
                                     PaymentMethod paymentMethod, String paymentReference) {
        this.cycleId = cycleId;
        this.memberId = memberId;
        this.amount = amount;
        this.currency = "ZAR";
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
        this.status = ContributionStatus.PENDING;
    }

    public CreateContributionRequest(UUID cycleId, UUID memberId, BigDecimal amount, String currency,
                                     PaymentMethod paymentMethod, String paymentReference, ContributionStatus status) {
        this.cycleId = cycleId;
        this.memberId = memberId;
        this.amount = amount;
        this.currency = currency != null ? currency : "ZAR";
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
        this.status = status != null ? status : ContributionStatus.PENDING;
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

    public ContributionStatus getStatus() {
        return status;
    }

    public void setStatus(ContributionStatus status) {
        this.status = status;
    }
}
