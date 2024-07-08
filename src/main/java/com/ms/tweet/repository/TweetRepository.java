package com.ms.tweet.repository;

import com.ms.tweet.model.Tweet;
import com.ms.tweet.repository.projection.ProfileTweetImageProjection;
import com.ms.tweet.repository.projection.TweetProjection;
import com.ms.tweet.repository.projection.TweetUserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    // TODO: check blocked user if existed in userIds list
    @Query("SELECT tweet FROM Tweet tweet " +
            "WHERE tweet.authorId IN :userIds " +
            "AND tweet.addressedUsername IS NULL " +
            "AND tweet.scheduledDate IS NULL " +
            "AND tweet.deleted = false " +
            "ORDER BY tweet.dateTime DESC")
    Page<TweetProjection> getTweetsByAuthorsIds(@Param("userIds") List<Long> userIds, Pageable pageable);

    @Query("SELECT DISTINCT tweet.authorId FROM Tweet tweet " +
            "WHERE tweet.addressedUsername IS NULL " +
            "AND tweet.scheduledDate IS NULL " +
            "AND tweet.deleted = false")
    List<Long> getTweetAuthorIds();

    @Query("SELECT tweet FROM Tweet tweet WHERE tweet.id = :tweetId")
    <T> Optional<T> getTweetById(@Param("tweetId") Long tweetId, Class<T> type);

    @Query("SELECT tweet FROM Tweet tweet " +
            "WHERE tweet.authorId = :userId " +
            "AND tweet.addressedUsername IS NULL " +
            "AND tweet.scheduledDate IS NULL " +
            "AND tweet.deleted = false " +
            "ORDER BY tweet.dateTime DESC")
    List<TweetUserProjection> getTweetsByUserId(@Param("userId") Long userId);

    @Query("SELECT tweet FROM Tweet tweet " +
            "WHERE tweet.id = :tweetId " +
            "AND tweet.authorId = :userId")
    Optional<Tweet> getTweetByUserId(@Param("userId") Long userId, @Param("tweetId") Long tweetId);

    @Query("SELECT tweet.id AS tweetId, image.id AS imageId, image.src AS src FROM Tweet tweet " +
            "LEFT JOIN tweet.images image " +
            "WHERE tweet.scheduledDate IS NULL " +
            "AND tweet.authorId = :userId " +
            "AND image.id IS NOT NULL " +
            "AND tweet.deleted = false " +
            "ORDER BY tweet.dateTime DESC")
    List<ProfileTweetImageProjection> getUserTweetImages(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT tagged " +
            "FROM Tweet tweet " +
            "LEFT JOIN tweet.taggedImageUsers tagged " +
            "WHERE tweet.id = :tweetId")
    List<Long> getTaggedImageUserIds(@Param("tweetId") Long tweetId);
}
