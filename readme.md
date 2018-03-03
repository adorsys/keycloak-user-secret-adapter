# Keycloak User Secret Adapter



## Building

```
> git clone https://github.com/adorsys/keycloak-user-secret-adapter.git
> cd keycloak-user-secret-adapter
> mvn clean package
> cd keycloak-user-secret-embedded-plugin
> mv target/work work
> cd work
> ./01-install-local.sh
> ./02-start-local.sh
> ./03-init-local.sh

```

## Running

This still runs on the branch sts-upgrade

```
> git clone https://git.adorsys.de/adorsys/multibanking-mock.git

> git checkout sts-upgrade

> mvn spring-boot:run

```
- Keycloak runs on ports: 8080 / 8787 (debug)
- Multibanking mock runs on ports: 10010 / 10017 (debug)
- Multibanking service if started runs on ports: 8081 / 10022 (debug)

