package com.ms.tweet.helper;

import main.java.com.leon.baobui.dto.request.IdsRequest;
import main.java.com.leon.baobui.dto.response.tweet.TweetAdditionalInfoUserResponse;
import main.java.com.leon.baobui.dto.response.tweet.TweetAuthorResponse;
import main.java.com.leon.baobui.dto.response.tweet.TweetListResponse;
import main.java.com.leon.baobui.dto.response.user.TaggedUserResponse;
import main.java.com.leon.baobui.util.AuthUtil;
import com.ms.tweet.client.ListsClient;
import com.ms.tweet.client.UserClient;
import com.ms.tweet.repository.BookmarkRepository;
import com.ms.tweet.repository.LikeTweetRepository;
import com.ms.tweet.repository.RetweetRepository;
import com.ms.tweet.repository.TweetRepository;
import com.ms.tweet.repository.projection.TweetUserProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TweetProjectionHelper {
    private final TweetRepository tweetRepository;
    private final RetweetRepository retweetRepository;
    private final LikeTweetRepository likeTweetRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserClient userClient;
    private final ListsClient listsClient;

    public TweetUserProjection getTweetUserProjection(Long tweetId) {
        return tweetRepository.getTweetById(tweetId, TweetUserProjection.class).get();
    }

    public boolean isUserBookmarkedTweet(Long tweetId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return bookmarkRepository.isUserBookmarkedTweet(authUserId, tweetId);
    }

    public TweetAuthorResponse getTweetAuthor(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        // This one we need to call user service because only there holds
        // all the information of a user like email, phone
        // in this tweet service, we only hold userId. Nothing else
        return userClient.getTweetAuthor(userId);
    }

    public TweetAdditionalInfoUserResponse getTweetAdditionalInfoUser(Long userId) {
        TweetAdditionalInfoUserResponse tweetAdditionalInfoUserResponse = userClient.getTweetAdditionalInfoUser(userId);
        return tweetAdditionalInfoUserResponse;
    }

    public TweetListResponse getTweetList(Long listId) {
        TweetListResponse tweetList = listsClient.getTweetList(listId);

        if (tweetList.getId() != null) {
            return tweetList;
        } else {
            return null;
        }
    }

    public List<TaggedUserResponse> getTaggedImageUsers(Long tweetId) {
        List<Long> taggedImageUserIds = tweetRepository.getTaggedImageUserIds(tweetId);
        return userClient.getTaggedImageUsers(new IdsRequest(taggedImageUserIds));
    }

    public boolean isUserLikedTweet(Long tweetId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return likeTweetRepository.isUserLikedTweet(authUserId, tweetId);
    }

    public boolean isUserRetweetedTweet(Long tweetId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return retweetRepository.isUserRetweetedTweet(authUserId, tweetId);
    }

    public boolean isUserFollowByOtherUser(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return userClient.isUserFollowByOtherUser(userId);
    }
}
