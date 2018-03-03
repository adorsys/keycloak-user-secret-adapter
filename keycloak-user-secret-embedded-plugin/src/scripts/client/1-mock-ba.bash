#!/bin/bash

if [ -z $2 ]
	then
		echo "Missing host and port"
		echo "Usage: $0 <host:port> <accesstoken>"
		exit 1
fi

hostAndPort=$1
TKN=$2

HEADER_DUMP_FILE="$0.txt"

curl -s -X POST "http://$hostAndPort/bankaccesses" -D ${HEADER_DUMP_FILE} -H "Authorization: Bearer $TKN"
