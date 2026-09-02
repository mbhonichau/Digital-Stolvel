CREATE TABLE IF NOT EXISTS payouts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cycle_id UUID NOT NULL,
    member_id UUID NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'ZAR',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    payout_method VARCHAR(50) NOT NULL,
    payout_reference VARCHAR(100),
    scheduled_date DATE NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payouts_cycle FOREIGN KEY (cycle_id) REFERENCES cycles(id) ON DELETE CASCADE,
    CONSTRAINT fk_payouts_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE INDEX idx_payouts_cycle_id ON payouts(cycle_id);
CREATE INDEX idx_payouts_member_id ON payouts(member_id);
CREATE INDEX idx_payouts_status ON payouts(status);
