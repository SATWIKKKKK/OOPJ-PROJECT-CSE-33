#!/bin/bash
echo "========================================"
echo "  College Admission and Fee System"
echo "  PostgreSQL + Java OOP"
echo "========================================"

mkdir -p out

# Check lib folder
if [ ! -d "lib" ]; then
    echo "[ERROR] lib/ folder not found."
    echo "Download postgresql-42.x.x.jar from https://jdbc.postgresql.org/download/"
    echo "and place it inside the lib/ folder."
    exit 1
fi

# Find JDBC jar
JDBC_JAR=$(ls lib/postgresql-*.jar 2>/dev/null | head -1)
if [ -z "$JDBC_JAR" ]; then
    echo "[ERROR] No postgresql-*.jar found in lib/"
    exit 1
fi

echo "Using driver: $JDBC_JAR"
echo "Compiling..."

find src -name "*.java" | xargs javac -cp "$JDBC_JAR" -d out

if [ $? -ne 0 ]; then
    echo "[ERROR] Compilation failed."
    exit 1
fi

echo "[OK] Compiled! Running..."
echo "========================================"
java -cp "out:$JDBC_JAR" com.college.Main
