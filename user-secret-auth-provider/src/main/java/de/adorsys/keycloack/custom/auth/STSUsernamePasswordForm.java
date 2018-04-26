package de.adorsys.keycloack.custom.auth;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.common.util.SystemEnvProperties;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Errors;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordUserCredentialModel;
import org.keycloak.representations.idm.CredentialRepresentation;

import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class STSUsernamePasswordForm extends UsernamePasswordForm {

    private UserSecretAdapter userSecretAdapter;

    public STSUsernamePasswordForm(UserSecretAdapter userSecretAdapter) {
        this.userSecretAdapter = userSecretAdapter;
    }

    /**
     * Override the validate password so we transfer password validation result into the authentication flow context.
     * <p>
     * TODO: Discuss issue with keycloak development team and send a patch.
     */
    @Override
    public boolean validatePassword(AuthenticationFlowContext context, UserModel user, MultivaluedMap<String, String> inputData) {
        List<CredentialInput> credentials = new LinkedList<>();
        String password = inputData.getFirst(CredentialRepresentation.PASSWORD);
        // Patched
        PasswordUserCredentialModel credentialModel = UserCredentialModel.password(password);

        AuthenticatorUtil.readScope(context)
                .ifPresent(s -> credentialModel.setNote(Constants.CUSTOM_SCOPE_NOTE_KEY, s));

        credentials.add(credentialModel);
        if (password != null && !password.isEmpty() && context.getSession().userCredentialManager().isValid(context.getRealm(), user, credentials)) {
            AuthenticatorUtil.addMainSecretToUserSession(userSecretAdapter, context, user, credentialModel);
            return true;
        } else {
            context.getEvent().user(user);
            context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
            Response challengeResponse = invalidCredentials(context);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
            context.clearUser();
            return false;
        }
    }
}
