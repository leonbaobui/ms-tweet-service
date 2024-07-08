package com.ms.tweet.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "gif_image")
public class GifImage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gif_image_seq")
    @SequenceGenerator(name = "gif_image_seq", sequenceName = "gif_image_seq", initialValue = 100, allocationSize = 1)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "width")
    private Long width;

    @Column(name = "height")
    private Long height;
}
