package com.ms.tweet.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class YoutubeClient {
    @Value("${google.api.url}")
    private String googleApiUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

    public String getYouTubeVideoData(String videoId) {
        WebClient webClient = createWebClient(videoId);
        return webClient.get()
                .exchangeToMono(clientResponse ->
                        clientResponse.bodyToMono(String.class))
                .block();

    }

    private WebClient createWebClient(String videoId) {
        return WebClient.builder()
                .baseUrl(String.format(googleApiUrl, videoId, googleApiKey))
                .build();
    }
}
