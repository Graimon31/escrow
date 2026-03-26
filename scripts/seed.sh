#!/usr/bin/env bash
set -euo pipefail
curl -u depositor:depositor123 -sS -X POST http://localhost:8081/api/v1/deals \
  -H 'Content-Type: application/json' \
  -d '{"depositor":"depositor","beneficiary":"beneficiary","amount":1200.00,"currency":"USD","subject":"MVP escrow deal"}'
