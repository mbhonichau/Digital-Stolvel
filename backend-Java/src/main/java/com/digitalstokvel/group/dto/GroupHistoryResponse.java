package com.digitalstokvel.group.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GroupHistoryResponse {

    private Integer cycleNumber;
    private LocalDate dueDate;
    private BigDecimal totalContributed;
    private String payoutRecipient;
    private String status;

    public GroupHistoryResponse() {
    }

    public GroupHistoryResponse(Integer cycleNumber, LocalDate dueDate, BigDecimal totalContributed, String payoutRecipient, String status) {
        this.cycleNumber = cycleNumber;
        this.dueDate = dueDate;
        this.totalContributed = totalContributed;
        this.payoutRecipient = payoutRecipient;
        this.status = status;
    }

    public Integer getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(Integer cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalContributed() {
        return totalContributed;
    }

    public void setTotalContributed(BigDecimal totalContributed) {
        this.totalContributed = totalContributed;
    }

    public String getPayoutRecipient() {
        return payoutRecipient;
    }

    public void setPayoutRecipient(String payoutRecipient) {
        this.payoutRecipient = payoutRecipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
