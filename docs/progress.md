# Progress

## Completed Steps

### Step 0: Project Planning (2026-03-28)
- [x] Created CLAUDE.md (working contract)
- [x] Created docs/architecture.md, progress.md, next-step.md
- [x] Defined microservice decomposition, deal state machine, entity models

### Step 1: Project Scaffolding and Build Infrastructure (2026-03-28)
- [x] Gradle wrapper, version catalog, root build config (Java 21, Spring Boot 3.4.1)
- [x] 5 Spring Boot modules + Next.js 14 frontend scaffolded
- [x] `./gradlew build` + `npm run build` — pass

### Step 2: Docker Compose Infrastructure Layer (2026-03-28)
- [x] docker-compose.yml: PostgreSQL 16, Zookeeper, Kafka, all services + frontend
- [x] init-db/init.sql, Dockerfiles for all services, healthchecks
- [x] `docker compose config` — valid

### Step 3: Auth Service — Registration and JWT Login (2026-03-28)
- [x] Dependencies: Spring Security, Data JPA, Flyway, PostgreSQL, jjwt 0.12.6
- [x] Flyway migration V1: `users` + `refresh_tokens` tables in `auth` schema
- [x] Entities: `User` (UUID, email, password_hash, full_name, role, enabled), `RefreshToken`
- [x] `Role` enum: DEPOSITOR, BENEFICIARY, OPERATOR, ADMINISTRATOR
- [x] `JwtProvider`: access token (15min), refresh token (7d), HMAC-SHA signing
- [x] `JwtAuthenticationFilter`: extracts Bearer token, sets SecurityContext
- [x] `SecurityConfig`: stateless sessions, 401 for unauthenticated, permit auth + actuator endpoints
- [x] `AuthService`: register (BCrypt), login, refresh, getCurrentUser
- [x] `AuthController`: POST register/login/refresh, GET me
- [x] DTOs: RegisterRequest, LoginRequest, RefreshRequest, AuthResponse, UserResponse
- [x] Integration test with Testcontainers (5 tests: register+login+me+refresh, duplicate email, wrong password, no token, health)
- [x] Version catalog updated: jjwt, testcontainers, spring-boot-testcontainers
- [x] `./gradlew build -x test` — BUILD SUCCESSFUL (all modules compile)
- [x] `./gradlew :auth-service:compileTestJava` — test code compiles
- [ ] Integration tests require Docker daemon (Testcontainers) — deferred to runtime

## Pending Steps

| Step | Name | Status |
|------|------|--------|
| 4 | API Gateway — routing + JWT validation | ⬜ |
| 5 | Deal service — create + read deals | ⬜ |
| 6 | Payment service — mock accounts + escrow | ⬜ |
| 7 | Deal-Payment Kafka integration | ⬜ |
| 8 | Frontend — auth pages | ⬜ |
| 9 | Frontend — deal flow UI | ⬜ |
| 10 | Notification service | ⬜ |
| 11 | Operator + admin roles | ⬜ |
| 12 | Dispute flow | ⬜ |
| 13 | Observability stack | ⬜ |
