CREATE TABLE deals (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255)   NOT NULL,
    description     TEXT,
    amount          NUMERIC(19, 2) NOT NULL,
    currency        VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    depositor_id    UUID           NOT NULL,
    beneficiary_id  UUID           NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'CREATED',
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT now(),
    funded_at       TIMESTAMP,
    delivered_at    TIMESTAMP,
    completed_at    TIMESTAMP,
    cancelled_at    TIMESTAMP
);

CREATE INDEX idx_deals_depositor ON deals(depositor_id);
CREATE INDEX idx_deals_beneficiary ON deals(beneficiary_id);
CREATE INDEX idx_deals_status ON deals(status);

CREATE TABLE deal_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id         UUID           NOT NULL REFERENCES deals(id) ON DELETE CASCADE,
    event_type      VARCHAR(50)    NOT NULL,
    actor_id        UUID           NOT NULL,
    actor_role      VARCHAR(20)    NOT NULL,
    previous_status VARCHAR(20),
    new_status      VARCHAR(20)    NOT NULL,
    payload         JSONB,
    created_at      TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE INDEX idx_deal_events_deal_id ON deal_events(deal_id);
