# Keycloak User Secret Adapter



## Building and Running

download and install jq (https://stedolan.github.io/jq/)
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

- Keycloak runs on ports: 8080 / 8787 (debug)

