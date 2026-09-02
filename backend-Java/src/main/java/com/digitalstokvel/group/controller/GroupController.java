package com.digitalstokvel.group.controller;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.group.dto.CreateGroupRequest;
import com.digitalstokvel.group.dto.GroupMemberResponse;
import com.digitalstokvel.group.dto.GroupResponse;
import com.digitalstokvel.group.dto.JoinGroupRequest;
import com.digitalstokvel.group.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/groups", "/api/v1/groups"})
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Group created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable UUID id) {
        GroupResponse response = groupService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.ok("Group retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups() {
        List<GroupResponse> response = groupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.ok("Groups retrieved successfully", response));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> joinGroup(@PathVariable UUID id,
                                                                       @Valid @RequestBody JoinGroupRequest request) {
        GroupMemberResponse response = groupService.joinGroup(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Successfully joined group", response));
    }
}
