#!/usr/bin/env bash
set -euo pipefail

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Требуется команда '$1', но она не найдена." >&2
    exit 1
  fi
}

require_cmd curl
require_cmd jq

AUTH_URL="${AUTH_URL:-http://localhost:8081}"
DEAL_URL="${DEAL_URL:-http://localhost:8080}"
FUNDING_URL="${FUNDING_URL:-http://localhost:8083}"
FULFILLMENT_URL="${FULFILLMENT_URL:-http://localhost:8084}"
REVIEW_URL="${REVIEW_URL:-http://localhost:8085}"
RESOLUTION_URL="${RESOLUTION_URL:-http://localhost:8087}"

login() {
  local username="$1"
  local password="$2"
  curl -fsS -X POST "${AUTH_URL}/api/v1/auth/login" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"${username}\",\"password\":\"${password}\"}" | jq -r '.token'
}

echo "[e2e] Логин под depositor/operator/beneficiary"
DEPOSITOR_TOKEN="$(login depositor depositor123)"
OPERATOR_TOKEN="$(login operator operator123)"
BENEFICIARY_TOKEN="$(login beneficiary beneficiary123)"

[[ -n "$DEPOSITOR_TOKEN" && "$DEPOSITOR_TOKEN" != "null" ]]
[[ -n "$OPERATOR_TOKEN" && "$OPERATOR_TOKEN" != "null" ]]
[[ -n "$BENEFICIARY_TOKEN" && "$BENEFICIARY_TOKEN" != "null" ]]

echo "[e2e] Создание сделки"
DEAL_ID="$(curl -fsS -X POST "${DEAL_URL}/api/v1/deals" \
  -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d '{"title":"E2E Deal","amount":1000,"currency":"RUB","beneficiaryUsername":"beneficiary"}' | jq -r '.id')"

[[ -n "$DEAL_ID" && "$DEAL_ID" != "null" ]]

echo "[e2e] agree + open escrow account"
curl -fsS -X POST "${DEAL_URL}/api/v1/deals/${DEAL_ID}/agree" -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" >/dev/null
curl -fsS -X POST "${DEAL_URL}/api/v1/deals/${DEAL_ID}/open-escrow-account" -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" >/dev/null

echo "[e2e] deposit funds"
curl -fsS -X POST "${FUNDING_URL}/api/v1/funding/deposit" \
  -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" \
  -H 'Content-Type: application/json' \
  -H "Idempotency-Key: e2e-${DEAL_ID}" \
  -d "{\"dealId\":\"${DEAL_ID}\",\"amount\":1000,\"currency\":\"RUB\"}" >/dev/null

echo "[e2e] submit fulfillment"
curl -fsS -X POST "${FULFILLMENT_URL}/api/v1/fulfillment/submit" \
  -H "Authorization: Bearer ${BENEFICIARY_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d "{\"dealId\":\"${DEAL_ID}\",\"summary\":\"Работы завершены\",\"documents\":[{\"name\":\"act.pdf\",\"storagePath\":\"/tmp/act.pdf\",\"contentType\":\"application/pdf\",\"sizeBytes\":1234}]}" >/dev/null

echo "[e2e] review accept"
curl -fsS -X POST "${REVIEW_URL}/api/v1/review/action" \
  -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d "{\"dealId\":\"${DEAL_ID}\",\"action\":\"ACCEPT\",\"comment\":\"Принято\"}" >/dev/null

echo "[e2e] resolution release"
curl -fsS -X POST "${RESOLUTION_URL}/api/v1/resolution/decide" \
  -H "Authorization: Bearer ${OPERATOR_TOKEN}" \
  -H 'Content-Type: application/json' \
  -d "{\"dealId\":\"${DEAL_ID}\",\"outcome\":\"RELEASE\",\"comment\":\"Выплатить бенефициару\"}" >/dev/null

echo "[e2e] Проверка финального статуса сделки"
for _ in {1..20}; do
  STATE="$(curl -fsS "${DEAL_URL}/api/v1/deals/${DEAL_ID}" -H "Authorization: Bearer ${DEPOSITOR_TOKEN}" | jq -r '.state')"
  if [[ "$STATE" == "RELEASED" ]]; then
    echo "[e2e] Успех: deal=${DEAL_ID} state=${STATE}"
    exit 0
  fi
  sleep 1
done

echo "[e2e] Ошибка: не дождались RELEASED, последний state=${STATE}" >&2
exit 1
