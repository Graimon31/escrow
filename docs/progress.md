# Progress

## Completed Steps

### Step 0: Project Planning (2026-03-28)
- [x] Created CLAUDE.md, docs/architecture.md, progress.md, next-step.md
- [x] Defined microservice decomposition, deal state machine, entity models

### Step 1: Project Scaffolding and Build Infrastructure (2026-03-28)
- [x] Gradle wrapper, version catalog, root build config (Java 21, Spring Boot 3.4.1)
- [x] 5 Spring Boot modules + Next.js 14 frontend scaffolded

### Step 2: Docker Compose Infrastructure Layer (2026-03-28)
- [x] docker-compose.yml: PostgreSQL 16, Zookeeper, Kafka, all services + frontend
- [x] init-db/init.sql, Dockerfiles, healthchecks

### Step 3: Auth Service — Registration and JWT Login (2026-03-28)
- [x] User/RefreshToken entities, Role enum, BCrypt, Flyway V1
- [x] JwtProvider (access 15min + refresh 7d), JwtAuthenticationFilter, SecurityConfig
- [x] AuthController: POST register/login/refresh, GET me
- [x] Integration test (5 cases) with Testcontainers

### Step 4: API Gateway — Routing and JWT Validation (2026-03-28)
- [x] JwtUtil: lightweight JWT parser (validates only, same HMAC-SHA secret)
- [x] JwtAuthGatewayFilterFactory: reactive filter for Spring Cloud Gateway
  - Extracts Bearer token, validates, forwards X-User-Id/X-User-Role/X-User-Email headers
  - Returns 401 on missing/invalid token
- [x] CorsConfig: allows localhost:3000, credentials, standard methods/headers
- [x] Routes configured (6 routes):
  - `/api/auth/**` → auth-service:8081 (no JWT — public)
  - `/api/deals/**` → deal-service:8082 (JWT required)
  - `/api/payments/**` → payment-service:8083 (JWT required)
  - `/api/notifications/**` → notification-service:8084 (JWT required)
  - `/api/admin/**` → auth-service:8081 (JWT required)
  - `/api/operator/**` → deal-service:8082 (JWT required)
- [x] application-docker.yml: Docker service hostnames for routes
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL (all modules)

## Pending Steps

| Step | Name | Status |
|------|------|--------|
| 5 | Deal service — create + read deals | ⬜ |
| 6 | Payment service — mock accounts + escrow | ⬜ |
| 7 | Deal-Payment Kafka integration | ⬜ |
| 8 | Frontend — auth pages | ⬜ |
| 9 | Frontend — deal flow UI | ⬜ |
| 10 | Notification service | ⬜ |
| 11 | Operator + admin roles | ⬜ |
| 12 | Dispute flow | ⬜ |
| 13 | Observability stack | ⬜ |
