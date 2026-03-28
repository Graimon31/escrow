# Прогресс реализации (Progress)

## Шаг 1 — Базовый каркас репозитория
Статус: выполнен.

## Шаг 2 — Рабочий infrastructure skeleton
Статус: выполнен.

## Шаг 3 — Auth contour и базовая ролевая модель
Статус: выполнен.

## Шаг 4 — Первый бизнесовый вертикальный срез (deal + escrow-account)
Статус: выполнен.

## Шаг 5 — Funding slice и первое event-driven взаимодействие
Статус: выполнен.

Сделано:
- реализован `funding-service` с mock deposit flow и idempotency;
- добавлены funding статусы и переходы в deal/escrow состояниях;
- настроен Kafka topic `escrow.funding.events`;
- реализована публикация событий из `funding-service`;
- реализовано потребление событий в `deal-service` и `escrow-account-service`;
- добавлен audit trail (хранение и API чтения);
- фронтенд карточки сделки дополнен кнопкой внесения средств и отображением audit trail;
- docker-compose и smoke-check обновлены под funding-service;
- добавлены/обновлены тесты для funding/deal/escrow переходов.

Ограничения:
- внешний платёжный шлюз не подключён (используется mock flow);
- нет гарантированной exactly-once доставки (допустимо для MVP шага).
