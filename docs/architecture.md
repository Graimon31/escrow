# Архитектура проекта (Project Architecture)

## Текущий этап
Запускаемый инфраструктурный каркас + auth contour + первый бизнесовый вертикальный срез сделки.

## Контуры микросервисов
Минимальные сервисы:
- deal-service
- escrow-account-service
- funding-service
- fulfillment-service
- review-service
- resolution-service
- dispute-service
- document-service
- notification-service
- audit-service
- auth-service

Текущее рабочее покрытие:
- `auth-service`: login, JWT, роли, guards, Swagger;
- `deal-service`: создание/согласование сделки, переходы состояния, открытие счёта;
- `escrow-account-service`: открытие счёта эскроу и статус счёта.

## Машины состояний (реализованная часть)
### Deal
- DRAFT
- AGREED
- ACCOUNT_OPENED
- AWAITING_FUNDING

### Escrow Account
- OPENED
- AWAITING_DEPOSIT

## Хранение данных
- PostgreSQL
- миграции Flyway в каждом сервисе:
  - `deal-service` (`deals`)
  - `escrow-account-service` (`escrow_accounts`)

## API
- Swagger/OpenAPI включён для `auth-service`, `deal-service`, `escrow-account-service`.

## Frontend
- UI на русском языке;
- реализованы страницы: список сделок, создание сделки, карточка сделки, auth/login, role-guard маршруты.

## Ограничения текущего этапа
- funding/fulfillment/dispute flow пока не реализованы;
- без глубокой event-driven интеграции между сервисами.
