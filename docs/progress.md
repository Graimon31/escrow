# Progress

## Completed Steps

### Step 0: Project Planning (2026-03-28)
- [x] CLAUDE.md, docs/architecture.md, progress.md, next-step.md

### Step 1: Project Scaffolding (2026-03-28)
- [x] Gradle multi-module (Java 21, Spring Boot 3.4.1), Next.js 14 frontend

### Step 2: Docker Compose (2026-03-28)
- [x] PostgreSQL 16, Zookeeper, Kafka, Dockerfiles, healthchecks

### Step 3: Auth Service (2026-03-28)
- [x] User/RefreshToken entities, JWT (access 15min + refresh 7d), Spring Security
- [x] POST register/login/refresh, GET me; Flyway V1; integration tests

### Step 4: API Gateway (2026-03-28)
- [x] Spring Cloud Gateway routes (6), JWT filter, CORS, X-User-Id/Role/Email forwarding

### Step 5: Deal Service — Create and Read Deals (2026-03-28)
- [x] Deal entity: UUID, title, description, amount, currency, depositor/beneficiary IDs, status, timestamps
- [x] DealEvent entity: audit log with event_type, actor, status transition, JSONB payload
- [x] DealStatus enum: CREATED, FUNDED, DELIVERED, RELEASING, COMPLETED, CANCELLED, DISPUTED, RESOLVED
- [x] DealStateMachine: static transition map with validate() — full lifecycle transitions defined
- [x] DealRepository: find by depositor/beneficiary, ordered by created_at desc
- [x] DealService: createDeal, getDeal, getDealEvents, listDeals, cancelDeal + event recording
- [x] DealController (reads X-User-Id/X-User-Role from gateway headers):
  - `POST /api/deals` — create deal (201)
  - `GET /api/deals` — list user's deals
  - `GET /api/deals/{id}` — deal details (403 if not party)
  - `GET /api/deals/{id}/events` — deal event history
  - `POST /api/deals/{id}/cancel` — cancel deal (409 if invalid transition)
- [x] Flyway V1: deals + deal_events tables with indexes in `deals` schema
- [x] Integration test (5 cases): create+get, list, cancel+idempotency, access denied, health
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL (all modules)
- [x] `./gradlew :deal-service:compileTestJava` — compiles

## Pending Steps

| Step | Name | Status |
|------|------|--------|
| 6 | Payment service — mock accounts + escrow | ⬜ |
| 7 | Deal-Payment Kafka integration | ⬜ |
| 8 | Frontend — auth pages | ⬜ |
| 9 | Frontend — deal flow UI | ⬜ |
| 10 | Notification service | ⬜ |
| 11 | Operator + admin roles | ⬜ |
| 12 | Dispute flow | ⬜ |
| 13 | Observability stack | ⬜ |
