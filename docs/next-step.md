# Следующий шаг (Next Step)

## Шаг 6 — Fulfillment и проверка депонентом (Fulfillment + Review Slice)

Сделать:
1. Реализовать `fulfillment-service` и переход в `AWAITING_BENEFICIARY_FULFILLMENT`.
2. Реализовать `review-service` и переход в `AWAITING_DEPOSITOR_REVIEW`.
3. Добавить события Kafka для fulfillment/review этапов.
4. Добавить базовый UI для подтверждения исполнения и проверки.
5. Добавить тесты переходов и role-guards для новых endpoint.
