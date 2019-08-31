package de.adorsys.keycloack.secret.mapper;

import de.adorsys.keycloack.secret.adapter.common.SecretAndAudiencesModel;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class STSClaimMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper {

    private static final String STS_USER_SECRET_CLAIM_NAME = "STS_USER_SECRET_CLAIM_NAME";
    private static final String PROVIDER_ID = "user-secret-claim-mapper";
    private static final List<ProviderConfigProperty> PROVIDER_CONFIG_PROPERTIES = new ArrayList<>();

    private UserSecretAdapter userSecretAdapter;
    private String claimName;

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        super.postInit(factory);

        claimName = Optional.ofNullable(System.getenv(STS_USER_SECRET_CLAIM_NAME))
                .orElseGet(() -> System.getProperty(STS_USER_SECRET_CLAIM_NAME, "user-secret"));
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
    public AccessToken transformAccessToken(AccessToken token, ProtocolMapperModel mappingModel,
                                            KeycloakSession session, UserSessionModel userSession,
                                            ClientSessionContext clientSessionCtx) {
        AccessToken accessToken = super.transformAccessToken(token, mappingModel, session, userSession,
                clientSessionCtx);

        SecretAndAudiencesModel secretAndAudModel = AuthenticatorUtil.readSecretAndAud(userSecretAdapter, userSession);
        Map<String, String> retrieveResourceSecrets = userSecretAdapter.retrieveResourceSecrets(secretAndAudModel,
                userSession.getRealm(), userSession.getUser());
        if (retrieveResourceSecrets != null && !retrieveResourceSecrets.isEmpty()) {
            accessToken.getOtherClaims().put(claimName, retrieveResourceSecrets);
        }
        return accessToken;
    }
}
