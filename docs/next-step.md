# Next Step: Step 5 — Deal Service: Create and Read Deals

## Goal
Depositor can create an escrow deal; both parties can view their deals. Core domain service with deal state machine.

## Tasks
1. Add dependencies: Spring Data JPA, Flyway, PostgreSQL, Validation
2. Flyway migration V1: `deals` and `deal_events` tables in `deals` schema
3. Entities: `Deal`, `DealEvent`, `DealStatus` enum
4. `DealStateMachine`: validates allowed state transitions
5. `DealService`: create deal, get deal, list deals (filtered by user role)
6. `DealController`: reads X-User-Id/X-User-Role headers from gateway
   - `POST /api/deals` — create (depositor)
   - `GET /api/deals` — list my deals
   - `GET /api/deals/{id}` — deal details
   - `POST /api/deals/{id}/cancel` — cancel deal
7. Integration tests with Testcontainers

## Verification
```bash
./gradlew :deal-service:compileJava
./gradlew build -x test
# With running stack: create deal via gateway with JWT → 201
```
