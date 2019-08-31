package de.adorsys.sts.keycloack.secret.adapter.embedded;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapterFactory;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class UserSecretAdapterEmbeddedFactory implements UserSecretAdapterFactory {

    private UserSecretAdapterEmbedded adapter;

    @Override
    public UserSecretAdapter create(KeycloakSession session) {
        if (adapter == null) adapter = AdapterUtil.init();
        return adapter;
    }

    @Override
    public void init(Scope scope) {
        //noop
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        adapter = AdapterUtil.init();
    }

    @Override
    public void close() {
        //noop
    }

    @Override
    public String getId() {
        return "user-secret-adapter-embedded";
    }

}
