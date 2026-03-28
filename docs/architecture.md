# Архитектура проекта (Project Architecture)

## Текущий этап
Запускаемый инфраструктурный каркас + auth contour + deal/escrow vertical slice + funding event-driven slice.

## Рабочие сервисы текущего этапа
- `auth-service`: login, JWT, роли, guards.
- `deal-service`: создание/согласование сделки, открытие счёта, обработка funding-событий.
- `escrow-account-service`: открытие счёта и обработка funding-событий.
- `funding-service`: mock внесение средств, публикация Kafka-событий, audit trail.

## Event-driven взаимодействие
Kafka topic:
- `escrow.funding.events`

Поток:
1. `funding-service` публикует `FUNDING_PROCESSING`.
2. `deal-service` переводит сделку в `FUNDING_PROCESSING`.
3. `escrow-account-service` переводит счёт в `DEPOSIT_IN_PROCESS`.
4. `funding-service` публикует `FUNDS_SECURED`.
5. `deal-service` переводит сделку в `FUNDS_SECURED`.
6. `escrow-account-service` переводит счёт в `HELD_IN_ESCROW`.

## Реализованные состояния
### Deal
- DRAFT
- AGREED
- ACCOUNT_OPENED
- AWAITING_FUNDING
- FUNDING_PROCESSING
- FUNDS_SECURED

### Escrow Account
- OPENED
- AWAITING_DEPOSIT
- DEPOSIT_IN_PROCESS
- HELD_IN_ESCROW

## Ограничения
- flow фондирования mock (без внешнего платёжного шлюза);
- события обрабатываются at-least-once моделью Kafka (для MVP приемлемо).
