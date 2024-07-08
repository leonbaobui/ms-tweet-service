package com.ms.tweet.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YoutubeVideoDataResponse implements Serializable {
    private String kind;
    private String etag;
    private List<Item> items;
}
