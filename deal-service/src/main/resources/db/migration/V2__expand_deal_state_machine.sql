-- Expand status column to accommodate longer state names
ALTER TABLE deals ALTER COLUMN status TYPE VARCHAR(30);

-- Add new timestamp columns
ALTER TABLE deals ADD COLUMN agreed_at    TIMESTAMP;
ALTER TABLE deals ADD COLUMN disputed_at  TIMESTAMP;
ALTER TABLE deals ADD COLUMN closed_at    TIMESTAMP;

-- Expand event status columns
ALTER TABLE deal_events ALTER COLUMN previous_status TYPE VARCHAR(30);
ALTER TABLE deal_events ALTER COLUMN new_status TYPE VARCHAR(30);

-- Migrate existing data: CREATED → DRAFT
UPDATE deals SET status = 'DRAFT' WHERE status = 'CREATED';
UPDATE deal_events SET previous_status = 'DRAFT' WHERE previous_status = 'CREATED';
UPDATE deal_events SET new_status = 'DRAFT' WHERE new_status = 'CREATED';

-- Migrate DELIVERED → AWAITING_REVIEW
UPDATE deals SET status = 'AWAITING_REVIEW' WHERE status = 'DELIVERED';
UPDATE deal_events SET previous_status = 'AWAITING_REVIEW' WHERE previous_status = 'DELIVERED';
UPDATE deal_events SET new_status = 'AWAITING_REVIEW' WHERE new_status = 'DELIVERED';

-- Migrate RESOLVED → CLOSED
UPDATE deals SET status = 'CLOSED' WHERE status = 'RESOLVED';
UPDATE deal_events SET previous_status = 'CLOSED' WHERE previous_status = 'RESOLVED';
UPDATE deal_events SET new_status = 'CLOSED' WHERE new_status = 'RESOLVED';
