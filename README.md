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

## Funding slice и event-driven поток
Реализован mock funding flow (без внешнего платёжного шлюза):
- `AWAITING_FUNDING -> FUNDING_PROCESSING -> FUNDS_SECURED` для сделки;
- `AWAITING_DEPOSIT -> DEPOSIT_IN_PROCESS -> HELD_IN_ESCROW` для счёта.

Kafka topic:
- `escrow.funding.events`

### Funding API
- `POST /api/v1/funding/deposit` — mock внесение средств
- `GET /api/v1/funding/audit/{dealId}` — audit trail по сделке

### Deal / Escrow API
- `POST /api/v1/deals/{id}/open-escrow-account`
- `POST /api/v1/deals/{id}/agree`
- `POST /api/v1/escrow-accounts/open`
- `GET /api/v1/escrow-accounts/by-deal/{dealId}`

### UI
- `/deals` — список + создание сделки
- `/deals/{id}` — карточка сделки + кнопка «Внести средства (mock)» + audit trail

## Доступные endpoint после запуска
- Frontend: http://localhost:3000
- Auth service: http://localhost:8081
- Deal service: http://localhost:8080
- Escrow-account service: http://localhost:8082
- Funding service: http://localhost:8083
- Swagger: `/swagger-ui.html` на каждом backend сервисе
