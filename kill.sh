#!/usr/bin/env bash
set -euxo pipefail

# shellcheck disable=SC2046
kill $(jps | grep gossip-tcp | awk '{print $1}')
