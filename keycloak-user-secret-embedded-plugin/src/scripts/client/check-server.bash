#!/bin/bash

if [ -z $1 ]
	then
		echo "Missing host and port"
		echo "Usage: $0 <host:port>"
		exit 1
fi

hostAndPort=$1
username=testuser
password=testpassword
realm=multibanking
clientId=multibanking-client

HEADER_DUMP_FILE="$0.txt"

TKN='test'
while [ "$TKN" == 'test' ]; do
	echo "Checking idp server"
	# Get and parse access token
	RESP=$(curl -s -X POST "http://$hostAndPort/auth/realms/$realm/protocol/openid-connect/token" -D ${HEADER_DUMP_FILE} -H "Content-Type: application/x-www-form-urlencoded" -d "username=$username" -d "password=$password" -d 'grant_type=password' -d "client_id=$clientId" -d "scope=mockbanking")
	if [[ "$RESP" == *"access_token"* ]]
	then
	  TKN=`echo $RESP | sed 's/.*access_token":"//g' | sed 's/".*//g'`
	  echo "Idp is ready"
	else
	  cat ${HEADER_DUMP_FILE}
	  echo "Still waiting for idp to be ready"
	  sleep 2
	fi
done

echo $TKN
