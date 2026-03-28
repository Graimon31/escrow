# Next Step: Step 7 — Deal-Payment Kafka Integration

## Goal
Deal state transitions trigger payment operations via Kafka events. Full happy path works end-to-end.

## Tasks
1. Add Spring Kafka dependency to deal-service and payment-service
2. Kafka topics: `deal-events`, `payment-events`
3. Deal-service:
   - `DealEventProducer`: publishes events on state transitions
   - `PaymentEventConsumer`: listens for payment confirmations
   - New endpoints: `POST /api/deals/{id}/fund`, `POST /api/deals/{id}/deliver`, `POST /api/deals/{id}/confirm`
4. Payment-service:
   - `DealEventConsumer`: listens for deal fund/confirm events → hold/release
   - `PaymentEventProducer`: publishes hold/release confirmations
5. Idempotency keys on all events (deal_id + event_type)
6. Integration test for full happy path

## Verification
```bash
./gradlew build -x test
# Full happy path via curl:
# register 2 users → login → create deal → fund → deliver → confirm → COMPLETED
```
