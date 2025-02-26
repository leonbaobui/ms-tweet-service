package com.ms.tweet.client;

import main.java.com.leon.baobui.configuration.FeignConfiguration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static main.java.com.leon.baobui.constants.FeignConstants.TAG_SERVICE;
import static main.java.com.leon.baobui.constants.PathConstants.API_V1_TAGS;
import static main.java.com.leon.baobui.constants.PathConstants.DELETE_TWEET_ID;

@CircuitBreaker(name = TAG_SERVICE)
@FeignClient(value = TAG_SERVICE, url = "${service.downstream-url.ms-tag-service}", path = API_V1_TAGS, configuration = FeignConfiguration.class)
public interface TagClient {
    @DeleteMapping(DELETE_TWEET_ID)
    void deleteTagsByTweetId(@PathVariable("tweetId") Long tweetId);
}
