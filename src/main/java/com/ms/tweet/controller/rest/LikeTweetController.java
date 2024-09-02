package com.ms.tweet.controller.rest;

import lombok.RequiredArgsConstructor;

import com.ms.tweet.service.LikeTweetService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import main.java.com.leon.baobui.dto.response.notification.NotificationResponse;
import main.java.com.leon.baobui.dto.response.notification.NotificationTweetResponse;
import static main.java.com.leon.baobui.constants.PathConstants.LIKE_USER_ID_TWEET_ID;
import static main.java.com.leon.baobui.constants.PathConstants.UI_V1_TWEETS;

@Controller
@RequestMapping(UI_V1_TWEETS)
@RequiredArgsConstructor
public class LikeTweetController {

    private final LikeTweetService likeTweetService;
    @GetMapping(LIKE_USER_ID_TWEET_ID)
    public ResponseEntity<NotificationTweetResponse> likeTweet(@PathVariable("userId") Long userId,
                                                               @PathVariable("tweetId") Long tweetId) {
        NotificationResponse notificationResponse = likeTweetService.likeTweet(tweetId);
        return ResponseEntity.ok(notificationResponse.getTweet());
    }
}
