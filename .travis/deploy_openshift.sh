#!/usr/bin/env bash
set -e
docker login ${OPENSHIFT_REGISTRY} -u ${OPENSHIFT_USER} -p ${OPENSHIFT_TOKEN}
docker build -t ${OPENSHIFT_REGISTRY}/${OPENSHIFT_NAMESPACE}/keycloak:${TAG} .
docker push ${OPENSHIFT_REGISTRY}/${OPENSHIFT_NAMESPACE}/keycloak:${TAG}
