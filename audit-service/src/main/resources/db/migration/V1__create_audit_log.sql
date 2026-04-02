CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timestamp       TIMESTAMP      NOT NULL DEFAULT now(),
    service         VARCHAR(50)    NOT NULL,
    event_type      VARCHAR(50)    NOT NULL,
    aggregate_type  VARCHAR(50)    NOT NULL,
    aggregate_id    UUID           NOT NULL,
    actor_id        UUID,
    actor_role      VARCHAR(30),
    payload         TEXT           NOT NULL,
    ip_address      VARCHAR(45)
);

CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp DESC);
CREATE INDEX idx_audit_log_aggregate ON audit_log(aggregate_type, aggregate_id);
CREATE INDEX idx_audit_log_actor ON audit_log(actor_id);
CREATE INDEX idx_audit_log_event_type ON audit_log(event_type);
