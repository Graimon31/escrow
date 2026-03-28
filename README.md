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

## Новые MVP-возможности шага
- Бенефициар может заявить исполнение: `POST /api/v1/fulfillment/submit`
- Метаданные документов хранятся локально + в БД
- Депонент может принять решение: `accept/reject/correction/dispute`
- История действий доступна: `GET /api/v1/review/history/{dealId}`

## UI
- `/fulfillment/{dealId}` — экран исполнения обязательства
- `/review/{dealId}` — экран проверки депонентом
- `/deals/{dealId}` — карточка сделки со ссылками на экраны и audit trail

## Основные backend адреса
- auth-service: `http://localhost:8081`
- deal-service: `http://localhost:8080`
- escrow-account-service: `http://localhost:8082`
- funding-service: `http://localhost:8083`
- fulfillment-service: `http://localhost:8084`
- review-service: `http://localhost:8085`
