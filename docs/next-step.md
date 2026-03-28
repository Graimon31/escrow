# Next Step: Step 2 — Docker Compose Infrastructure Layer

## Goal
All infrastructure runs locally via Docker Compose. PostgreSQL with per-service schemas, Kafka with Zookeeper.

## Tasks
1. Create `docker-compose.yml` with PostgreSQL 16, Kafka (Bitnami), Zookeeper
2. Create `init-db/init.sql` to create schemas: `auth`, `deals`, `payments`, `notifications`
3. Add Dockerfiles for each Java service (multi-stage: gradle build → eclipse-temurin:21-jre)
4. Add Dockerfile for frontend (multi-stage: npm build → node:22-alpine)
5. Add service containers to docker-compose.yml with correct ports and depends_on
6. Configure services' `application.yml` with datasource URLs for Dockerized Postgres

## Verification
```bash
docker compose up -d postgres kafka zookeeper     # infra starts
docker compose exec postgres psql -U escrow -c '\dn'  # schemas exist
docker compose up -d                               # all services start
curl http://localhost:8080/actuator/health          # gateway responds
curl http://localhost:8081/actuator/health          # auth responds
```

## Expected Outcome
- `docker compose up` brings up entire stack
- PostgreSQL has 4 schemas ready
- Kafka broker is reachable on :9092
- All 5 Java services + frontend accessible on their ports
