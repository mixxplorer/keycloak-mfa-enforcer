package de.mixxplorer.keycloak.mfaenforcer;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

import static org.keycloak.models.AuthenticationExecutionModel.Requirement.DISABLED;
import static org.keycloak.models.AuthenticationExecutionModel.Requirement.REQUIRED;

@AutoService(AuthenticatorFactory.class)
public final class MFAEnforcerAuthenticatorFactory implements AuthenticatorFactory {
     public static final MFAEnforcerAuthenticator INSTANCE = new MFAEnforcerAuthenticator();

    @Override
    public String getId() {
        return "mfaenforcer";
    }

    @Override
    public String getDisplayType() {
        return "MFA Enforcer";
    }

    @Override
    public String getHelpText() {
        return "Enforces that users set up one type of Multi-Factor-Authentication (depending on your realm settings).";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                REQUIRED,
                DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}

