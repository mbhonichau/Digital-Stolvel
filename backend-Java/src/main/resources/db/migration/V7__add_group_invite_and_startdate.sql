ALTER TABLE "groups"
    ADD COLUMN IF NOT EXISTS invite_code VARCHAR(20) UNIQUE,
    ADD COLUMN IF NOT EXISTS start_date  DATE;

UPDATE "groups" SET invite_code = UPPER(SUBSTRING(REPLACE(CAST(id AS VARCHAR), '-', ''), 1, 8)) WHERE invite_code IS NULL;

ALTER TABLE "groups" ALTER COLUMN invite_code SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_groups_invite_code ON "groups"(invite_code);
