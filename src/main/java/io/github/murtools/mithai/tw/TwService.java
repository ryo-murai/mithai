package io.github.murtools.mithai.tw;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Slf4j
@Service
public class TwService {
  private final Twitter tw;

  @Autowired
  public TwService(Twitter tw) {
    this.tw = tw;
  }

  public Optional<Long> parseStatusIdFromUrl(String urlString) {
    try {
      URL url = new URL(urlString);

      String hostName = url.getHost();
      if (!hostName.contains("twitter.com")) {
        log.warn("might not a twitter link. given URL: {}", hostName);
      }

      String[] paths = url.getPath().split("/");
      String statusId = paths[paths.length - 1];  // bottom of path hierarchy

      Long id = Long.parseLong(statusId);
      return Optional.of(id);
    } catch (NumberFormatException | MalformedURLException e) {
      log.warn("could not parse the given url: {}", urlString);
      return Optional.empty();
    }
  }

  public Status getStatus(long id) throws TwitterException {
    Status status = tw.showStatus(id);
    //String text = status.getText();
    //status.getMediaEntities();
    return status;
  }

}
