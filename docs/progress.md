# Прогресс реализации (Progress)

## Шаг 1 — Базовый каркас репозитория
Статус: выполнен.

Результат:
- созданы структуры backend/frontend/docs/scripts;
- зафиксированы Gradle и Tailwind CSS;
- определены сервисные границы.

## Шаг 2 — Рабочий infrastructure skeleton
Статус: выполнен.

Сделано:
- docker-compose расширен до минимально запускаемого окружения;
- поднимаются PostgreSQL, Kafka, Prometheus, Grafana, Elasticsearch, Kibana;
- в compose добавлены backend/frontend skeleton;
- smoke-check проверяет доступность инфраструктуры и health endpoint.

## Шаг 3 — Auth contour и базовая ролевая модель
Статус: выполнен.

Сделано:
- реализован `auth-service` с login endpoint и JWT-аутентификацией для MVP;
- добавлены seed users с ролями: DEPOSITOR, BENEFICIARY, OPERATOR, ADMIN;
- добавлены backend role guards (`@PreAuthorize`) на защищённые endpoint;
- добавлены frontend role guards через middleware и защищённые страницы cabinet;
- добавлен Swagger/OpenAPI для auth endpoints;
- добавлены минимальные backend tests для login и ролевых ограничений;
- compose обновлён для запуска auth-service.

Ограничения:
- полноценный бизнесовый workflow escrow ещё не реализован;
- users пока хранятся в seed-конфиге (in-memory) без БД.
