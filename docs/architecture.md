# Архитектура проекта (Project Architecture)

## Текущий этап
Первый шаг: сформирован запускаемый каркас (skeleton) без полной бизнес-логики.

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

Каждый сервис на текущем шаге содержит:
- минимальное Spring Boot приложение;
- endpoint проверки доступности `/api/v1/health`;
- endpoint Actuator `/actuator/health`.

## Технологический стек (Technology Stack)
- Backend: Java 21 + Spring Boot + Gradle.
- Frontend: Next.js + TypeScript + Tailwind CSS.
- Infra: Docker Compose (PostgreSQL, Kafka, Prometheus, Grafana, Elasticsearch, Kibana).

## Принцип запуска
Основной локальный сценарий: `./scripts/start-local.sh`.

## Ограничения первого шага
- Бизнес-сценарии счёта эскроу (escrow account) не реализованы.
- Межсервисное взаимодействие и события Kafka не реализованы.
- Базы данных сервисов и миграции не реализованы.
