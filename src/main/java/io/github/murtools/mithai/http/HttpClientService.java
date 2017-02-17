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

@Slf4j
@RequiredArgsConstructor
@Service
public class HttpClientService {
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
