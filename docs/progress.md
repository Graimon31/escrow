# Прогресс реализации (Progress)

## Шаги 1-7
Статус: выполнены (каркас, business flows до release/refund/dispute).

## Шаг 8 — Эксплуатационная готовность локального MVP
Статус: выполнен.

Сделано:
- Prometheus metrics scrape для сервисов;
- Grafana provisioning (datasource + dashboard `Escrow MVP Overview`);
- structured logs через `LOGGING_PATTERN_CONSOLE`;
- Elasticsearch + Kibana log flow через `filebeat`;
- завершены `README.requirements.md`, `README.testing.md`, `README.operations.md`;
- добавлен GitHub Actions CI workflow;
- расширены smoke/e2e скрипты;
- one-command bootstrap (`./scripts/start-local.sh`) выполняет запуск + smoke-проверку;
- финализированы URL для Swagger/Grafana/Kibana и документация.

Критерии готовности:
- Swagger доступен;
- Grafana доступна;
- Kibana доступна;
- тестовые пользователи есть;
- smoke/e2e есть;
- документация завершена.
