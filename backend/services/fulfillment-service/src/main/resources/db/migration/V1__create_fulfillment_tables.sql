CREATE TABLE fulfillment_records (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL UNIQUE,
    beneficiary_username VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE fulfillment_documents (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(512) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
