# Progress

## Completed Steps

### Step 0: Project Planning (2026-03-28)
- [x] CLAUDE.md, docs/architecture.md, progress.md, next-step.md

### Step 1: Project Scaffolding (2026-03-28)
- [x] Gradle multi-module (Java 21, Spring Boot 3.4.1), Next.js 14 frontend

### Step 2: Docker Compose (2026-03-28)
- [x] PostgreSQL 16, Zookeeper, Kafka, Dockerfiles, healthchecks

### Step 3: Auth Service (2026-03-28)
- [x] User/RefreshToken, JWT (15min+7d), Spring Security, Flyway V1, 5 integration tests

### Step 4: API Gateway (2026-03-28)
- [x] Spring Cloud Gateway (6 routes), JWT filter, CORS, X-User-Id/Role/Email forwarding

### Step 5: Deal Service (2026-03-28)
- [x] Deal/DealEvent entities, DealStateMachine, CRUD + cancel, Flyway V1, 5 integration tests

### Step 6: Payment Service (2026-03-28)
- [x] Account/Transaction/EscrowHold, hold/release/refund, pessimistic locking, 5 integration tests

### Step 7: Deal-Payment Integration (2026-03-28)
- [x] **Design**: Hybrid approach — synchronous REST for deal↔payment, Kafka for notification bus
- [x] DealEventMessage DTO + DealEventProducer (publishes to "deal-events" topic)
- [x] RestClientConfig: RestClient bean for calling payment-service
- [x] DealService: 3 new methods:
  - `fundDeal()`: validate CREATED→FUNDED, REST call to hold funds, update status
  - `deliverDeal()`: validate FUNDED→DELIVERED, update status (beneficiary only)
  - `confirmDeal()`: validate DELIVERED→RELEASING→COMPLETED, REST call to release, update status
- [x] DealController: 3 new endpoints:
  - `POST /api/deals/{id}/fund` (depositor)
  - `POST /api/deals/{id}/deliver` (beneficiary)
  - `POST /api/deals/{id}/confirm` (depositor)
- [x] All state transitions publish Kafka events (DEAL_CREATED, FUNDED, DELIVERED, COMPLETED, CANCELLED)
- [x] Spring Kafka added to deal-service + payment-service
- [x] application-docker.yml for deal-service (Kafka + payment-service Docker URLs)
- [x] Test config: Kafka disabled, DealEventProducer and RestClient mocked
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL (all modules)

## Pending Steps

| Step | Name | Status |
|------|------|--------|
| 8 | Frontend — auth pages | ⬜ |
| 9 | Frontend — deal flow UI | ⬜ |
| 10 | Notification service | ⬜ |
| 11 | Operator + admin roles | ⬜ |
| 12 | Dispute flow | ⬜ |
| 13 | Observability stack | ⬜ |
