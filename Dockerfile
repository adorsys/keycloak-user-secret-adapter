FROM docker.io/jboss/keycloak:7.0.0

ADD keycloak-customization.cli /opt/jboss/keycloak/
RUN /opt/jboss/keycloak/bin/jboss-cli.sh --file=/opt/jboss/keycloak/keycloak-customization.cli
RUN rm -rf /opt/jboss/keycloak/standalone/configuration/standalone_xml_history/current

ADD ./user-secret-adapter-common/target/user-secret-adapter-common.jar /opt/jboss/keycloak/providers/user-secret-adapter-common.jar
ADD ./user-secret-adapter-embedded/target/user-secret-adapter-embedded.jar /opt/jboss/keycloak/providers/user-secret-adapter-embedded.jar
ADD ./user-secret-auth-provider/target/user-secret-auth-provider.jar /opt/jboss/keycloak/providers/user-secret-auth-provider.jar
ADD ./user-secret-protocol-mapper/target/user-secret-protocol-mapper.jar /opt/jboss/keycloak/providers/user-secret-protocol-mapper.jar

USER jboss
