CREATE TABLE IF NOT EXISTS "groups" (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    group_type VARCHAR(50) NOT NULL,
    contribution_amount NUMERIC(15, 2) NOT NULL,
    contribution_frequency VARCHAR(50) NOT NULL,
    max_members INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_groups_status ON "groups"(status);
