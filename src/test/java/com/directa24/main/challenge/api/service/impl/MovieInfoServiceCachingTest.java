package com.directa24.main.challenge.api.service.impl;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.repository.MovieRepository;
import com.directa24.main.challenge.api.service.MovieInfoService;
import com.directa24.main.challenge.mocks.MovieTestDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class MovieInfoServiceCachingTest {

    @Autowired
    private MovieInfoService movieInfoService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private MovieRepository movieRepository;

    private static final int PROCESSING_THRESHOLD = 5;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache("directorsCache");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void testCachingBehavior() {

        when(movieRepository.fetchAllMovies()).thenReturn(
                Flux.just(MovieTestDataHelper.movieMock1(), MovieTestDataHelper.movieMock2(),
                        MovieTestDataHelper.movieMock3()));

        // First call to populate the cache
        StepVerifier.create(movieInfoService.getDirectors(1))
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();

        // Verify that the method on the repository was called once
        verify(movieRepository, times(1)).fetchAllMovies();

        // Second call should fetch from the cache
        StepVerifier.create(movieInfoService.getDirectors(1))
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();

        // Verify that the repository method is not called again due to caching
        verify(movieRepository, times(1)).fetchAllMovies();
    }

    @Test
    void testCacheEvictionOnDifferentThresholds() {

        when(movieRepository.fetchAllMovies()).thenReturn(Flux.just(
                MovieTestDataHelper.movieMock1(), MovieTestDataHelper.movieMock2(),
                MovieTestDataHelper.movieMock3()));

        // First call with threshold 1
        StepVerifier.create(movieInfoService.getDirectors(1))
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();

        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cacheManager
                        .getCache("directorsCache").getNativeCache();
        assertTrue(caffeineCache.estimatedSize() > 0, "Cache should contain an entry");

        StepVerifier.create(movieInfoService.getDirectors(2))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cacheManager
                        .getCache("directorsCache").getNativeCache();
        assertTrue(caffeineCache.estimatedSize() == 2, "Cache should contain two entry");


        // Verify that the repository was called twice due to different keys in cache
        verify(movieRepository, times(2)).fetchAllMovies();
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        when(movieRepository.fetchAllMovies()).thenReturn(Flux.just(
                MovieTestDataHelper.movieMock1(), MovieTestDataHelper.movieMock2(),
                MovieTestDataHelper.movieMock3()));
        // First call to populate the cache
        StepVerifier.create(movieInfoService.getDirectors(1))
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();

        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cacheManager
                        .getCache("directorsCache").getNativeCache();
        assertTrue(caffeineCache.estimatedSize() > 0, "Cache should contain an entry");

        // Trigger manual cleanup and wait
        caffeineCache.cleanUp();
        Thread.sleep(Duration.ofMinutes(2).toMillis()); // Exceeds cache expiry duration

        // Re-validate cache size
        caffeineCache.cleanUp();
        assertEquals(0, caffeineCache.estimatedSize(), "Cache should be empty after expiration");

        // Act - Second call: Data should be re-fetched as cache is expired
        Mono<List<String>> result2 = movieInfoService.getDirectors(1);

        StepVerifier.create(result2)
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();

        caffeineCache =
                (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cacheManager
                        .getCache("directorsCache").getNativeCache();
        assertTrue(caffeineCache.estimatedSize() > 0, "Cache should have one entry");

        // Assert - Verify that the repository method was called twice
        verify(movieRepository, times(2)).fetchAllMovies();
    }

}
