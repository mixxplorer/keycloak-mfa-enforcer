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

## Installation

You can install this plugin for Keycloak by copying the `.jar` file from [building](#building) into the `providers` directory of Keycloak.
It then automatically picks up the plugin and you can [configure](#configuration-in-keycloak) it accordingly.

For a test with docker, you can run the following command from the root directory of this repo.
**⚠️ Note:** This is only for development.
For production deployment, please consider the [Keycloak Documentation](https://www.keycloak.org/documentation).

```bash
cp ./target/mfa-enforcer-*.jar ./target/mfa-enforcer.jar && \
docker run --rm -v $PWD/target/mfa-enforcer.jar:/opt/keycloak/providers/mfa-enforcer.jar:ro -p 127.0.0.1:8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0 start-dev
```

You can check whether the plugin was loaded by looking into the "Provider Info" tab of the master realm and finding `mfa-enforcer` in the `authenticator` SPI section.

## Configuration in Keycloak

As this plugin hooks into the authentication flow, you need to create a new flow for your realm.
This guide shows setting up the realm with Keycloak Admin UI 2 and for all users *that have a specific role*.

Start by **duplicating the browser flow** (left sidebar, Authentication, Flows).
In this guide, we use the name `browser-with-enforced-mfa`.
You can also add the WebAuthn Authenticator as alternative to OTP.

![Duplicate the browser flow.](./docs/clone-browser-flow.png)

After that, add a *new subflow* and move it *before the "Conditional OTP"*.
In this guide, we named the flow `enforce-mfa`.

### Variant 1: Without role condition checking (all users)

Set its requirement to **"Required"**.  
To this subflow, **add the "MFA Enforcer" step**. Set its requirement to **"Required"**.
It then should look like this.

![Setup for all users](docs/setup-v1-without-role-checking.png)

### Variant 2: Checking a role for MFA

*Optionally* (if you only want to enforce MFA for e.g. a specific role), set the subflow to **"Conditional"** and add a condition.
Remember to configure the condition (by clicking the gear icon and adding a role), give it an alias (can be arbitrary, we chose `check-mfa-role`) and make it **"Required"**.

![Adding a user role condition](./docs/setup-v2-user-role-condition-add.png)
![Adding a user role condition - Configuration](./docs/setup-v2-user-role-condition-configure.png)

The flow then should look like this.

![Flow with user role condition](./docs/setup-v2-with-role-checking.png)

### For all Variants

To the `Conditional OTP` subflow (down below the `enforce-mfa` subflow), also **add the `WebAuthn Authenticator`** as an alternative and mark the `OTP Form` as alternative, too.
You can also rename the subflow to `Check MFA`.

### Binding the flow

The complete flow (depending on your configuration) could look like this.  
You now **need to bind this flow as the default browser flow**.
After that, users that log in (and did not setup MFA, yet) should see a prompt to configure MFA.

![Complete flow](./docs/complete-flow.png)
