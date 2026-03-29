#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Создан .env из .env.example"
fi

export COMPOSE_PARALLEL_LIMIT="${COMPOSE_PARALLEL_LIMIT:-2}"

echo "Сборка образов (COMPOSE_PARALLEL_LIMIT=${COMPOSE_PARALLEL_LIMIT})..."
docker compose build

echo "Запуск стека..."
docker compose up -d

echo "Стек запущен. Ожидание готовности..."
./scripts/smoke-check.sh

echo "Локальный MVP готов к проверке:"
echo "- Frontend: http://localhost:3000"
echo "- Grafana:  http://localhost:3001"
echo "- Kibana:   http://localhost:5601"
echo "- Swagger deal-service: http://localhost:8080/swagger-ui.html"
