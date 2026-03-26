#!/usr/bin/env bash
set -e
echo "Compiling Splitwise..."
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac --release 25 -d out @sources.txt
rm sources.txt
echo "Done. Classes written to out/"
