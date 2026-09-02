ALTER TABLE members
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(150);

UPDATE members
SET display_name = TRIM(first_name || ' ' || last_name)
WHERE display_name IS NULL;

CREATE INDEX IF NOT EXISTS idx_members_display_name ON members(display_name);
