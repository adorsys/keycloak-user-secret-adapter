#!/bin/bash

export KEYCLOAK_HOME=../keycloak-${keycloak.version}
export PATH=$PATH:$KEYCLOAK_HOME/bin

kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin --password admin123

kcadm.sh create realms -s realm=multibanking -s enabled=true

kcadm.sh create users -r multibanking -s username=testuser -s enabled=true

MBUSERID=$(kcadm.sh get users -r multibanking -q username=testuser --fields id | jq -r '.[] | .id')

kcadm.sh update users/$MBUSERID/reset-password -r multibanking -s type=password -s value=testpassword -s temporary=false -n

kcadm.sh create clients -r multibanking -s clientId=multibanking-client -s 'redirectUris=["*"]' -f multibanking-client.json
MBCLIENTID=$(kcadm.sh get clients -r multibanking -q clientId=multibanking-client --fields id | jq -r '.[] | .id')

kcadm.sh create -r multibanking clients/$MBCLIENTID/protocol-mappers/models -s protocol=openid-connect -s name=User-Secrets -s protocolMapper=user-secret-claim-mapper

# Modify the browser Flow
kcadm.sh create authentication/flows/browser/copy -r multibanking -s "newName=sts browser"
kcadm.sh create -r multibanking authentication/flows/sts%20browser%20forms/executions/execution -s provider=sts-username-password-form
deleteId=$(kcadm.sh get -r multibanking authentication/flows/sts%20browser%20forms/executions | jq -r '.[]  | select(.providerId == "auth-username-password-form") | .id')
kcadm.sh delete -r multibanking authentication/executions/$deleteId
kcadm.sh get -r multibanking authentication/flows/sts%20browser%20forms/executions | jq -r '.[]  | select(.providerId == "sts-username-password-form") | .requirement="REQUIRED"' > sts-username-password-form.json
kcadm.sh update -r multibanking authentication/flows/sts%20browser%20forms/executions -f sts-username-password-form.json
raiseId=$(kcadm.sh get -r multibanking authentication/flows/sts%20browser%20forms/executions | jq -r '.[]  | select(.providerId == "sts-username-password-form") | .id')
kcadm.sh create -r multibanking authentication/executions/$raiseId/raise-priority

# kcadm.sh update -r multibanking authentication/flows/sts%20browser%20forms/executions -f auth-username-password-form.json

# Modify the direct grant flow
kcadm.sh create authentication/flows/direct%20grant/copy -r multibanking -s "newName=sts direct grant"
kcadm.sh create -r multibanking authentication/flows/sts%20direct%20grant/executions/execution -s provider=sts-direct-access-authenticator
deleteId=$(kcadm.sh get -r multibanking authentication/flows/sts%20direct%20grant/executions | jq -r '.[]  | select(.providerId == "direct-grant-validate-password") | .id')
kcadm.sh delete -r multibanking authentication/executions/$deleteId
kcadm.sh get -r multibanking authentication/flows/sts%20direct%20grant/executions | jq -r '.[]  | select(.providerId == "sts-direct-access-authenticator") | .requirement="REQUIRED"' > sts-direct-access-authenticator.json
kcadm.sh update -r multibanking authentication/flows/sts%20direct%20grant/executions -f sts-direct-access-authenticator.json
raiseId=$(kcadm.sh get -r multibanking authentication/flows/sts%20direct%20grant/executions | jq -r '.[]  | select(.providerId == "sts-direct-access-authenticator") | .id')
kcadm.sh create -r multibanking authentication/executions/$raiseId/raise-priority

kcadm.sh update realms/multibanking -s "browserFlow=sts browser" -s "directGrantFlow=sts direct grant"

echo MB-CLIENT-ID $MBCLIENTID

rm sts-username-password-form.json
rm sts-direct-access-authenticator.json

