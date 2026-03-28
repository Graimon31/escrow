# Эксплуатация (Operations)

## One-command bootstrap
```bash
./scripts/start-local.sh
```
Скрипт:
1. создаёт `.env` из `.env.example` (если отсутствует);
2. запускает весь стек `docker compose up -d`;
3. запускает smoke-check и печатает ключевые URL.

## Управление окружением
- Остановка: `./scripts/stop.sh`
- Полный сброс: `./scripts/reset.sh`
- Проверка seed users + auth login: `./scripts/seed.sh`

## Наблюдаемость (Observability)
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001` (логин из `.env`: `GRAFANA_ADMIN_USER/PASSWORD`)
- Kibana: `http://localhost:5601`
- Elasticsearch: `http://localhost:9200`

### Grafana dashboards
Дашборд provisioned автоматически:
- `Escrow MVP Overview`

### Structured logs + ELK flow
- backend сервисы печатают структурированный (key=value) консольный лог через `LOGGING_PATTERN_CONSOLE`;
- Filebeat читает docker container logs и отправляет в Elasticsearch index `escrow-logs-*`;
- Kibana использует эти индексы для поиска и фильтрации.

## Swagger URL
- Auth: `http://localhost:8081/swagger-ui.html`
- Deal: `http://localhost:8080/swagger-ui.html`
- Escrow-account: `http://localhost:8082/swagger-ui.html`
- Funding: `http://localhost:8083/swagger-ui.html`
- Fulfillment: `http://localhost:8084/swagger-ui.html`
- Review: `http://localhost:8085/swagger-ui.html`
- Dispute: `http://localhost:8086/swagger-ui.html`
- Resolution: `http://localhost:8087/swagger-ui.html`
