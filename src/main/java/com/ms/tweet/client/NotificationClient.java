package com.ms.tweet.client;

import main.java.com.leon.baobui.configuration.FeignConfiguration;
import main.java.com.leon.baobui.dto.request.NotificationRequest;
import main.java.com.leon.baobui.dto.response.notification.NotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static main.java.com.leon.baobui.constants.FeignConstants.NOTIFICATION_SERVICE;
import static main.java.com.leon.baobui.constants.FeignConstants.USER_SERVICE;
import static main.java.com.leon.baobui.constants.PathConstants.*;

@FeignClient(name = NOTIFICATION_SERVICE, url = "${service.downstream-url.ms-notification-service}", path ="/" + NOTIFICATION_SERVICE + API_V1_USER, contextId = "NotificationClient", configuration = FeignConfiguration.class)
public interface NotificationClient {
    @PostMapping(TWEET)
    NotificationResponse sendTweetNotification(@RequestBody NotificationRequest request);

    @PostMapping(MENTION)
    void sendTweetMentionNotification(@RequestBody NotificationRequest request);

    @GetMapping(TWEET_TWEET_ID)
    void sendTweetNotificationToSubscribers(@PathVariable("tweetId") Long tweetId);
}
