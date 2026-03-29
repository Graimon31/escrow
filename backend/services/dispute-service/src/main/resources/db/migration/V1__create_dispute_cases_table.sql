CREATE TABLE dispute_cases (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    opened_by VARCHAR(128) NOT NULL,
    reason TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by VARCHAR(128),
    resolution_comment TEXT
);

CREATE INDEX idx_dispute_cases_deal_id_created_at ON dispute_cases(deal_id, created_at DESC);
