# Эксплуатация (Operations)

## Компоненты
- Kafka (брокер событий)
- PostgreSQL (основная база)
- Elasticsearch + Kibana (логи)
- Prometheus + Grafana (метрики)

## Проверки
- Health: `http://localhost:8081/actuator/health`
- Metrics: `http://localhost:8081/actuator/prometheus`
- Swagger: `http://localhost:8081/swagger-ui.html`

## Operational checks
- Проверить, что `docker compose ps` показывает `Up` для всех сервисов.
- Проверить доступность ключевых URL через `./scripts/smoke-check.sh`.
