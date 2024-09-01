package com.ms.tweet.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import com.ms.tweet.client.UserClient;
import com.ms.tweet.client.WebSocketClient;
import com.ms.tweet.helper.TweetServiceHelper;
import com.ms.tweet.helper.TweetValidationHelper;
import com.ms.tweet.model.LikeTweet;
import com.ms.tweet.model.Tweet;
import com.ms.tweet.repository.LikeTweetRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.com.leon.baobui.dto.response.notification.NotificationResponse;
import main.java.com.leon.baobui.enums.NotificationType;

import static main.java.com.leon.baobui.constants.WebsocketConstants.TOPIC_USER_UPDATE_TWEET;

@Service
@RequiredArgsConstructor
public class LikeTweetService {
    private final LikeTweetRepository likeTweetRepository;
    private final TweetValidationHelper tweetValidationHelper;
    private final TweetServiceHelper tweetServiceHelper;
    private final UserClient userClient;
    private final WebSocketClient webSocketClient;

    @Transactional
    public NotificationResponse likeTweet(Long userId, Long tweetId) {
        Tweet tweet = tweetValidationHelper.checkValidTweet(tweetId);
        Optional<LikeTweet> likeTweet = Optional.ofNullable(likeTweetRepository.getLikedTweet(tweetId, userId));
        boolean isTweetLiked = likeTweet.isEmpty();
        if (isTweetLiked) {
            likeTweetRepository.save(new LikeTweet(userId, tweetId));
            userClient.updateLikeCount(true);
        } else {
            likeTweetRepository.delete(likeTweet.get());
            userClient.updateLikeCount(false);
        }
        // Domain driven design here? Where notification responsible for sending tweet reaction to the author user not directly via websocket client?
        NotificationResponse notificationResponse = tweetServiceHelper.sendNotification(NotificationType.LIKE, isTweetLiked, tweet.getAuthorId(), userId, tweetId);
        // Update auth user the notification response via websocketClient?
        webSocketClient.send(TOPIC_USER_UPDATE_TWEET + userId, notificationResponse);
        return notificationResponse;
    }

}
