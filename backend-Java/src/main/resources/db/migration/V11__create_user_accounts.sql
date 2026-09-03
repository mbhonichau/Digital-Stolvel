CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY,
    msisdn VARCHAR(20) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    session_token VARCHAR(100),
    session_expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_app_users_session_token ON app_users(session_token);
