-- V9: Enforce valid GroupMemberStatus values on group_members.status
ALTER TABLE group_members
    ADD CONSTRAINT chk_group_member_status
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'));
