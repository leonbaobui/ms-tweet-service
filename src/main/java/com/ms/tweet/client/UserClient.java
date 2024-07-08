package com.ms.tweet.client;

import main.java.com.leon.baobui.configuration.FeignConfiguration;
import main.java.com.leon.baobui.dto.request.IdsRequest;
import main.java.com.leon.baobui.dto.response.tweet.TweetAdditionalInfoUserResponse;
import main.java.com.leon.baobui.dto.response.tweet.TweetAuthorResponse;
import main.java.com.leon.baobui.dto.response.user.TaggedUserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static main.java.com.leon.baobui.constants.FeignConstants.USER_SERVICE;
import static main.java.com.leon.baobui.constants.PathConstants.*;

@FeignClient(name = USER_SERVICE, url = "${service.downstream-url.ms-user-service}", path ="/" + USER_SERVICE + API_V1_USER, contextId = "UserClient", configuration = FeignConfiguration.class)
public interface UserClient {
    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(TWEET_AUTHOR_USER_ID)
    TweetAuthorResponse getTweetAuthor(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "defaultEmptyUsersList")
    @PostMapping(TAGGED_IMAGE)
    List<TaggedUserResponse> getTaggedImageUsers(@RequestBody IdsRequest idsRequest);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(IS_FOLLOWED_USER_ID)
    Boolean isUserFollowByOtherUser(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(IS_EXISTS_USER_ID)
    Boolean isUserExists(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(IS_PRIVATE_USER_ID)
    Boolean isUserHavePrivateProfile(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(IS_MY_PROFILE_BLOCKED_USER_ID)
    Boolean isMyProfileBlockedByUser(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "defaultEmptyPinnedTweetId")
    @GetMapping(TWEET_PINNED_USER_ID)
    Long getUserPinnedTweetId(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE)
    @PutMapping(MEDIA_COUNT)
    void updateMediaTweetCount(@PathVariable("increaseCount") boolean increaseCount);

    @CircuitBreaker(name = USER_SERVICE)
    @PutMapping(TWEET_COUNT)
    void updateTweetCount(@PathVariable("increaseCount") boolean increaseCount);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(USER_ID_USERNAME)
    Long getUserIdByUsername(@PathVariable("username") String username);

    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "defaultEmptyIdsList")
    @PostMapping(VALID_IDS)
    List<Long> getValidUserIds(@RequestBody IdsRequest request);

    @CircuitBreaker(name = USER_SERVICE)
    @GetMapping(TWEET_ADDITIONAL_INFO_USER_ID)
    TweetAdditionalInfoUserResponse getTweetAdditionalInfoUser(@PathVariable("userId") Long userId);

    @CircuitBreaker(name = USER_SERVICE)
    @PutMapping(TWEET_PINNED_TWEET_ID)
    void updatePinnedTweetId(@PathVariable("tweetId") Long tweetId);

    default Long defaultEmptyPinnedTweetId(Throwable throwable) {
        return 0L;
    }
}
