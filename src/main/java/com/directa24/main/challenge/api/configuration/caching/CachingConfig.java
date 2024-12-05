package com.directa24.main.challenge.api.configuration.caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CachingConfig {

    @Value("${cache.expiry.minutes:5}")
    private long cacheExpiryMinutes;

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(cacheExpiryMinutes))
                .maximumSize(1000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("directorsCache");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
