# Тестирование (Testing)

## Автотесты
- Unit: `mvn -f backend/deal-service/pom.xml test`
- Smoke: `./scripts/smoke-check.sh`

## Ручные проверки
1. Открыть dashboard: http://localhost:3000
2. Создать сделку через Swagger deal-service.
3. Перевести сделку по статусам endpoint `/api/v1/deals/{id}/transition`.
4. Проверить метрики в Grafana и Prometheus.
5. Проверить логи в Kibana.
