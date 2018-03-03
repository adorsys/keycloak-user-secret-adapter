package de.adorsys.keycloack.secret.adapter.common;

import org.keycloak.provider.ProviderFactory;

public interface UserSecretAdapterFactory extends ProviderFactory<UserSecretAdapter>{ //CredentialProviderFactory<UserSecretAdapter> {
	// Plugin must declare this instance.
//	public static final String ID = "user-secret-adapter";
}
