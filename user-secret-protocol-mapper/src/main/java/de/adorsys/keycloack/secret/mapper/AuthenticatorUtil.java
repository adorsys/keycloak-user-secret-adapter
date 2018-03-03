package de.adorsys.keycloack.secret.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.models.UserSessionModel;

import de.adorsys.keycloack.secret.adapter.common.SecretAndAudModel;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;

public class AuthenticatorUtil {

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
    
    public static SecretAndAudModel readSecretAndAud(UserSecretAdapter userSecretStorage, UserSessionModel userSession){
        String userMainSecret = userSession.getNote(UserSecretAdapter.USER_MAIN_SECRET_NOTE_KEY);
        String scope = userSession.getNote(UserSecretAdapter.AUTH_SESSION_SCOPE_NOTE_KEY);
        List<String> audiences = extractAudiences(scope);
        
        return new SecretAndAudModel(userMainSecret, audiences);
    }
}
