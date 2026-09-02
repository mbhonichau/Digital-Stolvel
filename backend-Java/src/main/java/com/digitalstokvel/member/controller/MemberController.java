package com.digitalstokvel.member.controller;

import com.digitalstokvel.common.dto.ApiResponse;
import com.digitalstokvel.member.dto.CreateMemberRequest;
import com.digitalstokvel.member.dto.MemberResponse;
import com.digitalstokvel.member.service.MemberService;
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
@RequestMapping({"/members", "/api/v1/members"})
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Member created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable UUID id) {
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.ok("Member retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> response = memberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.ok("Members retrieved successfully", response));
    }
}
