package de.adorsys.keycloack.secret.adapter.spi;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapterFactory;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.Spi;

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
