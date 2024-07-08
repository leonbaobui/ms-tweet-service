package com.ms.tweet.client;

import com.gmail.merikbest2015.configuration.FeignConfiguration;
import com.gmail.merikbest2015.dto.request.NotificationRequest;
import com.gmail.merikbest2015.dto.response.notification.NotificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.gmail.merikbest2015.constants.FeignConstants.NOTIFICATION_SERVICE;
import static com.gmail.merikbest2015.constants.FeignConstants.USER_SERVICE;
import static com.gmail.merikbest2015.constants.PathConstants.*;

@FeignClient(name = NOTIFICATION_SERVICE, url = "${service.downstream-url.ms-notification-service}", path ="/" + NOTIFICATION_SERVICE + API_V1_USER, contextId = "NotificationClient", configuration = FeignConfiguration.class)
public interface NotificationClient {
    @PostMapping(TWEET)
    NotificationResponse sendTweetNotification(@RequestBody NotificationRequest request);

    @PostMapping(MENTION)
    void sendTweetMentionNotification(@RequestBody NotificationRequest request);

    @GetMapping(TWEET_TWEET_ID)
    void sendTweetNotificationToSubscribers(@PathVariable("tweetId") Long tweetId);
}
