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

### Step 6: Payment Service — Mock Accounts and Escrow Hold (2026-03-28)
- [x] Account entity: UUID, user_id (unique), balance (default 10000 RUB), currency
- [x] Transaction entity: deal_id, from/to account, amount, type, status, timestamps
- [x] EscrowHold entity: deal_id (unique), amount, status (HELD/RELEASED/REFUNDED)
- [x] Enums: TransactionType (DEPOSIT_TO_ESCROW, RELEASE_TO_BENEFICIARY, REFUND_TO_DEPOSITOR),
  TransactionStatus (PENDING, COMPLETED, FAILED), EscrowHoldStatus (HELD, RELEASED, REFUNDED)
- [x] AccountRepository with pessimistic locking (`findByUserIdForUpdate`)
- [x] PaymentService: getOrCreateAccount, holdFunds, releaseFunds, refundFunds
  - Idempotent hold/release (duplicate calls are no-ops)
  - Insufficient funds check on hold
  - Pessimistic locking prevents race conditions
- [x] PaymentController: GET /api/payments/account, GET /api/payments/transactions
- [x] InternalPaymentController: POST hold/release/refund (for inter-service calls)
- [x] Flyway V1: accounts, escrow_holds, transactions tables in `payments` schema
- [x] Integration test (5 cases): account creation, hold+release, insufficient funds, idempotency, health
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL (all modules)

## Pending Steps

| Step | Name | Status |
|------|------|--------|
| 7 | Deal-Payment Kafka integration | ⬜ |
| 8 | Frontend — auth pages | ⬜ |
| 9 | Frontend — deal flow UI | ⬜ |
| 10 | Notification service | ⬜ |
| 11 | Operator + admin roles | ⬜ |
| 12 | Dispute flow | ⬜ |
| 13 | Observability stack | ⬜ |
