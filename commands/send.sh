#!/usr/bin/env bash

set -euxo pipefail

HOST="192.168.1.176"
PORT="5001"

# Send GossipCommand.SYN, 2 bytes, [0x00 0x01]
printf "\x00\x01" | nc $HOST $PORT

# Send GossipCommand.ACK, 2 bytes, [0x00 0x02]
printf "\x00\x02" | nc $HOST $PORT

# Send GossipCommand.ACK_SYN, 2 bytes, [0x00 0x03]
printf "\x00\x03" | nc $HOST $PORT
