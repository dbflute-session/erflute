package org.dbflute.erflute.core.util;

import java.io.File;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.dbflute.erflute.core.util.io.FileUtils;

public class PasswordCrypt {

    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final File KEY_FILE = new File("password.key");

    public static String encrypt(String password) throws Exception {
        final Key key = getKey();

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        final byte[] input = password.getBytes();
        final byte[] encrypted = cipher.doFinal(input);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedPassword) throws Exception {
        final Key key = getKey();

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        final byte[] encrypted = Base64.getDecoder().decode(encryptedPassword.getBytes());
        final byte[] output = cipher.doFinal(encrypted);

        return new String(output);
    }

    private static Key getKey() throws Exception {
        if (KEY_FILE.exists()) {
            final byte[] key = FileUtils.readFileToByteArray(KEY_FILE);

            final SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
            return keySpec;

        } else {
            final Key key = generateKey();
            FileUtils.writeByteArrayToFile(KEY_FILE, key.getEncoded());

            return key;
        }
    }

    private static Key generateKey() throws Exception {
        final KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);

        final SecureRandom random = new SecureRandom();
        generator.init(128, random);
        final Key key = generator.generateKey();

        return key;
    }

}
