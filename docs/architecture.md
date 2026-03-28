# Архитектура проекта (Project Architecture)

## Текущий этап
Запускаемый MVP с working slices:
- auth;
- deal + escrow-account + funding (event-driven);
- fulfillment + review;
- dispute + resolution + финальные исходы (release/refund).

## Event-driven контуры
Kafka topics:
- `escrow.funding.events`
- `escrow.fulfillment.events`
- `escrow.review.events`
- `escrow.dispute.events`
- `escrow.resolution.events`

Deal-service обрабатывает события funding/fulfillment/review/resolution и переводит сделку в финальные состояния.
Escrow-account-service обрабатывает funding/resolution события и поддерживает консистентные финальные состояния счёта.
Dispute-service ведёт реестр споров и автоматически закрывает OPEN-спор после resolution события.

## Финальные статусы
### Deal
- `RELEASED`
- `REFUNDED`
- `CLOSED` (техническое финальное состояние, доступно для следующего шага)

### EscrowAccount
- `RELEASED_TO_BENEFICIARY`
- `REFUNDED_TO_DEPOSITOR`
- `CLOSED` (резерв под следующий шаг)

## Ограничения шага
- фокус на корректность state transitions и консистентность;
- второстепенные сценарии (сложные SLA/таймеры/мульти-эскалации) не включены.
