package com.ms.tweet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "tweet_images")
public class TweetImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tweet_image_seq")
    @SequenceGenerator(name = "tweet_image_seq", sequenceName = "tweet_image_seq", initialValue = 100, allocationSize = 1)
    private Long id;

    @NonNull
    @Column(name = "src")
    private String src;
}
