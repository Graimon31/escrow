#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Создан .env из .env.example"
fi

docker compose up -d

echo "Стек запущен. Ожидание готовности..."
./scripts/smoke-check.sh

echo "Локальный MVP готов к проверке:"
echo "- Frontend: http://localhost:3000"
echo "- Grafana:  http://localhost:3001"
echo "- Kibana:   http://localhost:5601"
echo "- Swagger deal-service: http://localhost:8080/swagger-ui.html"
