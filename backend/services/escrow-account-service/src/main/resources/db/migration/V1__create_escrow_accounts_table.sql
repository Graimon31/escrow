CREATE TABLE escrow_accounts (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL UNIQUE,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    state VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
