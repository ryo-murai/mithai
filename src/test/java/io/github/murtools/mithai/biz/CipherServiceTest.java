package io.github.murtools.mithai.biz;

import static org.hamcrest.Matchers.*;
import org.junit.Test;

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
}