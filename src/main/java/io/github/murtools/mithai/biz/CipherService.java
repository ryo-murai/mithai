package io.github.murtools.mithai.biz;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

@Service
public class CipherService {
    public static final String BRIDGE_KEY = "@1ways 0n the rn";  // 16 bytes (128bit)

    private static Key key = new SecretKeySpec(BRIDGE_KEY.getBytes(StandardCharsets.UTF_8), "AES");

    public String decryptUrl(String encUrl) throws GeneralSecurityException {
        byte[] decoded = Base64.getDecoder().decode(encUrl);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(decoded);
        String plainUrl = new String(decrypted, StandardCharsets.UTF_8);
        return plainUrl;
    }

    public String encryptUrl(String plainUrl) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainUrl.getBytes(StandardCharsets.UTF_8));
        byte[] encoded = Base64.getEncoder().encode(encrypted);
        String urlString = new String(encoded, StandardCharsets.UTF_8);

        return urlString;
    }

}
