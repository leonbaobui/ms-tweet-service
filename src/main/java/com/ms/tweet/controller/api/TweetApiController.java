package com.ms.tweet.controller.api;

import com.gmail.merikbest2015.dto.response.chat.ChatTweetResponse;
import com.ms.tweet.service.TweetApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gmail.merikbest2015.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_TWEETS)
public class TweetApiController {
    private final TweetApiService tweetApiService;
    @GetMapping(CHAT_TWEET_ID)
    public ChatTweetResponse getChatTweet(@PathVariable("tweetId") Long tweetId) {
        return tweetApiService.getChatTweet(tweetId);
    }
}
