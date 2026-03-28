#!/usr/bin/env bash
set -euo pipefail

check() {
  local name="$1"
  local url="$2"
  echo "Проверка $name -> $url"
  curl -fsS "$url" >/dev/null
}

check "PostgreSQL порт" "http://localhost:5432" || true
check "Elasticsearch" "http://localhost:9200"
check "Kibana" "http://localhost:5601/status"
check "Prometheus" "http://localhost:9090/-/healthy"
check "Grafana" "http://localhost:3001/api/health"

echo "Smoke-check завершён."
