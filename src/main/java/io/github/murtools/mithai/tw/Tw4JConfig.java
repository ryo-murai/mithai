package io.github.murtools.mithai.tw;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
public class Tw4JConfig {

  @Value("${mithai.twitter4j.debug:false}")
  private boolean debugMode;

  @Value("${TWITTER4J_OAUTH_CONSUMER_KEY}")
  private String oauthConsumerKey;
  @Value("${TWITTER4J_OAUTH_CONSUMER_SECRET}")
  private String oauthConsumeSecret;
  @Value("${TWITTER4J_OAUTH_ACCESS_TOKEN}")
  private String oauthAccessToken;
  @Value("${TWITTER4J_OAUTH_ACCESS_TOKEN_SECRET}")
  private String oauthAccessTokenSecret;

  @Bean
  public Twitter twitter() {
    ConfigurationBuilder builder = new ConfigurationBuilder();
    builder
        .setOAuthConsumerKey(oauthConsumerKey)
        .setOAuthConsumerSecret(oauthConsumeSecret)
        .setOAuthAccessToken(oauthAccessToken)
        .setOAuthAccessTokenSecret(oauthAccessTokenSecret);

    TwitterFactory factory = new TwitterFactory(builder.build());
    Twitter twitter = factory.getInstance();

    return twitter;
  }
}
