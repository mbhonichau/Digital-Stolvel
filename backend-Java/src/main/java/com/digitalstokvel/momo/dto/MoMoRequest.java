package com.digitalstokvel.momo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MoMoRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String currency = "ZAR";

    @NotBlank(message = "Payer phone number is required")
    private String payerPhoneNumber;

    @NotBlank(message = "External reference ID is required")
    private String externalId;

    private String payerMessage;
    private String payeeNote;

    public MoMoRequest() {
    }

    public MoMoRequest(BigDecimal amount, String currency, String payerPhoneNumber,
                       String externalId, String payerMessage, String payeeNote) {
        this.amount = amount;
        this.currency = currency != null ? currency : "ZAR";
        this.payerPhoneNumber = payerPhoneNumber;
        this.externalId = externalId;
        this.payerMessage = payerMessage;
        this.payeeNote = payeeNote;
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

    public String getPayerPhoneNumber() {
        return payerPhoneNumber;
    }

    public void setPayerPhoneNumber(String payerPhoneNumber) {
        this.payerPhoneNumber = payerPhoneNumber;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPayerMessage() {
        return payerMessage;
    }

    public void setPayerMessage(String payerMessage) {
        this.payerMessage = payerMessage;
    }

    public String getPayeeNote() {
        return payeeNote;
    }

    public void setPayeeNote(String payeeNote) {
        this.payeeNote = payeeNote;
    }
}
