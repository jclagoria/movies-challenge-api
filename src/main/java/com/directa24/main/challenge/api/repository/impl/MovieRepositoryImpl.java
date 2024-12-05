package com.directa24.main.challenge.api.repository.impl;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.model.MoviesResponse;
import com.directa24.main.challenge.api.repository.MovieRepository;
import com.directa24.main.challenge.api.repository.util.ResponseParser;
import com.directa24.main.challenge.api.repository.util.UriBuilderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Repository
@Slf4j
public class MovieRepositoryImpl implements MovieRepository {

    private final WebClient webClient;
    private final ResponseParser responseParser;
    private final int CONCURRENCY_LIMIT;

    public MovieRepositoryImpl(@Value("${api.concurrency.limit}") int concurrencyLimit, @Qualifier("movieWebClient") WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.responseParser = new ResponseParser(objectMapper);
        this.CONCURRENCY_LIMIT = concurrencyLimit;
    }

    /**
     * Fetches all movies by retrieving the initial page to determine total pages,
     * then fetching each page sequentially with a limit of 5 concurrent requests.
     *
     * @return Flux<Movie> A reactive stream of Movie objects.
     */
    @Override
    public Flux<Movie> fetchAllMovies() {
        log.info("Fetching all movies");
        return fetchInitialPage()
                .flatMapMany(firstPageData -> {
                    int totalPages = firstPageData.getTotalPages();
                    return Flux.range(1, totalPages)
                            .flatMapSequential(this::fetchMoviesByPage, CONCURRENCY_LIMIT); // Process 5 pages concurrently
                });
    }

    /**
     * Fetches the initial page of movies to determine pagination details.
     *
     * @return Mono<MoviesResponse> A Mono containing the response from the initial page.
     */
    private Mono<MoviesResponse> fetchInitialPage() {
        log.info("Fetching initial page");
        return webClient.get()
                .uri(UriBuilderUtil.buildSearchUri("/search", 1))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response ->
                        responseParser.parseResponse(response, MoviesResponse.class))
                .onErrorResume(e -> {
                    log.error("Failed to fetch initial page {}", e.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch initial page", e));
                });
    }

    /**
     * Fetches movies from a specific page and parses the response into Movie objects.
     *
     * @param page The page number to fetch.
     * @return Flux<Movie> A reactive stream of Movie objects from the specified page.
     */
    private Flux<Movie> fetchMoviesByPage(int page) {

        log.info("Fetching movies from page {}", page);
        return webClient.get()
                .uri(UriBuilderUtil.buildSearchUri("/search", page))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response ->
                        responseParser.parseResponse(response, MoviesResponse.class))
                .flatMapMany(moviesResponse ->
                        Flux.fromIterable(moviesResponse.getData()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))) // Retry transient errors
                .onErrorResume(e -> {
                    log.error("Failed to fetch movies {}", e.getMessage());
                    return Flux.empty();
                });
    }

}
