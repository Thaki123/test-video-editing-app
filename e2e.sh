#!/usr/bin/env bash
set -e

docker compose up -d
trap "docker compose down" EXIT
sleep 5
curl -fsS -X POST http://localhost:8080/v1/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"username":"test"}'
echo
