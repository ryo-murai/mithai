package io.github.murtools.mithai.tw;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class TwServiceTest {
  @Test
  public void parseStatusIdFromUrl() throws Exception {
    TwService service = new TwService(null);

    String validUrl = "http://twitter.com/user_id/status/831326189478252544";
    Optional<Long> idOpt = service.parseStatusIdFromUrl(validUrl);
    assertThat(idOpt.get(), is(831326189478252544L));


    String otherHostUrl = "http://example.com/user_id/status/831326189478252643";
    Optional<Long> idOpt2 = service.parseStatusIdFromUrl(otherHostUrl);
    assertThat(idOpt2.get(), is(831326189478252643L));

    String noIdUrl = "http://example.com/user_id";
    Optional<Long> idOpt3 = service.parseStatusIdFromUrl(noIdUrl);
    assertThat(idOpt3.isPresent(), is(false));

    String malformedUrl = "1//example.com:90000/showid/5555";
    Optional<Long> idOpt4 = service.parseStatusIdFromUrl(malformedUrl);
    assertThat(idOpt4.isPresent(), is(false));
  }

  @Test
  public void getStatus() throws Exception {

  }

}