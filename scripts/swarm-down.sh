#!/usr/bin/env bash
set -euo pipefail
STACK_NAME="smartpark"
docker compose -f api-gateway/docker-compose.gateway.yml down
docker stack rm "$STACK_NAME"
sleep 5
docker service ls
