package com.example.caloryxbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${rest.client.connect-timeout}")
    private int connectTimeout;

    @Value("${rest.client.read-timeout}")
    private int readTimeout;

    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 @Value("${usda.base-url}") String baseUrl) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(connectTimeout).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(readTimeout).toMillis());

        return builder
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}