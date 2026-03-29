CREATE TABLE resolution_decisions (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL,
    outcome VARCHAR(32) NOT NULL,
    actor VARCHAR(128) NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_resolution_decisions_deal_id_created_at ON resolution_decisions(deal_id, created_at DESC);
