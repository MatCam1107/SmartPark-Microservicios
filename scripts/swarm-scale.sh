#!/usr/bin/env bash
set -euo pipefail
STACK_NAME="smartpark"
SERVICE_NAME="${STACK_NAME}_ms-estacionamiento"
REPLICAS="${1:-}"
if [ -z "$REPLICAS" ]; then
  echo "Uso: ./scripts/swarm-scale.sh <numero_replicas>"
  exit 1
fi
docker service scale "${SERVICE_NAME}=${REPLICAS}"
docker service ps "$SERVICE_NAME"
