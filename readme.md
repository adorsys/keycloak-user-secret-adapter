# Keycloak User Secret Adapter



## Building and Running

download and install jq (https://stedolan.github.io/jq/)

Download keycloak at https://downloads.jboss.org/keycloak/3.4.3.Final/keycloak-3.4.3.Final.tar.gz


```
For Mac users

> export KEYCLOAK_DIST=~/Downloads/keycloak-3.4.3.Final.tar.gz

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

## LICENSE

APACHE LICENSE 2.0
See LICENSE.md

## Release process

1. Assure that you have rights to push to develop and master branches
2. Check the build and Javadocs locally:
```bash
mvn clean install javadoc:javadoc
```
3. Setup release scripts for your local copy
```bash
git submodule update
```
or
```bash
git submodule init
```
4. Make a release from develop branch by following command:
```
scripts/release-scripts/release.sh 0.0.1 0.0.2
```
Where 0.0.1 - a version to release
0.0.2 - a next version to develop
5. Follow release scripts commands, including push


More details see in release-scripts: https://github.com/borisskert/release-scripts
