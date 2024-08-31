package com.ms.tweet.repository;

import com.ms.tweet.model.Bookmark;
import com.ms.tweet.model.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query("SELECT CASE WHEN count(bookmark) > 0 THEN true ELSE false END " +
            "FROM Bookmark bookmark " +
            "WHERE bookmark.userId = :userId " +
            "AND bookmark.tweetId = :tweetId")
    boolean isUserBookmarkedTweet(Long userId, Long tweetId);
}
