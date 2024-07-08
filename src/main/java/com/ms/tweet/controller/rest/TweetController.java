package com.ms.tweet.controller.rest;

import com.gmail.merikbest2015.dto.HeaderResponse;
import com.gmail.merikbest2015.dto.response.tweet.TweetResponse;
import com.ms.tweet.client.WebSocketClient;
import com.ms.tweet.dto.request.TweetRequest;
import com.ms.tweet.dto.response.ProfileTweetImageResponse;
import com.ms.tweet.dto.response.TweetAdditionalInfoResponse;
import com.ms.tweet.dto.response.TweetUserResponse;
import com.ms.tweet.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.WebSocket;
import java.util.List;

import static com.gmail.merikbest2015.constants.PathConstants.*;
import static com.gmail.merikbest2015.constants.WebsocketConstants.TOPIC_FEED_ADD;
import static com.gmail.merikbest2015.constants.WebsocketConstants.TOPIC_USER_ADD_TWEET;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_TWEETS)
public class TweetController {
    private final TweetService tweetService;
    private final WebSocketClient webSocketClient;
    @GetMapping(USER_USER_ID)
    public ResponseEntity<List<TweetUserResponse>> getUserTweets(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        HeaderResponse<TweetUserResponse> response = tweetService.getUserTweets(userId, pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @GetMapping(TWEET_ID_INFO)
    public ResponseEntity<TweetAdditionalInfoResponse> getTweetAdditionalInfoById(
            @PathVariable("tweetId") Long tweetId) {
        return ResponseEntity.ok(tweetService.getTweetAdditionalInfoById(tweetId));
    }

    @GetMapping(IMAGES_USER_ID)
    public ResponseEntity<List<ProfileTweetImageResponse>> getUserTweetImages(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(tweetService.getUserTweetImages(userId));
    }

    @GetMapping()
    public ResponseEntity<List<TweetResponse>> getTweets(@PageableDefault Pageable pageable) {
        HeaderResponse<TweetResponse> response = tweetService.getTweets(pageable);
        return ResponseEntity.ok().headers(response.getHeaders()).body(response.getItems());
    }

    @PostMapping
    public ResponseEntity<TweetResponse> createTweet(@RequestBody TweetRequest tweetRequest) {
        TweetResponse tweet = tweetService.createTweet(tweetRequest);
        webSocketClient.send(TOPIC_FEED_ADD, tweet);
        webSocketClient.send(TOPIC_USER_ADD_TWEET + tweet.getUser().getId(), tweet);
        return ResponseEntity.ok(tweet);
    }

    @DeleteMapping(TWEET_ID)
    public ResponseEntity<String> deleteTweet(@PathVariable("tweetId") Long tweetId) {
        return ResponseEntity.ok(tweetService.deleteTweet(tweetId));
    }
}
