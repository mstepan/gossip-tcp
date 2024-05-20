#!/usr/bin/env bash

set -euxo pipefail

HOST="192.168.1.176"
PORT="5001"

# send message 10 bytes length (0x00 0x0A)

#
# All short, int etc. should be converted to Big-endian (most significant byte first), so called network order
# \x00\x0A - 10 bytes, message length
# \x00\x01 - 2 bytes, Gossip command tag
# \x00\x01\x02\x03\x04\x05\x06\x07 - 8 bytes, message body
printf "\x00\x0A\x00\x01\x00\x01\x02\x03\x04\x05\x06\x07" | nc $HOST $PORT

printf "\x00\x0A\x00\x02\x00\x01\x02\x03\x04\x05\x06\x07" | nc $HOST $PORT

printf "\x00\x0A\x00\x03\x00\x01\x02\x03\x04\x05\x06\x07" | nc $HOST $PORT

# Send GossipCommand.SYN, 2 bytes, [0x00 0x01]
#printf "\x00\x01" | nc $HOST $PORT

# Send GossipCommand.ACK, 2 bytes, [0x00 0x02]
#printf "\x00\x02" | nc $HOST $PORT

# Send GossipCommand.ACK_SYN, 2 bytes, [0x00 0x03]
#printf "\x00\x03" | nc $HOST $PORT
