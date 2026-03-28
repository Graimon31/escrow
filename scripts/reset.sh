#!/usr/bin/env bash
set -euo pipefail

docker compose down -v --remove-orphans

echo "Окружение сброшено (контейнеры и volume удалены)."
