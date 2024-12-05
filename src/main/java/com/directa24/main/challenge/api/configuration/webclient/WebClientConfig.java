package com.directa24.main.challenge.api.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final String BASE_URL_API_ERON_MOVIES;

    public WebClientConfig(@Value("${movie.endpoint.url}") String baseUrl) {
        this.BASE_URL_API_ERON_MOVIES = baseUrl;
    }

    @Bean
    public WebClient movieWebClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL_API_ERON_MOVIES)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // Configurable size
                .build();    }

}
