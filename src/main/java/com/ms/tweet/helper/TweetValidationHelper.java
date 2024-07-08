package com.ms.tweet.helper;

import com.gmail.merikbest2015.dto.request.IdsRequest;
import com.gmail.merikbest2015.exception.ApiRequestException;
import com.gmail.merikbest2015.util.AuthUtil;
import com.ms.tweet.client.UserClient;
import com.ms.tweet.repository.TweetRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gmail.merikbest2015.constants.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class TweetValidationHelper {
    private final UserClient userClient;
    private final TweetRepository tweetRepository;
    public List<Long> getValidUserIds() {
        List<Long> tweetAuthorIds = tweetRepository.getTweetAuthorIds();
        return userClient.getValidUserIds(new IdsRequest(tweetAuthorIds));
    }
    public void validateTweet(boolean isDeleted, Long tweetAuthorId) {
        if (isDeleted) {
            throw new ApiRequestException(TWEET_DELETED, HttpStatus.BAD_REQUEST);
        }
        checkIsValidUserProfile(tweetAuthorId);
    }
    public void validateUserProfile(Long userId) {
        if (!userClient.isUserExists(userId)) {
            throw new ApiRequestException(String.format(USER_ID_NOT_FOUND, userId), HttpStatus.NOT_FOUND);
        }
        checkIsValidUserProfile(userId);
    }

    public void checkIsValidUserProfile(Long userId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();

        if (!userId.equals(authUserId)) {
            if (userClient.isUserHavePrivateProfile(userId)) {
                throw new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            if (userClient.isMyProfileBlockedByUser(userId)) {
                throw new ApiRequestException(USER_PROFILE_BLOCKED, HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void checkTweetTextLength(String text) {
        if (text.isEmpty() || text.length() > 280) {
            throw new ApiRequestException(INCORRECT_TWEET_TEXT_LENGTH, HttpStatus.BAD_REQUEST);
        }
    }
}
