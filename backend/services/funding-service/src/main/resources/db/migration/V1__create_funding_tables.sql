CREATE TABLE funding_operations (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    requested_by VARCHAR(128) NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE funding_audit_events (
    id UUID PRIMARY KEY,
    operation_id UUID NOT NULL,
    deal_id UUID NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    event_payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
