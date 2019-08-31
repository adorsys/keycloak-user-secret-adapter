package de.adorsys.sts.keycloack.secret.adapter.embedded;

import com.nimbusds.jose.*;
import com.nimbusds.jose.JWEHeader.Builder;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import de.adorsys.keycloack.secret.adapter.common.SecretAndAudiencesModel;
import de.adorsys.keycloack.secret.adapter.common.UserSecretAdapter;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

public class UserSecretAdapterEmbedded implements UserSecretAdapter {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int PBKDF2_ITERATIONS = 512;
    private static final int HASH_BYTES = 16;
    /**
     * The main secret is stored, encrypted with the user password. Remember
     * that with this administrator based password reset will not work.
     */
    SecureRandom random = new SecureRandom();
    private ResourceServerService resourceServerService;
    private EncryptionService encryptionService;
    private SecretEncryptionPasswordRetriever secretEncryptionPasswordRetriever;

    UserSecretAdapterEmbedded(ResourceServerService resourceServerService,
                              EncryptionService encryptionService,
                              SecretEncryptionPasswordRetriever secretEncryptionPasswordRetriever) {
        this.resourceServerService = resourceServerService;
        this.encryptionService = encryptionService;
        this.secretEncryptionPasswordRetriever = secretEncryptionPasswordRetriever;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String retrieveMainSecret(RealmModel realmModel, UserModel userModel, UserCredentialModel credentialModel) {
        String userMainSecretAttrName = UserSecretAdapter.USER_MAIN_SECRET_NOTE_KEY;
        List<String> userSecretClaimNameAttrs = userModel.getAttribute(userMainSecretAttrName);
        char[] secretEncryptionPassword = secretEncryptionPasswordRetriever.readSecretEncryptionPassword();
        byte[] secretEncryptionPasswordPBKDF2 = pbkdf2(secretEncryptionPassword, userModel.getId().getBytes(),
                PBKDF2_ITERATIONS, HASH_BYTES);

        if (userSecretClaimNameAttrs == null || userSecretClaimNameAttrs.isEmpty()) {
            return generateUserMainSecret(userModel, userMainSecretAttrName, secretEncryptionPasswordPBKDF2);
        } else {
            return decrypt(userSecretClaimNameAttrs.iterator().next(), secretEncryptionPasswordPBKDF2);
        }
    }

    @Override
    public Map<String, String> retrieveResourceSecrets(SecretAndAudiencesModel secretAndAudModel, RealmModel realmModel,
                                                       UserModel userModel) {
        List<String> audiences = secretAndAudModel.getAudiences();
        return readUserSecret(userModel, secretAndAudModel.getUserSecret(), audiences);
    }

    private String generateUserMainSecret(UserModel userModel, String secretAttrName,
                                          byte[] secretEncryptionPasswordPBKDF2) {
        String userMainSecretPlain = RandomStringUtils.randomGraph(16);
        Builder headerBuilder = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
        JWEObject jweObj = new JWEObject(headerBuilder.build(), new Payload(userMainSecretPlain));
        try {
            jweObj.encrypt(new DirectEncrypter(secretEncryptionPasswordPBKDF2));
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
        String customSecretAttr = jweObj.serialize();
        userModel.setAttribute(secretAttrName, Collections.singletonList(customSecretAttr));
        return userMainSecretPlain;
    }

    private Map<String, String> readUserSecret(UserModel userModel, String userMainSecret, List<String> audiences) {
        Map<String, String> resourceSecrets = new HashMap<>();
        for (String audience : audiences) {
            if (StringUtils.isBlank(audience)) continue;
            ResourceServer resourceServer = resourceServerService.getForAudience(audience);
            if (resourceServer == null)
                continue;

            String userSecretClaimName = resourceServer.getUserSecretClaimName();
            if (resourceSecrets.containsKey(userSecretClaimName))
                continue;

            List<String> userSecretClaimNameAttribute = userModel.getAttribute(userSecretClaimName);
            byte[] secretEncryptionPasswordPBKDF2 = pbkdf2(userMainSecret.toCharArray(), userModel.getId().getBytes(),
                    PBKDF2_ITERATIONS, HASH_BYTES);

            String userResourceSecretPlain;
            if (userSecretClaimNameAttribute == null || userSecretClaimNameAttribute.isEmpty()) {
                userResourceSecretPlain = RandomStringUtils.randomNumeric(16);
                String customSecretAttrEnc = encrypt(userResourceSecretPlain, secretEncryptionPasswordPBKDF2);
                userModel.setAttribute(userSecretClaimName, Arrays.asList(customSecretAttrEnc));
            } else {
                userResourceSecretPlain = decrypt(userSecretClaimNameAttribute.iterator().next(),
                        secretEncryptionPasswordPBKDF2);
            }
            String userResourceSecretEncrypted = encryptionService.encryptFor(audience, userResourceSecretPlain);
            resourceSecrets.put(userSecretClaimName, userResourceSecretEncrypted);
        }
        return resourceSecrets;
    }

    private String encrypt(String plain, byte[] key) {
        Builder headerBuilder = new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
        JWEObject jweObj = new JWEObject(headerBuilder.build(), new Payload(plain));
        try {
            jweObj.encrypt(new DirectEncrypter(key));
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }
        return jweObj.serialize();
    }

    private String decrypt(String encrypted, byte[] key) {
        try {
            JWEObject jweObject = JWEObject.parse(encrypted);
            jweObject.decrypt(new DirectDecrypter(key));
            return jweObject.getPayload().toString();
        } catch (JOSEException | ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        //noop
    }

}
