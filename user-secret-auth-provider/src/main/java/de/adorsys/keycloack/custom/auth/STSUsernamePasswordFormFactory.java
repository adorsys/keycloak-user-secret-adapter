package de.adorsys.keycloack.custom.auth;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;

public class STSUsernamePasswordFormFactory implements AuthenticatorFactory {

	private static final String PROVIDER_ID = "sts-username-password-form";
	
	private UsernamePasswordForm singleton;

	private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
			AuthenticationExecutionModel.Requirement.REQUIRED };

	@Override
	public Authenticator create(KeycloakSession session) {
		return singleton;
	}

	@Override
	public void init(Config.Scope config) {
	}

	@Override
    public void postInit(KeycloakSessionFactory factory) {
		UserSecretAdapter userSecretAdapter = factory.getProviderFactory(UserSecretAdapter.class).create(null);
		singleton = new STSUsernamePasswordForm(userSecretAdapter);			
	}

	@Override
	public void close() {

	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public String getReferenceCategory() {
		return UserCredentialModel.PASSWORD;
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return REQUIREMENT_CHOICES;
	}

	@Override
	public String getDisplayType() {
		return "Custom Username Password Form";
	}

	@Override
	public String getHelpText() {
		return "Validates a username and password from login form.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return null;
	}

	@Override
	public boolean isUserSetupAllowed() {
		return false;
	}

}
