FROM docker.io/jboss/keycloak:7.0.0

ENV JBOSS_HOME /opt/jboss/keycloak
ENV ADAPTER_VERSION 1.0.7

ADD keycloak-customization.cli /opt/jboss/keycloak/

USER root

RUN /opt/jboss/keycloak/bin/jboss-cli.sh --file=/opt/jboss/keycloak/keycloak-customization.cli \
    && rm -rf /opt/jboss/keycloak/standalone/configuration/standalone_xml_history/current

RUN mkdir -p ${JBOSS_HOME}/providers \
    && curl -k -s -L "https://repo1.maven.org/maven2/de/adorsys/keycloak/user-secret-adapter-common/${ADAPTER_VERSION}/user-secret-adapter-common-${ADAPTER_VERSION}.jar" -o ${JBOSS_HOME}/providers/user-secret-adapter-common-${ADAPTER_VERSION}.jar \
    && curl -k -s -L "https://repo1.maven.org/maven2/de/adorsys/keycloak/user-secret-adapter-embedded/${ADAPTER_VERSION}/user-secret-adapter-embedded-${ADAPTER_VERSION}.jar" -o ${JBOSS_HOME}/providers/user-secret-adapter-embedded-${ADAPTER_VERSION}.jar \
    && curl -k -s -L "https://repo1.maven.org/maven2/de/adorsys/keycloak/user-secret-auth-provider/${ADAPTER_VERSION}/user-secret-auth-provider-${ADAPTER_VERSION}.jar" -o ${JBOSS_HOME}/providers/user-secret-auth-provider-${ADAPTER_VERSION}.jar \
    && curl -k -s -L "https://repo1.maven.org/maven2/de/adorsys/keycloak/user-secret-protocol-mapper/${ADAPTER_VERSION}/user-secret-protocol-mapper-${ADAPTER_VERSION}.jar" -o ${JBOSS_HOME}/providers/user-secret-protocol-mapper-${ADAPTER_VERSION}.jar

RUN chgrp -R 0 $JBOSS_HOME &&\
    chmod -R g+rw $JBOSS_HOME

USER jboss
