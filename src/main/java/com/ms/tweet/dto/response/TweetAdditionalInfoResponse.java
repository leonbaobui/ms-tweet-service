package com.ms.tweet.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.java.com.leon.baobui.dto.response.tweet.TweetAdditionalInfoUserResponse;
import main.java.com.leon.baobui.enums.ReplyType;
import lombok.Data;

@Data
public class TweetAdditionalInfoResponse {
    private String text;
    private ReplyType replyType;
    private Long addressedTweetId;
    private TweetAdditionalInfoUserResponse user;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
}