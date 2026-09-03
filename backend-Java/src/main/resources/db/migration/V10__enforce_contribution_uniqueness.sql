ALTER TABLE contributions
    ADD CONSTRAINT uk_contributions_cycle_member UNIQUE (cycle_id, member_id);
