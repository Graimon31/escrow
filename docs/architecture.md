# Архитектура проекта (Project Architecture)

## Текущий этап
Эксплуатационно проверяемый локальный MVP:
- auth;
- deal + escrow-account + funding (event-driven);
- fulfillment + review;
- dispute + resolution;
- observability (Prometheus + Grafana dashboards + structured logs + Elasticsearch/Kibana).

## Event-driven контуры
Kafka topics:
- `escrow.funding.events`
- `escrow.fulfillment.events`
- `escrow.review.events`
- `escrow.dispute.events`
- `escrow.resolution.events`

`deal-service` обрабатывает funding/fulfillment/review/resolution события.
`escrow-account-service` обрабатывает funding/resolution события.
`dispute-service` закрывает OPEN-спор по resolution событию.

## Наблюдаемость
- Метрики: `/actuator/prometheus` у backend-сервисов.
- Prometheus scrapes сервисы + агрегирует метрики.
- Grafana с provisioning datasource/dashboard (`Escrow MVP Overview`).
- Structured logs (key=value паттерн) в stdout контейнеров.
- Filebeat -> Elasticsearch (`escrow-logs-*`) -> Kibana.

## Финальные статусы (MVP)
### Deal
- `RELEASED`
- `REFUNDED`
- `CLOSED` (зарезервирован для следующего шага close-flow)

### EscrowAccount
- `RELEASED_TO_BENEFICIARY`
- `REFUNDED_TO_DEPOSITOR`
- `CLOSED` (зарезервирован для следующего шага close-flow)
