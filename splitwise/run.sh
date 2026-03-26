#!/usr/bin/env bash
set -e
if [ ! -d "out" ]; then
  echo "Run ./compile.sh first"
  exit 1
fi
java -cp out splitwise.SplitwiseDemo
