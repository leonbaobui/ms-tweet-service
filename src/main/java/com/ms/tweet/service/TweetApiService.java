package com.ms.tweet.service;

import main.java.com.leon.baobui.dto.response.chat.ChatTweetResponse;
import main.java.com.leon.baobui.dto.response.notification.NotificationTweetResponse;
import main.java.com.leon.baobui.mapper.BasicMapper;
import com.ms.tweet.repository.TweetRepository;
import com.ms.tweet.repository.projection.ChatTweetProjection;
import com.ms.tweet.repository.projection.NotificationTweetProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TweetApiService {
    private final BasicMapper basicMapper;
    private final TweetRepository tweetRepository;
    public ChatTweetResponse getChatTweet(Long tweetId) {
        ChatTweetProjection chatTweetProjection = tweetRepository.getTweetById(tweetId, ChatTweetProjection.class)
                .get();
        return basicMapper.convertToResponse(chatTweetProjection, ChatTweetResponse.class);
    }

    public NotificationTweetResponse getNotificationTweet(Long tweetId) {
        NotificationTweetProjection notificationTweetProjection = tweetRepository.getTweetById(tweetId, NotificationTweetProjection.class).get();
        return basicMapper.convertToResponse(notificationTweetProjection, NotificationTweetResponse.class);
    }
}
