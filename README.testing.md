# Тестирование (Testing)

## Локальные команды

### Backend unit tests
```bash
gradle -p backend test
```

### Frontend checks
```bash
npm --prefix frontend ci
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

### Smoke check (запущенный compose)
```bash
./scripts/smoke-check.sh
```
Проверяет (с retry ожиданием готовности сервисов):
- health endpoint'ы сервисов;
- Swagger доступность;
- Frontend/Grafana/Prometheus/Kibana URL.

### E2E happy-path
```bash
./scripts/e2e.sh
```
Сценарий покрывает:
- login;
- create/agree/open-account;
- deposit;
- fulfillment submit;
- review accept;
- resolution release;
- проверку финального статуса `RELEASED`.

## CI (GitHub Actions)
Workflow `.github/workflows/ci.yml` запускает:
- backend tests;
- frontend typecheck + build;
- docker compose config validation.
