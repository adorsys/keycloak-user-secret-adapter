package de.adorsys.sts.keycloack.secret.adapter.embedded;

/**
 * Retrieves the secret encryption password.
 * Decides on which credential to use to encrypt user secrets store in the database of the identity provider.
 * We have follwing possibilities:
 * - to use the user password or
 * - to use a general IDP key
 * 
 * @author fpo 2018-04-27 07:16
 *
 */
public interface SecretEncryptionPasswordRetriever {
	public char[] readSecretEncryptionPassword();
}
