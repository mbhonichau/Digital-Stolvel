package com.digitalstokvel.api;

import com.digitalstokvel.api.dto.ApiDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class StokvelController {
    private final StokvelService service;

    public StokvelController(StokvelService service) {
        this.service = service;
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@Valid @RequestBody CreateGroupRequest request) { return service.createGroup(request); }
    @GetMapping("/groups") public List<GroupResponse> myGroups() { return service.myGroups(); }
    @GetMapping("/groups/{id}") public GroupResponse getGroup(@PathVariable UUID id) { return service.getGroup(id); }
    @GetMapping("/groups/{id}/cycles") public List<CycleResponse> cycles(@PathVariable UUID id) { return service.cycles(id); }
    @PostMapping("/groups/{id}/join") public GroupResponse join(@PathVariable UUID id, @Valid @RequestBody JoinGroupRequest request) { return service.join(id, request); }
    @PostMapping("/groups/{id}/members") public GroupResponse addMember(@PathVariable UUID id, @Valid @RequestBody AddGroupMemberRequest request) { return service.addMember(id, request); }
    @PostMapping("/groups/join") public GroupResponse joinInvite(@Valid @RequestBody JoinGroupRequest request) { return service.joinByInvite(request); }
    @GetMapping("/groups/{id}/history") public List<CycleHistoryResponse> history(@PathVariable UUID id) { return service.history(id); }
    @PostMapping("/cycles") @ResponseStatus(HttpStatus.CREATED) public CycleResponse createCycle(@Valid @RequestBody CreateCycleRequest request) { return service.createCycle(request); }
    @PostMapping("/contributions") @ResponseStatus(HttpStatus.ACCEPTED) public ContributionStatusResponse contribute(@Valid @RequestBody TriggerContributionRequest request) { return service.contribute(request); }
    @GetMapping("/cycles/{id}/contributions") public List<ContributionStatusResponse> contributions(@PathVariable UUID id) { return service.contributions(id); }
    @PostMapping("/contributions/{id}/refresh") public ContributionStatusResponse refresh(@PathVariable UUID id) { return service.refresh(id); }
    @GetMapping("/contributions/{id}") public ContributionStatusResponse contribution(@PathVariable UUID id) { return service.contributionStatus(id); }
    @PostMapping("/payouts") @ResponseStatus(HttpStatus.ACCEPTED) public TriggerPayoutResponse payout(@Valid @RequestBody TriggerPayoutRequest request) { return service.payout(request); }
    @PostMapping("/cycles/{id}/payout") @ResponseStatus(HttpStatus.ACCEPTED) public TriggerPayoutResponse payoutForCycle(@PathVariable UUID id) { return service.payout(new TriggerPayoutRequest(id)); }
    @GetMapping("/payouts/{id}") public TriggerPayoutResponse payoutStatus(@PathVariable UUID id) { return service.payoutStatus(id); }
}
