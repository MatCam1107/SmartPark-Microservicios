#!/usr/bin/env bash
set -euo pipefail
STACK_NAME="smartpark"
COMPOSE_FILE="docker-compose.swarm.yml"
MANAGER_IP="${1:-}"
if ! docker info >/dev/null 2>&1; then
  echo "[ERROR] Docker no esta en ejecucion."
  exit 1
fi
if [ ! -f "$COMPOSE_FILE" ]; then
  echo "[ERROR] No se encontro $COMPOSE_FILE."
  exit 1
fi
if ! docker info --format "{{.Swarm.LocalNodeState}}" | grep -q "active"; then
  if [ -n "$MANAGER_IP" ]; then
    docker swarm init --advertise-addr "$MANAGER_IP"
  else
    docker swarm init
  fi
fi
echo "=== Comando para unir nodos WORKER ==="
docker swarm join-token worker
docker stack deploy -c "$COMPOSE_FILE" "$STACK_NAME"
docker stack services "$STACK_NAME"
