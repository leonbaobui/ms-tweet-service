package com.ms.tweet.repository;

import com.ms.tweet.model.Retweet;
import com.ms.tweet.repository.projection.RetweetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    @Query("SELECT retweet FROM Retweet retweet " +
            "WHERE retweet.userId = :userId " +
            "ORDER BY retweet.retweetDate DESC")
    List<RetweetProjection> getRetweetsByUserId(@Param("userId") Long userId);

    @Query("SELECT retweet.userId FROM Retweet retweet " +
            "WHERE retweet.tweetId = :tweetId")
    List<Long> getRetweetsUserIds(@Param("tweetId") Long tweetId);

    @Query("SELECT CASE WHEN count(retweet) > 0 THEN true ELSE false END " +
            "FROM Retweet retweet " +
            "WHERE retweet.tweetId = :tweetId AND retweet.userId = :userId")
    boolean isUserRetweetedTweet(@Param("userId") Long userId, @Param("tweetId") Long tweetId);

    @Query("SELECT count(retweet) " +
            "FROM Retweet retweet " +
            "WHERE retweet.tweetId = :tweetId")
    Long getRetweetSize(@Param("tweetId") Long tweetId);
}
