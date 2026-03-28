# Следующий шаг (Next Step)

## Шаг 4 — Первый бизнесовый вертикальный срез сделки (Deal Vertical Slice)

Сделать:
1. Подключить `deal-service` к PostgreSQL и добавить миграции БД.
2. Реализовать создание сделки в состоянии DRAFT.
3. Реализовать переход сделки в AGREED с проверкой ролей (DEPOSITOR/BENEFICIARY).
4. Защитить endpoint сделки через JWT из `auth-service`.
5. Добавить Swagger/OpenAPI для `deal-service`.
6. Добавить unit и integration tests для happy-path и role-based отказов.
