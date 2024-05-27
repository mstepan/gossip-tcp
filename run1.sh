#!/usr/bin/env bash
set -euxo pipefail

export JAVA_TOOL_OPTIONS="-XX:ActiveProcessorCount=2 -Xms256M -Xmx256M"

java -jar "$(find -E target -regex '.*/gossip-tcp-.+\.jar$')" \
  --port=5001 \
  --seeds 192.168.1.176:5001 \
  --seeds 192.168.1.176:5002 \
  --gossip
