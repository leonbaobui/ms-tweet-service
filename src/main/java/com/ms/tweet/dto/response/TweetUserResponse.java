package com.ms.tweet.dto.response;

import main.java.com.leon.baobui.dto.response.tweet.TweetResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TweetUserResponse extends TweetResponse {
    private List<Long> retweetsUserIds;
}
