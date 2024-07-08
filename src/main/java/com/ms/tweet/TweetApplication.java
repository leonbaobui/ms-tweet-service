package com.ms.tweet;

import com.gmail.merikbest2015.configuration.SharedConfiguration;
import com.gmail.merikbest2015.mapper.BasicMapper;
import com.gmail.merikbest2015.security.JwtProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.ms.tweet", "com.gmail.merikbest2015"} )
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableFeignClients
@Import({JwtProvider.class, BasicMapper.class, SharedConfiguration.class})
public class TweetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweetApplication.class, args);
	}

}
