package com.digitalstokvel.group.repository;

import com.digitalstokvel.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    List<GroupMember> findByGroupId(UUID groupId);

    List<GroupMember> findByMemberId(UUID memberId);

    Optional<GroupMember> findByGroupIdAndMemberId(UUID groupId, UUID memberId);

    boolean existsByGroupIdAndMemberId(UUID groupId, UUID memberId);

    long countByGroupId(UUID groupId);
}
