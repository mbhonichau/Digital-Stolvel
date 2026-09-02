CREATE TABLE IF NOT EXISTS members (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    kyc_status VARCHAR(50) NOT NULL DEFAULT 'NOT_VERIFIED',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_members_phone_number UNIQUE (phone_number),
    CONSTRAINT uk_members_email UNIQUE (email)
);

CREATE INDEX idx_members_phone ON members(phone_number);
CREATE INDEX idx_members_status ON members(status);
