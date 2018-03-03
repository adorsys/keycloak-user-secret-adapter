package de.adorsys.keycloack.secret.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.adorsys.envutils.EnvProperties;
import org.keycloak.Config.Scope;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import de.adorsys.keycloack.secret.adapter.common.SecretAndAudModel;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;

public class STSClaimMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper {
    private static final String PROVIDER_ID = "user-secret-claim-mapper";
    private static final List<ProviderConfigProperty> PROVIDER_CONFIG_PROPERTIES = new ArrayList<>();
    
    private UserSecretAdapter userSecretAdapter;
    private String claimName;

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
	public void init(Scope config) {
		super.init(config);
    }
    

	@Override
	public void postInit(KeycloakSessionFactory factory) {
		super.postInit(factory);
		claimName = EnvProperties.getEnvOrSysProp("STS_USER_SECRET_CLAIM_NAME", "user-secret");
		userSecretAdapter = factory.getProviderFactory(UserSecretAdapter.class).create(null);
	}

	@Override
    public String getDisplayType() {
        return "User Attribute";
    }

    @Override
    public String getHelpText() {
        return "Put user secret into access token under the name specified by STS_USER_SECRET_CLAIM_NAME";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return PROVIDER_CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel, KeycloakSession session, UserSessionModel userSession, AuthenticatedClientSessionModel clientSession) {
        AccessToken accessToken = super.transformAccessToken(token, mappingModel, session, userSession, clientSession);

        SecretAndAudModel secretAndAudModel = AuthenticatorUtil.readSecretAndAud(userSecretAdapter, userSession);
		Map<String, String> retrieveResourceSecrets = userSecretAdapter.retrieveResourceSecrets(secretAndAudModel, userSession.getRealm(), userSession.getUser());
        if (retrieveResourceSecrets != null && !retrieveResourceSecrets.isEmpty()) {
            accessToken.getOtherClaims().put(claimName, retrieveResourceSecrets);
        }
        return accessToken;
    }
}
