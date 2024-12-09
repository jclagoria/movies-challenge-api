package com.directa24.main.challenge.api.repository.impl;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.model.MoviesResponse;
import com.directa24.main.challenge.api.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MovieRepositoryImpl implements MovieRepository {
    private final WebClient webClient;
    private final int concurrencyLimit;

    public MovieRepositoryImpl(@Value("${api.concurrency.limit}") int concurrencyLimit,
                               @Qualifier("movieWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.concurrencyLimit = concurrencyLimit;
    }

    /**
     * Fetches all movies by retrieving the initial page to determine total pages,
     * then fetching each page concurrently up to the configured concurrency limit.
     *
     * @return Flux<Movie> A reactive stream of Movie objects.
     */
    @Override
    public Flux<Movie> fetchAllMovies() {
        log.info("Starting to fetch all movies");

        return fetchPage(1)
                .flatMapMany(initialPage -> {
                    int totalPages = initialPage.getTotalPages();
                    return Flux.range(1, totalPages)
                            .flatMap(this::fetchPageMovies, concurrencyLimit);
                })
                .doOnError(e -> log.error("Error fetching all movies: {}", e.getMessage()))
                .onErrorResume(e -> Flux.empty()); // Fail gracefully with an empty result
    }

    /**
     * Fetches a specific page of movies from the API.
     *
     * @param page the page number to fetch.
     * @return Mono<MoviesResponse> A reactive Mono containing the page's MoviesResponse.
     */
    private Mono<MoviesResponse> fetchPage(int page) {
        log.info("Fetching page {}", page);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search").queryParam("page", page).build())
                .header("Accept", "application/json") // Force JSON response
                .retrieve()
                .bodyToMono(String.class) // Fetch raw response as String
                .flatMap(rawResponse -> {
                    try {
                        MoviesResponse moviesResponse =
                                new ObjectMapper().readValue(rawResponse, MoviesResponse.class);
                        return Mono.just(moviesResponse);
                    } catch (Exception e) {
                        log.error("Error deserializing response for page {}: {}", page, e.getMessage());
                        return Mono.error(e);
                    }
                });
    }

    /**
     * Fetches movies for a specific page by transforming the MoviesResponse into a Flux of movies.
     *
     * @param page the page number to fetch movies from.
     * @return Flux<Movie> A reactive stream of Movie objects from the specified page.
     */
    private Flux<Movie> fetchPageMovies(int page) {
        return fetchPage(page)
                .flatMapMany(response -> Flux.fromIterable(response.getData()))
                .onErrorResume(e -> {
                    log.error("Failed to fetch movies from page {}: {}", page, e.getMessage());
                    return Flux.empty();
                });
    }
}
