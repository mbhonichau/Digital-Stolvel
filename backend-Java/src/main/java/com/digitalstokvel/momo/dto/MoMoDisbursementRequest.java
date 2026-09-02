package com.digitalstokvel.momo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class MoMoDisbursementRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String currency = "ZAR";

    @NotBlank(message = "Payee phone number is required")
    private String payeePhoneNumber;

    @NotBlank(message = "External reference ID is required")
    private String externalId;

    private String payerMessage;
    private String payeeNote;

    public MoMoDisbursementRequest() {
    }

    public MoMoDisbursementRequest(BigDecimal amount, String currency, String payeePhoneNumber,
                                  String externalId, String payerMessage, String payeeNote) {
        this.amount = amount;
        this.currency = currency != null ? currency : "ZAR";
        this.payeePhoneNumber = payeePhoneNumber;
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

    public String getPayeePhoneNumber() {
        return payeePhoneNumber;
    }

    public void setPayeePhoneNumber(String payeePhoneNumber) {
        this.payeePhoneNumber = payeePhoneNumber;
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
