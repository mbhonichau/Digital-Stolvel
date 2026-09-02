package com.digitalstokvel.group.repository;

import com.digitalstokvel.group.entity.Group;
import com.digitalstokvel.group.entity.GroupStatus;
import com.digitalstokvel.group.entity.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    List<Group> findByStatus(GroupStatus status);

    List<Group> findByGroupType(GroupType groupType);

    List<Group> findByNameContainingIgnoreCase(String name);
}
