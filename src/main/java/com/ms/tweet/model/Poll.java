package com.ms.tweet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "polls_seq")
    @SequenceGenerator(name = "polls_seq", sequenceName = "polls_seq", initialValue = 100, allocationSize = 1)
    private Long id;

    @NonNull
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @NonNull
    @OneToOne(mappedBy = "poll")
    private Tweet tweet;

    @NonNull
    @OneToMany
    private List<PollChoice> pollChoices;
}
