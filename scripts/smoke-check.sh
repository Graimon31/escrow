#!/usr/bin/env bash
set -euo pipefail
urls=(
  "http://localhost:3000"
  "http://localhost:8081/actuator/health"
  "http://localhost:8081/swagger-ui.html"
  "http://localhost:3001"
  "http://localhost:5601"
)
for u in "${urls[@]}"; do
  echo "Checking $u"; curl -fsS "$u" >/dev/null
  echo "OK $u"
done
