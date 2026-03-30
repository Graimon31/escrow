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

### Step 8: Frontend — Authentication Pages (2026-03-28)
- [x] API client (`src/lib/api.ts`): typed fetch wrapper, register/login/refresh/getMe
- [x] Auth context (`src/contexts/auth-context.tsx`): AuthProvider, useAuth hook, localStorage JWT storage
- [x] Login page (`/login`): email/password form, error handling, redirect to dashboard
- [x] Register page (`/register`): full name/email/password/role form, DEPOSITOR/BENEFICIARY selection
- [x] Dashboard page (`/dashboard`): protected route, user info cards, empty deals placeholder
- [x] Navbar component: logo, user name + role badge, logout button
- [x] Protected route component: auth check, loading spinner, redirect to /login
- [x] Root layout: AuthProvider wrapper, updated metadata
- [x] Home page (`/`): redirect to /dashboard or /login based on auth state
- [x] `npm run build` — BUILD SUCCESSFUL, `npm run lint` — no errors

### Step 9: Frontend — Deal Flow UI + State Machine Expansion (2026-03-30)
- [x] **9a**: Layout overhaul — sidebar (Dashboard/Deals/Documents/Notifications/Settings), top bar (search/notifications/user menu), mobile sidebar, AppShell wrapper
- [x] **9b**: Expanded deal state machine (8 → 15 states) per frontend ТЗ
  - New states: DRAFT, AWAITING_AGREEMENT, AGREED, AWAITING_FUNDING, FUNDING_PROCESSING, AWAITING_FULFILLMENT, AWAITING_REVIEW, REFUNDING, REFUNDED, CLOSED
  - New endpoints: submit, agree, decline, reject, dispute, resolve
  - Flyway V2 migration with data migration
- [x] **9c**: Deal flow UI pages
  - API client: deal types (15 statuses), list/get/create/action functions
  - DealStatusBadge: color-coded badges for all states
  - /deals: table with status/role filters
  - /deals/new: create deal form
  - /deals/[id]: detail page with info, timeline, role+state-aware action panel, confirmation modals
- [x] Placeholder pages: /documents, /notifications, /settings
- [x] `npm run build` (13 routes), `npm run lint` — clean
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL

### Step 10: Notification Service (2026-03-30)
- [x] Added Spring Kafka + Spring Data JPA + Flyway dependencies to notification-service
- [x] Notification entity (id, userId, type, title, message, dealId, read, createdAt)
- [x] Flyway V1 migration: notifications table with indexes (user_id, unread)
- [x] DealEventConsumer: Kafka listener on "deal-events" topic, creates notifications for relevant users
  - Handles all 12 event types (CREATED, SUBMITTED, AGREED, DECLINED, FUNDED, DELIVERED, COMPLETED, REFUNDED, DISPUTED, DISPUTE_RESOLVED_RELEASE/REFUND, CANCELLED)
  - Notifies correct user(s) per event type (some events notify both parties)
- [x] REST endpoints: GET /api/notifications, GET /api/notifications/unread-count, PATCH /api/notifications/{id}/read, POST /api/notifications/read-all
- [x] Frontend /notifications page: real notification list with type icons, mark read, mark all read, link to deal
- [x] Notification bell in top bar: unread count badge with 30s polling
- [x] application.yml + application-docker.yml for PostgreSQL (notifications schema), Kafka consumer config
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL
- [x] `cd frontend && npm run build` — BUILD SUCCESSFUL (11 routes)

### Step 11: Operator + Admin Roles (2026-03-30)
- [x] AdminController in auth-service: GET /api/admin/users, PATCH role, PATCH toggle enabled
- [x] OperatorController in deal-service: GET /api/operator/deals (with status filter), GET /api/operator/deals/stats
- [x] DealService: operators/admins can view any deal (bypass depositor/beneficiary check)
- [x] UserResponse: added `enabled` field
- [x] DealRepository: new query methods (findAllByOrder, findByStatus, countByStatus, countByStatusNotIn)
- [x] Frontend: /operator page — stats cards, deal table with filter, resolve dispute modal
- [x] Frontend: /admin page — user list, change role dropdown, enable/disable toggle
- [x] Sidebar: role-based navigation (Operator Panel for OPERATOR/ADMIN, Admin Panel for ADMIN only)
- [x] API routes already configured: /api/admin/** → auth-service, /api/operator/** → deal-service

### Step 12: Dispute Flow (2026-03-30)
- [x] Already implemented in Steps 9b + 11:
  - disputeDeal() — depositor/beneficiary can open dispute from AWAITING_FULFILLMENT/AWAITING_REVIEW
  - resolveDispute() — operator/admin resolves with RELEASE or REFUND
  - Frontend deal detail page shows dispute actions per role
  - Operator panel has "Resolve Dispute" button + modal

### Step 13: Observability Stack (2026-03-30)
- [x] Added micrometer-prometheus to all services (root build.gradle.kts)
- [x] All services expose /actuator/prometheus endpoint
- [x] Prometheus in docker-compose: scrapes all 5 services every 15s
- [x] Grafana in docker-compose: auto-provisioned with Prometheus + Elasticsearch datasources
- [x] Pre-built dashboard: "Escrow Platform Overview" (request rate, p95 latency, JVM heap, service health)
- [x] Elasticsearch 8.13 + Kibana 8.13 in docker-compose
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL
- [x] `cd frontend && npm run build` — BUILD SUCCESSFUL (13 routes)

## All Steps Complete

| Step | Name | Status |
|------|------|--------|
| 0 | Project planning | ✅ |
| 1 | Project scaffolding | ✅ |
| 2 | Docker Compose | ✅ |
| 3 | Auth service | ✅ |
| 4 | API Gateway | ✅ |
| 5 | Deal service | ✅ |
| 6 | Payment service | ✅ |
| 7 | Deal-Payment integration | ✅ |
| 8 | Frontend — auth pages | ✅ |
| 9 | Frontend — deal flow UI | ✅ |
| 10 | Notification service | ✅ |
| 11 | Operator + admin roles | ✅ |
| 12 | Dispute flow | ✅ |
| 13 | Observability stack | ✅ |
