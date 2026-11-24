#!/usr/bin/env bash
set -euo pipefail
STACK_NAME="smartpark"
docker stack rm "$STACK_NAME"
sleep 5
docker service ls
