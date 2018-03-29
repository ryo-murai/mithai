package io.github.murtools.mithai.biz;

import io.github.murtools.mithai.http.HttpClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

@Controller
@RequestMapping("/biz")
@RequiredArgsConstructor
public class BridgeController {
    private final HttpClientService httpClientService;

    private final CipherService cipherService;

    @RequestMapping("/br")
    public ResponseEntity<InputStreamResource> bridge(@RequestParam("u") String urlAESString) {

        try {
            String urlString = cipherService.decryptBase64Url(urlAESString);

            HttpURLConnection httpConn = httpClientService.createConnection(urlString);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.connect();

            HttpHeaders headers = HttpClientService.createHeadersFrom(httpConn);

            return ResponseEntity.status(httpConn.getResponseCode())
                    .headers(headers)
                    .body(new InputStreamResource(httpConn.getInputStream()));

        } catch (GeneralSecurityException | IOException e) {
            byte[] msg = e.getMessage().getBytes(StandardCharsets.UTF_8);
            return ResponseEntity
                    .status(500)
                    .body(new InputStreamResource(new ByteArrayInputStream(msg)));
        }
    }
}
