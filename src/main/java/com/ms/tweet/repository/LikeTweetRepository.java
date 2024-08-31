package com.ms.tweet.repository;

import com.ms.tweet.model.Bookmark;
import com.ms.tweet.model.LikeTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeTweetRepository extends JpaRepository<LikeTweet, Long> {
    @Query("SELECT CASE WHEN count(likeTweet) > 0 THEN true ELSE false END " +
            "FROM LikeTweet likeTweet " +
            "WHERE likeTweet.userId = :userId AND likeTweet.tweetId = :tweetId")
    boolean isUserLikedTweet(@Param("userId") Long userId, @Param("tweetId") Long tweetId);

    @Query("SELECT count(likeTweet) FROM LikeTweet likeTweet WHERE likeTweet.id = :tweetId")
    Long getLikedTweetsSize(@Param("tweetId") Long tweetId);
}
