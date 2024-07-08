package com.ms.tweet.service;

import main.java.com.leon.baobui.dto.HeaderResponse;
import main.java.com.leon.baobui.dto.request.IdsRequest;
import main.java.com.leon.baobui.dto.response.tweet.TweetResponse;
import main.java.com.leon.baobui.exception.ApiRequestException;
import main.java.com.leon.baobui.mapper.BasicMapper;
import main.java.com.leon.baobui.util.AuthUtil;
import com.ms.tweet.client.TagClient;
import com.ms.tweet.client.UserClient;
import com.ms.tweet.dto.request.TweetRequest;
import com.ms.tweet.dto.response.ProfileTweetImageResponse;
import com.ms.tweet.dto.response.TweetAdditionalInfoResponse;
import com.ms.tweet.dto.response.TweetUserResponse;
import com.ms.tweet.helper.TweetServiceHelper;
import com.ms.tweet.helper.TweetValidationHelper;
import com.ms.tweet.model.Tweet;
import com.ms.tweet.repository.RetweetRepository;
import com.ms.tweet.repository.TweetRepository;
import com.ms.tweet.repository.projection.*;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static main.java.com.leon.baobui.constants.ErrorMessage.TWEET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TweetService {
    private final UserClient userClient;
    private final TagClient tagClient;
    private final TweetRepository tweetRepository;
    private final RetweetRepository retweetRepository;
    private final TweetServiceHelper tweetServiceHelper;
    private final TweetValidationHelper tweetValidationHelper;
    private final BasicMapper basicMapper;
    @Transactional(readOnly = true)
    public HeaderResponse<TweetUserResponse> getUserTweets(Long userId, Pageable pageable) {
        tweetValidationHelper.validateUserProfile(userId);
        // TODO: check N + 1 query here for images field
        List<TweetUserProjection> tweets = tweetRepository.getTweetsByUserId(userId);
        List<RetweetProjection> retweets = retweetRepository.getRetweetsByUserId(userId);
        List<TweetUserProjection> userTweets = tweetServiceHelper.combineTweetsArraysOnDateOrderDsc(tweets, retweets);
        Long pinnedTweetId = userClient.getUserPinnedTweetId(userId);

        if (pinnedTweetId != null) {
            TweetUserProjection pinnedTweet = tweetRepository.getTweetById(pinnedTweetId, TweetUserProjection.class).get();
            boolean isTweetExist = userTweets.removeIf(tweet -> tweet.getId().equals(pinnedTweet.getId()));

            if (isTweetExist) {
                userTweets.add(0, pinnedTweet);
            }
        }
        Page<TweetUserProjection> tweetsPageable = tweetServiceHelper.getPageableTweetProjectionList(pageable, userTweets, tweets.size() + retweets.size());
        return basicMapper.getHeaderResponse(tweetsPageable, TweetUserResponse.class);
    }

    @Transactional
    public HeaderResponse<TweetResponse> getTweets(Pageable pageable) {
        List<Long> validUserIds = tweetValidationHelper.getValidUserIds();
        Page<TweetProjection> tweetProjectionPage = tweetRepository.getTweetsByAuthorsIds(validUserIds, pageable);
        return basicMapper.getHeaderResponse(tweetProjectionPage, TweetResponse.class);
    }

    public TweetAdditionalInfoResponse getTweetAdditionalInfoById(Long tweetId) {
        TweetAdditionalInfoProjection tweetAdditionalInfoProjection = tweetRepository
                .getTweetById(tweetId, TweetAdditionalInfoProjection.class)
                .orElseThrow(() -> new ApiRequestException(TWEET_NOT_FOUND, HttpStatus.NOT_FOUND));
        tweetValidationHelper.validateTweet(tweetAdditionalInfoProjection.isDeleted(), tweetAdditionalInfoProjection.getAuthorId());
        return basicMapper.convertToResponse(tweetAdditionalInfoProjection, TweetAdditionalInfoResponse.class);
    }
    public List<ProfileTweetImageResponse> getUserTweetImages(Long userId) {
        tweetValidationHelper.validateUserProfile(userId);
        List<ProfileTweetImageProjection> profileTweetImageProjections = tweetRepository.getUserTweetImages(userId, PageRequest.of(0, 6));
        return basicMapper.convertToResponseList(profileTweetImageProjections, ProfileTweetImageResponse.class);
    }

    @Transactional
    public TweetResponse createTweet(TweetRequest tweetRequest) {
        Tweet tweet = basicMapper.convertToResponse(tweetRequest, Tweet.class);
        return tweetServiceHelper.createTweet(tweet);
    }

    @Transactional
    public String deleteTweet(Long tweetId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        Tweet tweet = tweetRepository.getTweetByUserId(authUserId, tweetId)
                .orElseThrow(() -> new ApiRequestException(TWEET_NOT_FOUND, HttpStatus.BAD_REQUEST));
        userClient.updatePinnedTweetId(tweetId);
//        tagClient.deleteTagsByTweetId(tweetId);
        // soft delete
        tweet.setDeleted(true);
        return "Your Tweet has been deleted successfully";
    }
}
