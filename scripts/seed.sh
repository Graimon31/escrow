#!/usr/bin/env bash
set -euo pipefail

cat <<'USERS'
Тестовые пользователи (seed users из auth-service):
- depositor / depositor123 (DEPOSITOR)
- beneficiary / beneficiary123 (BENEFICIARY)
- operator / operator123 (OPERATOR)
- admin / admin123 (ADMIN)
USERS

echo "Проверка логина depositor..."
TOKEN=$(curl -fsS -X POST "http://localhost:8081/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d '{"username":"depositor","password":"depositor123"}' | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

if [[ -z "$TOKEN" ]]; then
  echo "Не удалось получить токен. Убедитесь, что auth-service запущен." >&2
  exit 1
fi

echo "Seed check OK: токен получен."
