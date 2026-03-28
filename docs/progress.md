# Прогресс реализации (Progress)

## Шаг 1 — Базовый каркас репозитория
Статус: выполнен.

## Шаг 2 — Рабочий infrastructure skeleton
Статус: выполнен.

## Шаг 3 — Auth contour и базовая ролевая модель
Статус: выполнен.

## Шаг 4 — Первый бизнесовый вертикальный срез (deal + escrow-account)
Статус: выполнен.

Сделано:
- реализованы `deal-service` и `escrow-account-service` на Spring Boot + JPA + Flyway + Swagger;
- добавлены миграции БД для таблиц `deals` и `escrow_accounts`;
- реализованы сущности и state transitions:
  - Deal: `DRAFT -> AGREED -> ACCOUNT_OPENED -> AWAITING_FUNDING`;
  - Escrow Account: `OPENED -> AWAITING_DEPOSIT`;
- реализованы API:
  - создание/получение/согласование сделки;
  - открытие счёта эскроу;
- добавлена валидация запрещённых переходов;
- обновлён frontend: список сделок, создание сделки, карточка сделки с кнопками переходов;
- добавлены минимальные тесты state machine и сервисных переходов;
- docker-compose обновлён для запуска `escrow-account-service`.

Ограничения:
- funding/fulfillment/dispute пока не реализованы глубоко;
- нет полноценной интеграции событий Kafka в доменный flow.
