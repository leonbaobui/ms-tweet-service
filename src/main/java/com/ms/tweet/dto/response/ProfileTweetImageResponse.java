package com.ms.tweet.dto.response;

import lombok.Data;

@Data
public class ProfileTweetImageResponse {
    private Long tweetId;
    private Long imageId;
    private String src;
}
