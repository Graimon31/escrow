CREATE TABLE deals (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    depositor_username VARCHAR(128) NOT NULL,
    beneficiary_username VARCHAR(128) NOT NULL,
    state VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
