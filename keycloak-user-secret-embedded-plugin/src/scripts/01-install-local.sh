#!/bin/bash

# Download the keycloak distribution and set the corresponding env variable
# export KEYCLOAK_DIST=~/Downloads/keycloak-${keycloak.version}.tar.gz

if [ "y$KEYCLOAK_DIST" = "y" ];
        then
                echo "Missing param KEYCLOAK_DIST"
                echo "Download the keycloak distribution and set the corresponding env variable"
                exit 1
fi

echo "KEYCLOAK_DIST $KEYCLOAK_DIST"

export WORK_DIR=$(pwd)

cd $WORK_DIR && tar xzf $KEYCLOAK_DIST

export KEYCLOACK_HOME=$WORK_DIR/keycloak-${keycloak.version}

cd $KEYCLOACK_HOME && tar xzf $WORK_DIR/../target/${project.build.finalName}-${project.version}-keycloak-${keycloak.version}.tar.gz

cd $KEYCLOACK_HOME/cli && $KEYCLOACK_HOME/bin/jboss-cli.sh --file=$KEYCLOACK_HOME/cli/init_keycloak.cli 

cd $KEYCLOACK_HOME/bin && $KEYCLOACK_HOME/bin/add-user-keycloak.sh --user admin --password admin123
