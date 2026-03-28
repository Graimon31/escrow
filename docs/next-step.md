# Следующий шаг (Next Step)

## Шаг 8 — Notification/Audit hardening + close flow

Сделать:
1. Подключить `notification-service` к событиям dispute/resolution.
2. Добавить автоматический переход deal/account в `CLOSED` после подтверждённого завершения расчётов.
3. Вынести payout/refund mock в отдельный payment-adapter интерфейс.
4. Добавить интеграционные end-to-end тесты полного цикла с Kafka/Testcontainers.
5. Расширить audit trail межсервисной корреляцией (`traceId`, `idempotencyKey`).
