package de.adorsys.keycloack.secret.mapper;

import de.adorsys.keycloack.secret.adapter.common.SecretAndAudiencesModel;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import org.apache.commons.lang.StringUtils;
import org.keycloak.models.UserSessionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthenticatorUtil {

    private static final String STS_DEFAULT_AUDIENCE = "STS_DEFAULT_AUDIENCE";
    private static String defaultAudience = Optional.ofNullable(System.getenv(STS_DEFAULT_AUDIENCE))
            .orElseGet(() -> System.getProperty(STS_DEFAULT_AUDIENCE));

    private static List<String> extractAudiences(String scope) {
        List<String> audiences = Optional.ofNullable(scope)
                .map(Object::toString)
                .map(s -> s.split(" "))
                .map(Arrays::asList)
                .orElse(new ArrayList<>());

        return audiences.stream()
                .filter(a -> !"openid".equals(a))
                .collect(Collectors.toList());
    }

    public static SecretAndAudiencesModel readSecretAndAud(UserSecretAdapter userSecretStorage,
                                                           UserSessionModel userSession) {
        String userMainSecret = userSession.getNote(UserSecretAdapter.USER_MAIN_SECRET_NOTE_KEY);
        String scope = userSession.getNote(UserSecretAdapter.AUTH_SESSION_SCOPE_NOTE_KEY);
        List<String> audiences = extractAudiences(scope);

        if (audiences.size() == 0 && defaultAudience != null) {
            audiences.add(defaultAudience);
        }

        return new SecretAndAudiencesModel(userMainSecret, audiences);
    }

    public static String getEnvOrSysProp(String propName, boolean optional) {
        String propValue = System.getenv(propName);
        if (StringUtils.isBlank(propValue)) {
            propValue = System.getProperty(propName);
        }

        if (StringUtils.isBlank(propValue)) {
            if (optional) {
                return null;
            } else {
                throw new IllegalStateException("Missing Environmen property " + propName);
            }
        } else {
            return propValue;
        }
    }
}
