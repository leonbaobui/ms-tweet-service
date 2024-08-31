package com.ms.tweet.repository.projection;

import main.java.com.leon.baobui.dto.response.tweet.TweetAdditionalInfoUserResponse;
import main.java.com.leon.baobui.enums.ReplyType;
import org.springframework.beans.factory.annotation.Value;

public interface TweetAdditionalInfoProjection {
    String getText();
    ReplyType getReplyType();
    Long getAddressedTweetId();
    boolean isDeleted();
    Long getAuthorId();

    @Value("#{@tweetProjectionHelper.getTweetAdditionalInfoUser(target.authorId)}")
    TweetAdditionalInfoUserResponse getUser();
}