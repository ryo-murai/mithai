package io.github.murtools.mithai.biz;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

@Service
public class CipherService {
    public static final String BRIDGE_KEY = "@1ways 0n the rn";  // 16 bytes (128bit)

    private static Key key = new SecretKeySpec(BRIDGE_KEY.getBytes(StandardCharsets.UTF_8), "AES");

    public String decryptUrl(String encUrl) throws GeneralSecurityException {
        byte[] decoded = Base64.getUrlDecoder().decode(encUrl);

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
        String encUrl = Base64.getUrlEncoder().encodeToString(encrypted);

        return encUrl;
    }
}
