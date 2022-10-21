# keycloak-mfa-enforcer

Enforce multi-factor authentication (MFA) for your [Keycloak](https://www.keycloak.org) users and letting them choose their type of MFA.

## Goal of this plugin

This Keycloak plugin solves a problem where administrator want to enforce MFA for users but also want the users to choose their type of MFA (e.g. choosing between WebAuthn or TOTP).  
This is done via a *authentication flow execution* that can be configured to be triggered when a user logs in that should have MFA enabled but has not.

## Building

From this directory, you can create a `.jar` file with [maven](https://maven.apache.org) and [docker](https://www.docker.com):

```bash
docker run --rm -it -v $PWD:/mfa-enforcer -w /mfa-enforcer maven:3-openjdk-18 mvn clean package
```
