#!/bin/sh
set -eu

image="bof"
container="bof"

docker build -t "$image" .
docker rm -f "$container" >/dev/null 2>&1 || true
docker run -d \
  --name "$container" \
  --restart always \
  --cap-drop ALL \
  --security-opt no-new-privileges \
  --pids-limit 100 \
  --memory 256m \
  --cpus 1.0 \
  -p 127.0.0.1:10003:10003 \
  "$image"

printf 'running: nc 127.0.0.1 10003\n'
