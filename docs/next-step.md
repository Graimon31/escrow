# Следующий шаг (Next Step)

## Шаг 9 — Production-hardening MVP

Сделать:
1. Добавить alerting rules (Prometheus Alertmanager) и уведомления в `notification-service`.
2. Добавить закрытие сделок/счетов в `CLOSED` после подтверждённого completion.
3. Реализовать retry/DLQ стратегию для Kafka listeners.
4. Добавить интеграционные тесты на Testcontainers (Postgres + Kafka + Elasticsearch).
5. Усилить security профиль: CORS/headers/rate limit/secret management.
