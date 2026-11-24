#!/usr/bin/env bash
set -euo pipefail
STACK_NAME="smartpark"
COMPOSE_FILE="docker-compose.swarm.yml"
if ! docker info --format "{{.Swarm.LocalNodeState}}" | grep -q "active"; then
  docker swarm init
fi
echo "Token para unir nodos WORKER:"
docker swarm join-token worker
docker stack deploy -c "$COMPOSE_FILE" "$STACK_NAME"
docker stack services "$STACK_NAME"
