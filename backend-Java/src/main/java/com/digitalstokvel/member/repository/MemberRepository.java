package com.digitalstokvel.member.repository;

import com.digitalstokvel.member.entity.Member;
import com.digitalstokvel.member.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<Member> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    List<Member> findByStatus(MemberStatus status);
}
