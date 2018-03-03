package de.adorsys.keycloack.secret.adapter.spi;

import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapterFactory;

public class UserSecretAdapterSpi implements Spi {

	@Override
	public String getName() {
		return "user-secret";
	}

	@Override
	public Class<? extends Provider> getProviderClass() {
		return UserSecretAdapter.class;
	}

	@Override
	public Class<? extends ProviderFactory> getProviderFactoryClass() {
		return UserSecretAdapterFactory.class;
	}

	@Override
	public boolean isInternal() {
		return true;
	}

}
