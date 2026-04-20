#!/bin/bash
set -e

# Required env vars: VPS_USER, VPS_HOST
# Gradle property -PbackendBaseUrl is empty for production so browser JS calls
# resolve same-origin (e.g. https://dgkat.com/api/web/...) and the SESSION cookie
# is scoped to the SSR host. SSR JVM uses the BACKEND_BASE_URL docker env var at
# runtime for its own server-side calls to the backend — independent of this flag.
# Optional:
#   VPS_JAR_PATH   - where the JAR lives on the host (default: /opt/fakeshop-web)
#   COMPOSE_DIR    - directory containing docker-compose.yml (default: /opt/fakeshop/prod_env)
VPS_JAR_PATH="${VPS_JAR_PATH:-/opt/fakeshop-web}"
COMPOSE_DIR="${COMPOSE_DIR:-/opt/fakeshop/prod_env}"
JAR="web/ssr/build/libs/ssr-all.jar"

if [[ -z "$VPS_USER" || -z "$VPS_HOST" ]]; then
  echo "Error: VPS_USER and VPS_HOST must be set"
  exit 1
fi

echo "==> Building fat JAR (production)..."
./gradlew :web:ssr:buildFatJar -PbackendBaseUrl=""

echo "==> Uploading to $VPS_USER@$VPS_HOST:$VPS_JAR_PATH/..."
scp "$JAR" "$VPS_USER@$VPS_HOST:$VPS_JAR_PATH/ssr-all.jar"

echo "==> Restarting fakeshop-web container..."
ssh "$VPS_USER@$VPS_HOST" "cd $COMPOSE_DIR && docker compose restart fakeshop-web"

echo "==> Done."
