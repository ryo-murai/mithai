package io.github.murtools.mithai.tw;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
  public String status(@RequestParam("url") String url, Model model) throws Exception {

    Optional<Long> idopt = twService.parseStatusIdFromUrl(url);
    if (idopt.isPresent()) {
      Status status = twService.getStatus(idopt.get());
      model.addAttribute("status", status);

      return "tw/status";
    } else {
      model.addAttribute("twError", "URLを認識できませんでした");

      return "index";
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
