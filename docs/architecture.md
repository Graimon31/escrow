# Архитектура проекта (Project Architecture)

## Текущий этап
Сформирован запускаемый инфраструктурный каркас и реализован рабочий контур аутентификации (auth contour) для MVP.

## Контуры микросервисов (Microservice Boundaries)
Минимальные сервисы (границы зафиксированы):
- deal-service
- escrow-account-service
- funding-service
- fulfillment-service
- review-service
- resolution-service
- dispute-service
- document-service
- notification-service
- audit-service
- auth-service

Текущее состояние:
- `auth-service` реализован как рабочий контур login + JWT + role guards + Swagger/OpenAPI;
- `deal-service` и `frontend` запускаются в compose как skeleton;
- остальные сервисы пока остаются кодовыми skeleton-модулями.

## Технологический стек (Technology Stack)
- Backend: Java 21 + Spring Boot + Spring Security + Gradle.
- Frontend: Next.js + TypeScript + Tailwind CSS.
- Infra: Docker Compose (PostgreSQL, Kafka, Prometheus, Grafana, Elasticsearch, Kibana).

## Контур авторизации (Authorization Contour)
Роли:
- DEPOSITOR
- BENEFICIARY
- OPERATOR
- ADMIN

В `auth-service`:
- login endpoint выдаёт JWT;
- защищённые endpoint проверяют роли на backend;
- swagger доступен для тестирования auth API.

Во frontend:
- middleware и защищённые маршруты cabinet реализуют базовый role guard.

## Принцип запуска
Основной локальный сценарий: `./scripts/start-local.sh`.

## Ограничения текущего этапа
- users пока seed/in-memory, без persistence в БД;
- бизнес-сценарии escrow deal/account пока не реализованы;
- межсервисные взаимодействия и state machine домена будут добавлены на следующем шаге.
