-- V2: Double-entry ledger system
-- Adds proper accounting with ledger_accounts, ledger_entries, and escrow_accounts

-- Ledger accounts: virtual accounts for double-entry bookkeeping
-- Each user has: AVAILABLE (spendable), RESERVED (held for escrow)
-- System has: ESCROW_HOLDING (safeguarded funds)
CREATE TABLE ledger_accounts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    UUID,
    owner_type  VARCHAR(20)    NOT NULL DEFAULT 'USER',
    account_type VARCHAR(30)   NOT NULL,
    currency    VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    balance     NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT uq_ledger_account UNIQUE (owner_id, account_type, currency)
);

-- owner_type: USER, SYSTEM
-- account_type: AVAILABLE, RESERVED, ESCROW_HOLDING, PAYABLE

-- Ledger entries: immutable debit/credit records
CREATE TABLE ledger_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id  UUID           NOT NULL,
    account_id      UUID           NOT NULL REFERENCES ledger_accounts(id),
    entry_type      VARCHAR(10)    NOT NULL,
    amount          NUMERIC(19, 2) NOT NULL,
    balance_after   NUMERIC(19, 2) NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_ledger_entries_tx ON ledger_entries(transaction_id);
CREATE INDEX idx_ledger_entries_account ON ledger_entries(account_id);

-- Escrow accounts: per-deal escrow lifecycle
CREATE TABLE escrow_accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deal_id         UUID           NOT NULL UNIQUE,
    depositor_id    UUID           NOT NULL,
    beneficiary_id  UUID           NOT NULL,
    amount          NUMERIC(19, 2) NOT NULL,
    currency        VARCHAR(3)     NOT NULL DEFAULT 'RUB',
    status          VARCHAR(30)    NOT NULL DEFAULT 'NOT_CREATED',
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    funded_at       TIMESTAMP,
    released_at     TIMESTAMP,
    refunded_at     TIMESTAMP
);

CREATE INDEX idx_escrow_accounts_deal ON escrow_accounts(deal_id);
CREATE INDEX idx_escrow_accounts_status ON escrow_accounts(status);

-- Idempotency keys
CREATE TABLE idempotency_keys (
    key             VARCHAR(100)   PRIMARY KEY,
    result          TEXT,
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    expires_at      TIMESTAMP      NOT NULL DEFAULT (now() + interval '24 hours')
);

-- Outbox events table for transactional outbox pattern
CREATE TABLE outbox_events (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type  VARCHAR(50)    NOT NULL,
    aggregate_id    UUID           NOT NULL,
    event_type      VARCHAR(50)    NOT NULL,
    payload         TEXT           NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT now(),
    published       BOOLEAN        NOT NULL DEFAULT FALSE,
    published_at    TIMESTAMP
);

CREATE INDEX idx_outbox_unpublished ON outbox_events(published, created_at) WHERE published = FALSE;

-- Seed system escrow holding account
INSERT INTO ledger_accounts (id, owner_id, owner_type, account_type, currency, balance)
VALUES ('00000000-0000-0000-0000-000000000001', NULL, 'SYSTEM', 'ESCROW_HOLDING', 'RUB', 0.00)
ON CONFLICT DO NOTHING;

-- Migrate existing accounts to ledger: create AVAILABLE ledger accounts for existing users
INSERT INTO ledger_accounts (owner_id, owner_type, account_type, currency, balance)
SELECT user_id, 'USER', 'AVAILABLE', currency, balance
FROM accounts
ON CONFLICT (owner_id, account_type, currency) DO NOTHING;
