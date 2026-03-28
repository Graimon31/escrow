# Требования (Requirements)

## Обязательные зависимости
- Docker Engine 24+.
- Docker Compose v2.
- `curl`.
- `jq` (для e2e/smoke сценариев).

## Для локальной разработки без Docker
- Java 21 + Gradle 8+.
- Node.js 20+ + npm 10+.

## Порты локального MVP
- 3000 — frontend.
- 3001 — Grafana.
- 5432 — PostgreSQL.
- 5601 — Kibana.
- 8080..8087 — backend API.
- 9090 — Prometheus.
- 9092 — Kafka.
- 9200 — Elasticsearch.

## Тестовые пользователи (seed users)
- `depositor / depositor123` (DEPOSITOR).
- `beneficiary / beneficiary123` (BENEFICIARY).
- `operator / operator123` (OPERATOR).
- `admin / admin123` (ADMIN).
