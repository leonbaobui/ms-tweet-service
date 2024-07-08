package com.ms.tweet.repository;

import com.ms.tweet.model.Bookmark;
import com.ms.tweet.model.PollChoice;
import com.ms.tweet.model.PollChoiceVoted;
import com.ms.tweet.repository.projection.VotedUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollChoiceVotedRepository extends JpaRepository<PollChoiceVoted, Long> {
    @Query("SELECT pollChoiceVoted.votedUserId FROM PollChoiceVoted pollChoiceVoted WHERE pollChoiceVoted.pollChoiceId = :pollChoiceId")
    List<VotedUserProjection> getVotedUserIds(@Param("pollChoiceId") Long pollChoiceId);
}
