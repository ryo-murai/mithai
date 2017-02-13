package io.github.murtools.mithai.tw;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

@Service
public class TwService {
  private final Twitter tw;

  @Autowired
  public TwService(Twitter tw) {
    this.tw = tw;
  }

  public Optional<Long> parseStatusIdFromUrl(String url) {
    try {
      Long id = Long.parseLong(url);
      return Optional.of(id);
    } catch (NumberFormatException e) {
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
