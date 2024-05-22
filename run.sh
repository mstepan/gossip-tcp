#!/usr/bin/env bash

#export REMOTE_DEBUGGER=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
export JAVA_TOOL_OPTIONS="-XX:ActiveProcessorCount=2 -Xms256M -Xmx256M"

java ${REMOTE_DEBUGGER} -jar "$(find -E target -regex '.*/gossip-tcp-.+\.jar$')" --port=5001 --gossip=false
