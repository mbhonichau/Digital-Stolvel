package com.digitalstokvel.member.service;

import com.digitalstokvel.common.exception.BusinessException;
import com.digitalstokvel.common.exception.ResourceNotFoundException;
import com.digitalstokvel.member.dto.CreateMemberRequest;
import com.digitalstokvel.member.dto.MemberResponse;
import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(CreateMemberRequest request) {
        log.info("Creating new member with phone number: {}", request.getPhoneNumber());

        if (memberRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("DUPLICATE_PHONE_NUMBER",
                    "Member with phone number " + request.getPhoneNumber() + " already exists");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("DUPLICATE_EMAIL",
                    "Member with email " + request.getEmail() + " already exists");
        }

        Member member = new Member(
                request.getPhoneNumber().trim(),
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getEmail() != null ? request.getEmail().trim() : null
        );

        Member savedMember = memberRepository.save(member);
        log.info("Successfully created member with id: {}", savedMember.getId());
        return MemberResponse.fromEntity(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return MemberResponse.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberEntityById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
