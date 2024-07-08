package com.ms.tweet.service;

import com.gmail.merikbest2015.dto.response.chat.ChatTweetResponse;
import com.gmail.merikbest2015.mapper.BasicMapper;
import com.ms.tweet.repository.TweetRepository;
import com.ms.tweet.repository.projection.ChatTweetProjection;
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
}
