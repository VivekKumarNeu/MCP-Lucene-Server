#!/usr/bin/env bash
set -euo pipefail
export LUCENE_BASE_URL="${LUCENE_BASE_URL:-http://localhost:8080/mcp/v1}"
exec node /Users/vivekkumar/github/MCP-Lucene-Server/mcp-shim/server.js \
  2> /tmp/mcp-lucene-shim.stderr.log
