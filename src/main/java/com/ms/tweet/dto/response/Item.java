package com.ms.tweet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Valid
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    private String snippet;

}
