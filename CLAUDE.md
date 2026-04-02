# Escrow Platform — Working Contract

## Project
Escrow-first web product. Microservice architecture, locally runnable MVP.

## Tech Stack (fixed)
- **Backend:** Java 21, Spring Boot 3.x, Spring Web, Spring Security, Spring Data JPA, Hibernate, Gradle (Kotlin DSL)
- **Frontend:** TypeScript, Next.js 14, React, Tailwind CSS
- **Database:** PostgreSQL 16 (single instance, schema-per-service)
- **Messaging:** Apache Kafka
- **Observability:** Prometheus, Grafana, Elasticsearch, Kibana
- **Infra:** Docker Compose
- **CI:** GitHub Actions

## Roles
depositor (депонент), beneficiary (бенефициар), operator (оператор), administrator (администратор)

## Microservices
| Service | Port | Domain |
|---------|------|--------|
| api-gateway | 8080 | Routing, JWT validation, CORS |
| auth-service | 8081 | Registration, login, JWT, roles |
| deal-service | 8082 | Escrow deal lifecycle (core domain) |
| payment-service | 8083 | Mock accounts, escrow hold/release |
| notification-service | 8084 | Kafka consumer, in-app notifications |
| frontend | 3000 | Next.js UI |

## Deal State Machine
```
CREATED → FUNDED → DELIVERED → RELEASING → COMPLETED
                   DELIVERED → DISPUTED → RESOLVED
Any (except terminal) → CANCELLED
Terminal: COMPLETED, CANCELLED, RESOLVED
```

## Development Rules
1. Work incrementally — one step at a time
2. Each step must end in a runnable, testable state
3. Do not rewrite existing working code without reason
4. After each step: update `docs/progress.md` and `docs/next-step.md`
5. Run verification commands before marking step as complete
6. Commit after each completed step with descriptive message
7. Prefer editing existing files over creating new ones
8. Use Flyway for DB migrations (V1, V2, ... per service)
9. Integration tests use Testcontainers
10. All services expose `/actuator/health`

## Key Build Commands
```bash
./gradlew build                    # build all Java modules
./gradlew :auth-service:bootRun    # run single service
cd frontend && npm run dev         # run frontend
docker compose up -d               # start infrastructure
docker compose up                  # start everything
```

## Key Docs
- `docs/architecture.md` — architecture and design decisions
- `docs/progress.md` — completed steps tracker
- `docs/next-step.md` — what to do next
