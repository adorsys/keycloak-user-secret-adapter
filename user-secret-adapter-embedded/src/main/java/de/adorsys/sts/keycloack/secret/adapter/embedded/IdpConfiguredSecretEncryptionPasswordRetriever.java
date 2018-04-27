package de.adorsys.sts.keycloack.secret.adapter.embedded;

import org.adorsys.envutils.EnvProperties;

/**
 * Decides on which credential to use to encrypt user secrets store in the database of the identity provider.
 * We have following possibilities:
 * - to use the user password or
 * - to use a general IDP key
 * 
 * @author fpo 2018-04-27 07:18
 *
 */
public class IdpConfiguredSecretEncryptionPasswordRetriever implements SecretEncryptionPasswordRetriever{
	private static String userSecretEncryptionPasswordPropKey = "USER_SECRET_ENCRYPTION_PASSWORD";
	private char[] userSecretEncryptionPassword;
	
	public IdpConfiguredSecretEncryptionPasswordRetriever() {
		userSecretEncryptionPassword = EnvProperties.getEnvOrSysProp(userSecretEncryptionPasswordPropKey, false).toCharArray();
	}

	@Override
	public char[] readSecretEncryptionPassword() {
		return userSecretEncryptionPassword;
	}
}
