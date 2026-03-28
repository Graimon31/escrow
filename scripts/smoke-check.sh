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
check_http "Funding service health" "http://localhost:8083/api/v1/health"
check_http "Fulfillment service health" "http://localhost:8084/api/v1/health"
check_http "Review service health" "http://localhost:8085/api/v1/health"
check_http "Dispute service health" "http://localhost:8086/api/v1/health"
check_http "Resolution service health" "http://localhost:8087/api/v1/health"
check_http "Frontend" "http://localhost:3000"
check_http "Prometheus" "http://localhost:9090/-/healthy"
check_http "Grafana" "http://localhost:3001/api/health"

echo "Smoke-check завершён успешно."
