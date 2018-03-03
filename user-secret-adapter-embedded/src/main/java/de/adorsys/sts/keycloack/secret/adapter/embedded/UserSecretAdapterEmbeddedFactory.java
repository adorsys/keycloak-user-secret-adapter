package de.adorsys.sts.keycloack.secret.adapter.embedded;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapterFactory;

public class UserSecretAdapterEmbeddedFactory implements UserSecretAdapterFactory {
	UserSecretAdapterEmbedded adapter;
	@Override
	public UserSecretAdapter create(KeycloakSession session) {
		if(adapter==null) adapter = AdapterUtil.init();
		return adapter;
	}

	@Override
	public void init(Scope scope) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		adapter = AdapterUtil.init();
	}

	@Override
	public void close() {
	}

	@Override
	public String getId() {
		return "user-secret-adapter-embedded";//UserSecretAdapterFactory.ID;
	}

}
