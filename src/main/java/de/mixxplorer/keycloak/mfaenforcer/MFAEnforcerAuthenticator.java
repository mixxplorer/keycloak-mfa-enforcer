package de.mixxplorer.keycloak.mfaenforcer;

import lombok.extern.jbosslog.JBossLog;
import org.jboss.logging.Logger;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.authentication.requiredactions.UpdateTotp;
import org.keycloak.authentication.requiredactions.WebAuthnRegisterFactory;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.models.credential.WebAuthnCredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.function.Consumer;

@JBossLog
public class MFAEnforcerAuthenticator implements Authenticator {

    private static final Logger LOGGER = Logger.getLogger(MFAEnforcerAuthenticator.class);

    static final String WEBAUTHN_REGISTER_REQUIRED_ACTION_ID = WebAuthnRegisterFactory.PROVIDER_ID;
    static final String OTP_REGISTER_REQUIRED_ACTION_ID = new UpdateTotp().getId();  

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        // Skip if we already have MFA.
        if (this.userHasMFA(context.getUser())) {
            context.success();
            return;
        }

        context.challenge(this.createForm(context, null));
    }

    protected Response createForm(AuthenticationFlowContext context, Consumer<LoginFormsProvider> formCustomizer) {
        LoginFormsProvider form = context.form();
        form.setAttribute("username", context.getUser().getUsername());
        form.setAttribute("actionUri", context.getActionUrl(context.generateAccessCode()));

        if (formCustomizer != null) {
            formCustomizer.accept(form);
        }

        // Load form from src/main/resources/theme-resources/templates/
        return form.createForm("select-mfa-type-form.ftl");
    }

    protected Map<String, String> getConfig(AuthenticationFlowContext context) {
        AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
        if (configModel == null) {
            return null;
        }
        return configModel.getConfig();
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        EventBuilder event = context.getEvent();
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        EventBuilder errorEvent = event.clone().event(EventType.LOGIN_ERROR)
            .client(authSession.getClient())
            .user(authSession.getAuthenticatedUser());

        if (formData.containsKey("totp")) {
            this.addRequiredActionForAuthSession(authSession, OTP_REGISTER_REQUIRED_ACTION_ID);
        } else if (formData.containsKey("webauthn")) {
            this.addRequiredActionForAuthSession(authSession, WEBAUTHN_REGISTER_REQUIRED_ACTION_ID);
        } else {
            Response challenge = this.createForm(context, form -> {
                // null as field means `GLOBAL`
                form.addError(new FormMessage(null, "Invalid input. Please contact your administrator."));
            });

            context.challenge(challenge);

            errorEvent.error(Errors.INVALID_INPUT);
            return;
        }
        // Successfully selected MFA type.
        context.success();
    }

    private void addRequiredActionForAuthSession(AuthenticationSessionModel authSession, String actionId) {
        // Remove other maybe selected options because we only want one of the two actions.
        // This is necessary, as restarting the login process in the same tab does not
        // seem to clear the required actions in this authentication session.
        authSession.removeRequiredAction(OTP_REGISTER_REQUIRED_ACTION_ID);
        authSession.removeRequiredAction(WEBAUTHN_REGISTER_REQUIRED_ACTION_ID);
        // And add the user-selected action.
        authSession.addRequiredAction(actionId);
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    private boolean userHasMFA(UserModel user) {
        // Check whether we need to trigger this step in the authentication flow.
        // This is the case, the user neither has MFA activated and didn't select a type of MFA, yet.

        final boolean hasOTP = user.credentialManager().isConfiguredFor(OTPCredentialModel.TYPE);
        final boolean hasWebAuthn = user.credentialManager().isConfiguredFor(WebAuthnCredentialModel.TYPE_TWOFACTOR);
        final boolean hasMFA = hasOTP || hasWebAuthn;

        return hasMFA;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // noop
    }

    @Override
    public void close() {
        // noop
    }
}
