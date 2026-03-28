# Следующий шаг (Next Step)

## Шаг 2 — Базовый вертикальный срез сделки эскроу (Escrow Deal Vertical Slice)

Сделать:
1. Выбрать 2 сервиса для первого потока: `deal-service` и `escrow-account-service`.
2. Добавить PostgreSQL-конфигурацию и миграции (Flyway/Liquibase) для этих сервисов.
3. Реализовать минимальные сущности и state machine переходы:
   - создание черновика сделки (DRAFT),
   - согласование до AGREED,
   - открытие escrow account до OPENED.
4. Добавить OpenAPI-описание публичных endpoint.
5. Добавить базовые unit-тесты и интеграционный тест для happy-path.
