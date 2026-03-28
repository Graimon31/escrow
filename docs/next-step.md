# Следующий шаг (Next Step)

## Шаг 3 — Вертикальный срез сделки и счёта эскроу (Deal + Escrow Account Vertical Slice)

Сделать:
1. Добавить в compose запуск `escrow-account-service` рядом с `deal-service`.
2. Подключить PostgreSQL-конфигурацию для `deal-service` и `escrow-account-service`.
3. Добавить миграции БД (Flyway/Liquibase) для минимальных таблиц сделки и счёта.
4. Реализовать базовые endpoint:
   - создание сделки в состоянии DRAFT;
   - согласование до AGREED;
   - открытие счёта до OPENED.
5. Добавить springdoc-openapi и Swagger UI для этих двух сервисов.
6. Добавить unit + integration tests для happy-path вертикального среза.
