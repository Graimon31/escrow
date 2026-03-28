# Платформа эскроу-first (Escrow-first Platform)

Локально запускаемый MVP-каркас микросервисной платформы с центральной сущностью счёта эскроу (escrow account).

## Быстрый старт (Quick Start)
1. Скопировать переменные окружения:
   ```bash
   cp .env.example .env
   ```
2. Поднять весь каркас одной командой:
   ```bash
   ./scripts/start-local.sh
   ```
3. Выполнить smoke-check:
   ```bash
   ./scripts/smoke-check.sh
   ```

## Доступные endpoint после запуска
- Frontend skeleton: http://localhost:3000
- Deal service health: http://localhost:8080/api/v1/health
- Deal service actuator health: http://localhost:8080/actuator/health
- PostgreSQL: localhost:5432
- Kafka: localhost:9092
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

## Остановка
```bash
./scripts/stop.sh
```

## Сброс окружения
```bash
./scripts/reset.sh
```

## Структура
- `backend/services` — каркасы backend-сервисов (Gradle + Spring Boot).
- `frontend` — каркас Next.js + TypeScript + Tailwind CSS.
- `docker-compose.yml` — инфраструктура + поднимаемые skeleton-сервисы (`deal-service`, `frontend`).
- `docs` — архитектура, прогресс, следующий шаг.
