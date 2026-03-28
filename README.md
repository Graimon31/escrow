# Платформа эскроу-first (Escrow-first Platform)

Локально запускаемый MVP микросервисной платформы со счётом эскроу (escrow account) как центральной сущностью.

## One-command bootstrap
```bash
./scripts/start-local.sh
```

## Реализованные срезы
- auth (JWT + роли);
- deal + escrow-account + funding (event-driven);
- fulfillment + review;
- dispute + resolution + финальные исходы (release/refund);
- observability (Prometheus, Grafana dashboards, structured logs, Elasticsearch + Kibana).

## Ключевые URL
- Frontend: `http://localhost:3000`
- Grafana: `http://localhost:3001`
- Prometheus: `http://localhost:9090`
- Kibana: `http://localhost:5601`
- Elasticsearch: `http://localhost:9200`

Swagger UI:
- auth-service: `http://localhost:8081/swagger-ui.html`
- deal-service: `http://localhost:8080/swagger-ui.html`
- escrow-account-service: `http://localhost:8082/swagger-ui.html`
- funding-service: `http://localhost:8083/swagger-ui.html`
- fulfillment-service: `http://localhost:8084/swagger-ui.html`
- review-service: `http://localhost:8085/swagger-ui.html`
- dispute-service: `http://localhost:8086/swagger-ui.html`
- resolution-service: `http://localhost:8087/swagger-ui.html`

## Тестовые пользователи
- `depositor / depositor123` (DEPOSITOR)
- `beneficiary / beneficiary123` (BENEFICIARY)
- `operator / operator123` (OPERATOR)
- `admin / admin123` (ADMIN)

## Проверки
```bash
./scripts/smoke-check.sh
./scripts/e2e.sh
```

См. также:
- `README.requirements.md`
- `README.testing.md`
- `README.operations.md`
