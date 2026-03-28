# Следующий шаг (Next Step)

## Шаг 5 — Funding срез и депонирование средств (Funding Slice)

Сделать:
1. Реализовать `funding-service` и endpoint депонирования средств.
2. Добавить переходы `AWAITING_FUNDING -> FUNDING_PROCESSING -> FUNDS_SECURED`.
3. Добавить idempotency для операции депонирования.
4. Добавить публикацию базового доменного события о фондировании (Kafka).
5. Добавить UI-индикатор статуса фондирования в карточке сделки.
6. Добавить unit/integration тесты для funding-flow.
