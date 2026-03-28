#!/usr/bin/env bash
set -euo pipefail

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Создан .env из .env.example"
fi

docker compose up -d

echo "Локальный каркас запущен."
echo "Frontend skeleton: ./frontend"
echo "Backend skeleton: ./backend/services"
echo "Запустите smoke-check: ./scripts/smoke-check.sh"
