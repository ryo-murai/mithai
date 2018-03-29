package io.github.murtools.mithai.biz;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.junit.Assert.*;

public class CipherServiceTest {
    @Test
    public void encryptAndDecryptUrl() throws Exception {
        String plainText =  "http://example.com/foo/bar?ppp=ddd&qq=cc";
        CipherService cipherService = new CipherService();

        String encryptUrl = cipherService.encryptUrl(plainText);

        assertThat(encryptUrl, is(not(plainText)));

        String decryptUrl = cipherService.decryptUrl(encryptUrl);

        assertThat(decryptUrl, is(plainText));
    }

    @Test
    public void testDecrypt() throws GeneralSecurityException {
        String encUrl = "t6TIXGKcQ+FYvU5Y2gnRSpAjgtzJt7NSPM1tvo2imlDGGJH//zkQHcG3roJOqrUB";

        CipherService cipherService = new CipherService();
        String decryptUrl = cipherService.decryptUrl(encUrl);

        assertThat(decryptUrl, is("http://hogehoge.com"));
    }
}