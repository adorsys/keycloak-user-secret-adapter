package de.adorsys.keycloack.secret.adapter.common;

import java.util.Map;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

/**
 * The user secret storage does two things:
 * - Prepare the user secret for access in the protocol mapper.
 *    - Use the user password to decrypt the user secret,
 *    - Hold the decrypted user secret in the user session.
 *    
 * - Provide protocol mapper with encrypted user secret.
 *    - Read the decrypted secret from the user session
 *    - Retrieve the corresponding user secret from the user attribute 
 *    
 * @author fpo
 *
 */
public interface UserSecretAdapter extends Provider{
	
	String USER_MAIN_SECRET_NOTE_KEY="UserSecretStorage-UserMainSecret";
	String AUTH_SESSION_SCOPE_NOTE_KEY="UserSecretStorage-AuthSessionScope";
	
    String retrieveMainSecret(RealmModel realmModel, UserModel userModel, UserCredentialModel credentialModel);

    Map<String, String> retrieveResourceSecrets(SecretAndAudiencesModel secretAndAudModel, RealmModel realmModel, UserModel userModel);
}
