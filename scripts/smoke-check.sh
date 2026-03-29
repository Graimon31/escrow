#!/usr/bin/env bash
set -euo pipefail

RETRIES="${SMOKE_RETRIES:-30}"
SLEEP_SECONDS="${SMOKE_SLEEP_SECONDS:-2}"

check_http() {
  local name="$1"
  local url="$2"

  echo "Проверка $name -> $url"
  local attempt=1
  while (( attempt <= RETRIES )); do
    if curl -fsS "$url" >/dev/null; then
      return 0
    fi
    echo "  попытка ${attempt}/${RETRIES} неуспешна, ждём ${SLEEP_SECONDS}с..."
    sleep "$SLEEP_SECONDS"
    ((attempt++))
  done

  echo "Проверка '$name' не пройдена после ${RETRIES} попыток" >&2
  return 1
}

check_cmd() {
  local name="$1"
  shift

  echo "Проверка $name"
  local attempt=1
  while (( attempt <= RETRIES )); do
    if "$@" >/dev/null; then
      return 0
    fi
    echo "  попытка ${attempt}/${RETRIES} неуспешна, ждём ${SLEEP_SECONDS}с..."
    sleep "$SLEEP_SECONDS"
    ((attempt++))
  done

  echo "Проверка '$name' не пройдена после ${RETRIES} попыток" >&2
  return 1
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

check_http "Swagger deal-service" "http://localhost:8080/swagger-ui.html"
check_http "Swagger auth-service" "http://localhost:8081/swagger-ui.html"

check_http "Frontend" "http://localhost:3000"
check_http "Prometheus" "http://localhost:9090/-/healthy"
check_http "Grafana" "http://localhost:3001/api/health"
check_http "Kibana status" "http://localhost:5601/api/status"

echo "Smoke-check завершён успешно."
