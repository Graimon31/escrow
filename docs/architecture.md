# Архитектура проекта (Project Architecture)

## Текущий этап
Запускаемый MVP с working slices:
- auth;
- deal + escrow-account + funding (event-driven);
- fulfillment + review.

## Fulfillment/Review contour
### Fulfillment
- endpoint для заявления исполнения бенефициаром;
- metadata документов хранится в БД;
- local storage path фиксируется для MVP.

### Review
- действия депонента:
  - ACCEPT
  - REJECT
  - CORRECTION
  - DISPUTE
- история review-действий сохраняется и доступна API.

## Event-driven взаимодействие
Kafka topics:
- `escrow.funding.events`
- `escrow.fulfillment.events`
- `escrow.review.events`

Deal-service обрабатывает:
- funding события;
- fulfillment событие;
- review события.

## Ограничения
- документы без полноценного object storage (только metadata + local path);
- workflow намеренно упрощён до MVP.
