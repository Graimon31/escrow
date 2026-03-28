# Платформа эскроу-first (Escrow-first Platform)

## Быстрый старт
```bash
cp .env.example .env
./scripts/start-local.sh
./scripts/smoke-check.sh
```

## Реализованные срезы
- auth (JWT + роли)
- deal + escrow-account + funding (Kafka)
- fulfillment + review
- dispute + resolution + final outcomes (release/refund)

## Новые MVP-возможности шага
- Открытие спора: `POST /api/v1/disputes/open`
- Финальное решение: `POST /api/v1/resolution/decide`
- История споров/решений:
  - `GET /api/v1/disputes/{dealId}`
  - `GET /api/v1/resolution/history/{dealId}`
- Финальные состояния:
  - сделка: `RELEASED` / `REFUNDED`
  - счёт: `RELEASED_TO_BENEFICIARY` / `REFUNDED_TO_DEPOSITOR`

## UI
- `/fulfillment/{dealId}` — экран исполнения обязательства
- `/review/{dealId}` — экран проверки депонентом
- `/dispute/{dealId}` — экран спора
- `/outcome/{dealId}` — экран финального исхода
- `/deals/{dealId}` — карточка сделки со ссылками на экраны

## Основные backend адреса
- auth-service: `http://localhost:8081`
- deal-service: `http://localhost:8080`
- escrow-account-service: `http://localhost:8082`
- funding-service: `http://localhost:8083`
- fulfillment-service: `http://localhost:8084`
- review-service: `http://localhost:8085`
- dispute-service: `http://localhost:8086`
- resolution-service: `http://localhost:8087`
