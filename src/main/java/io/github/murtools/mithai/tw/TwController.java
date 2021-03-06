package io.github.murtools.mithai.tw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.github.murtools.mithai.biz.CipherService;
import io.github.murtools.mithai.http.HttpClientService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterException;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/tw")
@RequiredArgsConstructor
@Slf4j
public class TwController {
  private final TwService twService;

  private final HttpClientService httpClientService;

  private final CipherService cipherService;

  @RequestMapping("/status")
  public String status(@RequestParam("url") String url, Model model) throws IOException {

    Optional<Long> idOpt = twService.parseStatusIdFromUrl(url);
    if (idOpt.isPresent()) {
      return "redirect:/tw/status/" + idOpt.get();
    } else {
      String redirectUrl = httpClientService.getRedirectUrl(url);

      if(redirectUrl != null) {
        Optional<Long> idOptRedirect = twService.parseStatusIdFromUrl(redirectUrl);
        if (idOptRedirect.isPresent()) {
          return "redirect:/tw/status/" + idOptRedirect.get();
        } else {
          model.addAttribute("url", redirectUrl);

          try {
            String encUrl = cipherService.encryptUrl(redirectUrl);
            model.addAttribute("encUrl", encUrl);
          } catch (GeneralSecurityException e) {
            log.warn("could not encode ...", e);
            // noop
          }

          return "tw/confirm";
        }
      }

      model.addAttribute("twError", "URLを認識できませんでした");
      return "index";
    }
  }

  @RequestMapping("/status/{id}")
  public String status(@PathVariable("id") long id, Model model) throws Exception {
    Status status = twService.getStatus(id);
    model.addAttribute("status", status);

    List<MediaEntityExInfo> mediaEntities = Stream.of(status.getMediaEntities())
            .map(this::createExInfo)
            .collect(toList());
    model.addAttribute("mediaEntities", mediaEntities);

    return "tw/status";
  }

  private MediaEntityExInfo createExInfo(MediaEntity entity) {
    try {
      String encryptoUrl = cipherService.encryptUrl(entity.getMediaURL());
      return new MediaEntityExInfo(entity, encryptoUrl);
    } catch (GeneralSecurityException e) {
      log.warn("could not encrypt media url", e);
      return new  MediaEntityExInfo(entity, "/");  // todo: set alternative url
    }
  }

  @Value
  public static class MediaEntityExInfo {
    private final MediaEntity entity;
    private final String encUrl;
  }

  @RequestMapping("/media")
  public ResponseEntity<InputStreamResource> media(@RequestParam("url") String urlString) {
    try {
      HttpURLConnection httpConn = httpClientService.createConnection(urlString);
      httpConn.setInstanceFollowRedirects(true);
      httpConn.connect();

      HttpHeaders headers = HttpClientService.createHeadersFrom(httpConn);

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
