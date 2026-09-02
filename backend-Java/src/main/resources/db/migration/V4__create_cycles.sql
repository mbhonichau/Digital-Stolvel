CREATE TABLE IF NOT EXISTS cycles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    group_id UUID NOT NULL,
    cycle_number INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    target_amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cycles_group FOREIGN KEY (group_id) REFERENCES "groups"(id) ON DELETE CASCADE,
    CONSTRAINT uk_group_cycle_number UNIQUE (group_id, cycle_number)
);

CREATE INDEX idx_cycles_group_id ON cycles(group_id);
CREATE INDEX idx_cycles_status ON cycles(status);
