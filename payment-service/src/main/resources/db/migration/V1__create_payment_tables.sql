CREATE TABLE accounts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID           NOT NULL UNIQUE,
    balance     NUMERIC(19, 2) NOT NULL DEFAULT 10000.00,
    currency    VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    created_at  TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE escrow_holds (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id     UUID           NOT NULL UNIQUE,
    amount      NUMERIC(19, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'HELD',
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    released_at TIMESTAMP
);

CREATE TABLE transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id         UUID           NOT NULL,
    from_account_id UUID           REFERENCES accounts(id),
    to_account_id   UUID           REFERENCES accounts(id),
    amount          NUMERIC(19, 2) NOT NULL,
    type            VARCHAR(30)    NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    completed_at    TIMESTAMP
);

CREATE INDEX idx_transactions_deal_id ON transactions(deal_id);
CREATE INDEX idx_escrow_holds_deal_id ON escrow_holds(deal_id);
