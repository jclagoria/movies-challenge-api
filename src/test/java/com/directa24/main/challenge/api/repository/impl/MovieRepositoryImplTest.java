package com.directa24.main.challenge.api.repository.impl;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.model.MoviesResponse;
import com.directa24.main.challenge.mocks.MovieTestDataHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class MovieRepositoryImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    private MovieRepositoryImpl movieRepository;

    private static final int concurrencyLimit = 2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieRepository = new MovieRepositoryImpl(concurrencyLimit, webClient);
    }

    @Test
    void fetchAllMovies_success() throws Exception {

        MoviesResponse page1Response = MovieTestDataHelper.getMockMoviesResponse();

        when(webClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec); // Mock uri() with Function

        when(requestHeadersSpec.header(eq("Accept"),
                eq("application/json"))).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenAnswer(invocation -> responseSpec);

        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.just(MovieTestDataHelper.warJsonPage1)); // For both pages

        when(objectMapper.readValue(anyString(), eq(MoviesResponse.class)))
                .thenReturn(page1Response);

        Flux<Movie> result = movieRepository.fetchAllMovies();

        StepVerifier.create(result)
                .expectNextMatches(movie -> movie.getTitle().equals("Movie 1"))
                .expectNextMatches(movie -> movie.getTitle().equals("Movie 2"))
                .verifyComplete();

        verify(webClient, times(2)).get();
    }

    @Test
    void fetchAllMovies_errorGracefulFallback() {
        when(webClient.get()).thenAnswer(invocation -> requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec); // Mock uri() with Function
        when(requestHeadersSpec.header(eq("Accept"),
                eq("application/json"))).thenAnswer(invocation -> requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(String.class)))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        Flux<Movie> result = movieRepository.fetchAllMovies();

        StepVerifier.create(result)
                .verifyComplete(); // Gracefully completes with no items

        verify(webClient).get();
    }


}