package com.digitalstokvel.cycle.dto;

import com.digitalstokvel.cycle.entity.CycleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateCycleRequest {

    @NotNull(message = "Group ID is required")
    private UUID groupId;

    private Integer cycleNumber;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    private CycleStatus status = CycleStatus.ACTIVE;

    public CreateCycleRequest() {
    }

    public CreateCycleRequest(UUID groupId, Integer cycleNumber, LocalDate startDate, LocalDate endDate, BigDecimal targetAmount) {
        this.groupId = groupId;
        this.cycleNumber = cycleNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetAmount = targetAmount;
        this.status = CycleStatus.ACTIVE;
    }

    public CreateCycleRequest(UUID groupId, Integer cycleNumber, LocalDate startDate, LocalDate endDate, BigDecimal targetAmount, CycleStatus status) {
        this.groupId = groupId;
        this.cycleNumber = cycleNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetAmount = targetAmount;
        this.status = status != null ? status : CycleStatus.ACTIVE;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public Integer getCycleNumber() {
        return cycleNumber;
    }

    public void setCycleNumber(Integer cycleNumber) {
        this.cycleNumber = cycleNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public CycleStatus getStatus() {
        return status;
    }

    public void setStatus(CycleStatus status) {
        this.status = status;
    }
}
