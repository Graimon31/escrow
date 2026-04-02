CREATE SCHEMA IF NOT EXISTS documents;

CREATE TABLE documents (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id         UUID           NOT NULL,
    uploader_id     UUID           NOT NULL,
    file_name       VARCHAR(255)   NOT NULL,
    content_type    VARCHAR(100)   NOT NULL,
    file_size       BIGINT         NOT NULL,
    storage_path    VARCHAR(500)   NOT NULL,
    document_type   VARCHAR(50)    NOT NULL DEFAULT 'GENERAL',
    created_at      TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_documents_deal ON documents(deal_id);
CREATE INDEX idx_documents_uploader ON documents(uploader_id);
