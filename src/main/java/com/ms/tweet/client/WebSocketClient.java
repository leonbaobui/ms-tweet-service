package com.ms.tweet.client;

import main.java.com.leon.baobui.configuration.FeignConfiguration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static main.java.com.leon.baobui.constants.FeignConstants.WEBSOCKET_SERVICE;
import static main.java.com.leon.baobui.constants.PathConstants.API_V1_WEBSOCKET;

@CircuitBreaker(name = WEBSOCKET_SERVICE)
@FeignClient(name = WEBSOCKET_SERVICE, url = "${service.downstream-url.ms-user-service}", configuration = FeignConfiguration.class)
public interface WebSocketClient {
    @PostMapping("/" + WEBSOCKET_SERVICE + API_V1_WEBSOCKET)
    void send(@RequestParam("destination") String destination, @RequestBody Object request);
}
