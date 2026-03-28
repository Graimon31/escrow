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

## Auth contour (контур аутентификации)
### Тестовые пользователи (seed users)
- `depositor / depositor123` → роль `DEPOSITOR`
- `beneficiary / beneficiary123` → роль `BENEFICIARY`
- `operator / operator123` → роль `OPERATOR`
- `admin / admin123` → роль `ADMIN`

### Основные endpoint
- Login: `POST http://localhost:8081/api/v1/auth/login`
- Profile: `GET http://localhost:8081/api/v1/auth/me` (Bearer token)
- Role-guard demo:
  - `GET /api/v1/auth/admin-zone` (только ADMIN)
  - `GET /api/v1/auth/operator-zone` (OPERATOR, ADMIN)
  - `GET /api/v1/auth/depositor-zone` (DEPOSITOR)
  - `GET /api/v1/auth/beneficiary-zone` (BENEFICIARY)
- Swagger UI auth-service: `http://localhost:8081/swagger-ui.html`

## Доступные endpoint после запуска
- Frontend skeleton: http://localhost:3000
- Auth service health: http://localhost:8081/api/v1/health
- Deal service health: http://localhost:8080/api/v1/health
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
- `frontend` — каркас Next.js + TypeScript + Tailwind CSS + role guards для MVP auth.
- `docker-compose.yml` — инфраструктура + поднимаемые skeleton-сервисы (`auth-service`, `deal-service`, `frontend`).
- `docs` — архитектура, прогресс, следующий шаг.
