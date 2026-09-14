#!/usr/bin/env bash
# AI Study Planner - Unified Startup Script for Unix/Linux/macOS

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "==================================================="
echo "    AI Study Planner - Unified Startup Script"
echo "==================================================="
echo ""

# 1. Check Java 17+
if ! command -v java &> /dev/null; then
    echo "[ERROR] java is not installed or not in PATH. Please install JDK 17+."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo "[1/3] Java detected: $(java -version 2>&1 | head -n 1)"

# 2. Check Backend JAR
JAR_FILE="$SCRIPT_DIR/backend/target/ai-study-planner-backend-1.0.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "[2/3] Backend JAR not found. Building with Maven..."
    cd "$SCRIPT_DIR/backend"
    mvn clean package -DskipTests
    cd "$SCRIPT_DIR"
fi

echo "[2/3] Backend JAR ready: $JAR_FILE"

# 3. Background health check and browser open
(
    for i in {1..45}; do
        sleep 1
        STATUS=$(curl -s http://localhost:8080/api/health | grep -o '"status":"UP"' || true)
        if [ -n "$STATUS" ]; then
            echo ">>> Backend is UP! Opening http://localhost:8080/ in default browser..."
            if command -v xdg-open &> /dev/null; then
                xdg-open "http://localhost:8080/"
            elif command -v open &> /dev/null; then
                open "http://localhost:8080/"
            fi
            break
        fi
    done
) &

# 4. Start Spring Boot application
echo "[3/3] Starting Spring Boot on http://localhost:8080/ ..."
java -jar "$JAR_FILE"
