#!/bin/bash

export WORK_DIR=$(pwd)

export KEYCLOACK_HOME=$WORK_DIR/keycloak-${keycloak.version}

cd $KEYCLOACK_HOME && $KEYCLOACK_HOME/bin/standalone.sh --debug -DSTS_RESOURCE_SERVERS_CONFIG_FILE=$KEYCLOACK_HOME/cli/SAMPLE_STS_RESOURCE_SERVERS_CONFIG_FILE.properties
