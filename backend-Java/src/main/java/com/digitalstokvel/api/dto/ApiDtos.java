package com.digitalstokvel.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class ApiDtos {
    private ApiDtos() { }
    public record CreateGroupRequest(@NotBlank String name, @NotNull @DecimalMin("0.01") BigDecimal contributionAmount,
                                     @NotBlank String frequency, @NotNull LocalDate startDate, @Min(1) @Max(100) Integer maxMembers,
                                     String creatorMsisdn, String creatorDisplayName) { }
    public record JoinGroupRequest(String inviteCode, @NotBlank String msisdn, @NotBlank String displayName) { }
    public record AddGroupMemberRequest(@NotBlank String adminMsisdn, @NotBlank String msisdn, @NotBlank String displayName) { }
    public record MemberSummary(UUID id, String displayName, String msisdn, Integer joinOrder) { }
    public record GroupResponse(UUID id, String name, BigDecimal contributionAmount, String frequency, LocalDate startDate,
                                String inviteCode, Integer maxMembers, Instant createdAt, List<MemberSummary> members) { }
    public record CreateCycleRequest(@NotNull UUID groupId, LocalDate dueDate) { }
    public record CycleResponse(UUID id, UUID groupId, Integer cycleNumber, LocalDate dueDate, String status) { }
    public record TriggerContributionRequest(@NotNull UUID cycleId, @NotNull UUID memberId) { }
    public record ContributionStatusResponse(UUID id, UUID memberId, String displayName, BigDecimal amount, String status, Instant paidAt, String momoReference) { }
    public record TriggerPayoutRequest(@NotNull UUID cycleId) { }
    public record TriggerPayoutResponse(UUID id, UUID recipientMemberId, String recipientName, BigDecimal amount, String status) { }
    public record CycleHistoryResponse(Integer cycleNumber, LocalDate dueDate, BigDecimal totalContributed, String payoutRecipient, String status) { }
}
