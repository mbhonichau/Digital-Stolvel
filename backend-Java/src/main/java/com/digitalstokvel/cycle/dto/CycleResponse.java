package com.digitalstokvel.cycle.dto;

import com.digitalstokvel.cycle.entity.Cycle;
import com.digitalstokvel.cycle.entity.CycleStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CycleResponse {

    private UUID id;
    private UUID groupId;
    private String groupName;
    private Integer cycleNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal targetAmount;
    private CycleStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public CycleResponse() {
    }

    public static CycleResponse fromEntity(Cycle cycle) {
        CycleResponse response = new CycleResponse();
        response.setId(cycle.getId());
        if (cycle.getGroup() != null) {
            response.setGroupId(cycle.getGroup().getId());
            response.setGroupName(cycle.getGroup().getName());
        }
        response.setCycleNumber(cycle.getCycleNumber());
        response.setStartDate(cycle.getStartDate());
        response.setEndDate(cycle.getEndDate());
        response.setTargetAmount(cycle.getTargetAmount());
        response.setStatus(cycle.getStatus());
        response.setCreatedAt(cycle.getCreatedAt());
        response.setUpdatedAt(cycle.getUpdatedAt());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
