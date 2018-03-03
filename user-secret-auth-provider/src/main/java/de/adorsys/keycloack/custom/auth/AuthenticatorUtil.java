package de.adorsys.keycloack.custom.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;

public class AuthenticatorUtil {

    public static Optional<String> readScope(AuthenticationFlowContext context) {
        Object scope = context.getAuthenticationSession().getClientNote(OAuth2Constants.SCOPE);

        return Optional.ofNullable(scope)
                .map(Object::toString);
    }

    public static List<String> extractAudiences(UserCredentialModel credentialInput) {
        Object scope = credentialInput.getNote(Constants.CUSTOM_SCOPE_NOTE_KEY);

        List<String> audiences = Optional.ofNullable(scope)
                .map(Object::toString)
                .map(s -> s.split(" "))
                .map(Arrays::asList)
                .orElse(new ArrayList<>());

        return audiences.stream()
                .filter(a -> !"openid".equals(a))
                .collect(Collectors.toList());
    }
    
    public static void addMainSecretToUserSession(UserSecretAdapter userSecretStorage, AuthenticationFlowContext context, UserModel user, UserCredentialModel credentialModel ){
		String userSecret = userSecretStorage.retrieveMainSecret(context.getRealm(), user, credentialModel);
        // copy notes into the user session
        // Hint: it might have been interesting to distinguish between the different type of notes
        // that can be returned by a user storage provider like:
        // - UserSesionNote
        // - AuthNote
        // - ClientNote
        // Hint: even roles could be transported using these notes.
        Object scope = credentialModel.getNote(Constants.CUSTOM_SCOPE_NOTE_KEY);
		if (userSecret != null) {
			context.getAuthenticationSession().setUserSessionNote(UserSecretAdapter.USER_MAIN_SECRET_NOTE_KEY,userSecret);
		}
		if(scope!=null){
			context.getAuthenticationSession().setUserSessionNote(UserSecretAdapter.AUTH_SESSION_SCOPE_NOTE_KEY,scope.toString());
		}
    }
}
