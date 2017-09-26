package io.github.murtools.mithai.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;

@Slf4j
@RequiredArgsConstructor
@Service
public class HttpClientService {
  private static final int MaxNumRedirects = 20;

  private final Environment env;

  public HttpURLConnection createConnection(String urlString) throws IOException {
    URL url = new URL(urlString);

    String httpProxy = env.getProperty("http_proxy");
    if(httpProxy != null) {
      log.info("http_proxy enabled:{}", httpProxy);
      return (HttpURLConnection) url.openConnection(createProxy(httpProxy));
    } else {
      return (HttpURLConnection) url.openConnection();
    }
  }

  public String getRedirectUrl(String sourceUrl) throws IOException {
    return getRedirectUrl(sourceUrl, 1);
  }

  public String getRedirectUrl(String sourceUrl, int numRedirects) throws IOException {
    if(numRedirects > MaxNumRedirects) {
      log.error("num redirects reached the limit. current url: {}", sourceUrl);
      return null;
    }

    HttpURLConnection conn = createConnection(sourceUrl);
    conn.setInstanceFollowRedirects(false);
    conn.setRequestMethod("HEAD");
    conn.connect();

    if(conn.getResponseCode() == 200) {
      log.info("HEAD {} succeeded 200", sourceUrl);
      return sourceUrl;
    } else if(conn.getResponseCode() == 301) {
      String redirectLocation = conn.getHeaderField("Location");
      return getRedirectUrl(redirectLocation, numRedirects + 1);
    } else {
      log.error("accessing url:[{}] response status: {}, message: {}", sourceUrl,
              conn.getResponseCode(), conn.getResponseMessage());
      return null;
    }
  }

  public static HttpHeaders createHeadersFrom(HttpURLConnection sourceConnection) {
    HttpHeaders destHeaders = new HttpHeaders();
    sourceConnection.getHeaderFields().forEach((key, values) -> {
      log.debug("key:{}, values:{}", key, values);
      if (key != null) {
        destHeaders.putIfAbsent(key, values);
      }
    });

    return destHeaders;
  }

  private static Proxy createProxy(String httpProxy) {
    try {
      URI proxyUri = new URI(httpProxy);
      InetSocketAddress proxyAddress =
          new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort());

      return new Proxy(Proxy.Type.HTTP, proxyAddress);
    } catch (URISyntaxException e) {
      throw new RuntimeException("invalid proxy setting:" + httpProxy, e);
    }
  }
}
