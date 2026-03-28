#!/usr/bin/env bash
set -euo pipefail

check_http() {
  local name="$1"
  local url="$2"
  echo "Проверка $name -> $url"
  curl -fsS "$url" >/dev/null
}

check_cmd() {
  local name="$1"
  shift
  echo "Проверка $name"
  "$@" >/dev/null
}

check_cmd "PostgreSQL (pg_isready внутри контейнера)" docker compose exec -T postgres pg_isready -U "${POSTGRES_USER:-escrow}" -d "${POSTGRES_DB:-escrow}"
check_http "Auth service health" "http://localhost:8081/api/v1/health"
check_http "Deal service health" "http://localhost:8080/api/v1/health"
check_http "Escrow-account service health" "http://localhost:8082/api/v1/health"
check_http "Auth swagger" "http://localhost:8081/swagger-ui.html"
check_http "Deal swagger" "http://localhost:8080/swagger-ui.html"
check_http "Escrow swagger" "http://localhost:8082/swagger-ui.html"
check_http "Frontend" "http://localhost:3000"
check_http "Elasticsearch" "http://localhost:9200"
check_http "Kibana" "http://localhost:5601/status"
check_http "Prometheus" "http://localhost:9090/-/healthy"
check_http "Grafana" "http://localhost:3001/api/health"

echo "Smoke-check завершён успешно."
