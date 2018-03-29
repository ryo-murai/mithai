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
    public static final String BRIDGE_KEY = "@1ways 0n the run";

    private static Key key = new SecretKeySpec(BRIDGE_KEY.getBytes(StandardCharsets.UTF_8), "AES");

    public String decryptBase64Url(String urlAESString) throws GeneralSecurityException {
        byte[] crypted = Base64.getDecoder().decode(urlAESString);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(crypted);
        String urlString = new String(decrypted, StandardCharsets.UTF_8);
        return urlString;
    }

    public String encryptUrl(String rawUrl) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(rawUrl.getBytes(StandardCharsets.UTF_8));

        byte[] base64Bytes = Base64.getEncoder().encode(encrypted);
        String urlString = new String(base64Bytes, StandardCharsets.UTF_8);

        return urlString;
    }
}
