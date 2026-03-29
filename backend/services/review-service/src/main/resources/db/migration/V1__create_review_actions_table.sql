CREATE TABLE review_actions (
    id UUID PRIMARY KEY,
    deal_id UUID NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    actor VARCHAR(128) NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
