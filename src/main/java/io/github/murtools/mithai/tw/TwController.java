package io.github.murtools.mithai.tw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import twitter4j.Status;
import twitter4j.TwitterException;

@Controller
@RequestMapping("/tw")
@RequiredArgsConstructor
@Slf4j
public class TwController {
  private final TwService twService;

  @RequestMapping("/status")
  public String status(@RequestParam("url") String url, Model model) {

    Optional<Long> idOpt = twService.parseStatusIdFromUrl(url);
    if (idOpt.isPresent()) {

      return "redirect:/tw/status/" + idOpt.get();
    } else {
      model.addAttribute("twError", "URLを認識できませんでした");

      return "index";
    }
  }

  @RequestMapping("/status/{id}")
  public String status(@PathVariable("id") long id, Model model) throws Exception {
    Status status = twService.getStatus(id);
    model.addAttribute("status", status);

    return "tw/status";
  }

  @RequestMapping("/media")
  public ResponseEntity<InputStreamResource> media(@RequestParam("url") String urlString) {
    try {
//      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8880));


      URL url = new URL(urlString);
      //HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(proxy);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      httpConn.setInstanceFollowRedirects(true);
      httpConn.connect();
//    String contentLength = httpConn.getHeaderField("Content-Length");
//    String contentType = httpConn.getHeaderField("Content-Type");
      HttpHeaders headers = new HttpHeaders();
      //headers.putAll(httpConn.getHeaderFields());

      httpConn.getHeaderFields().forEach((key, values) -> {
        log.debug("key:{}, values:{}", key, values);
        if (key != null) {
          headers.putIfAbsent(key, values);
        }
      });


      return ResponseEntity.status(httpConn.getResponseCode())
          .headers(headers)
          .body(new InputStreamResource(httpConn.getInputStream()));
    } catch (IOException e) {
      byte[] msg = e.getMessage().getBytes(StandardCharsets.UTF_8);
      return ResponseEntity
          .status(500)
          .body(new InputStreamResource(new ByteArrayInputStream(msg)));
    }
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(TwitterException.class)
  public ModelAndView handleTwError(TwitterException exception) {
    log.warn("error", exception);
    ExtendedModelMap model = new ExtendedModelMap();
    model.addAttribute("twError", exception);
    return new ModelAndView("tw/error", model);
  }
}
