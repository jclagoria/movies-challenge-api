package com.directa24.main.challenge.api.controller;

import com.directa24.main.challenge.api.dto.DirectorsResponse;
import com.directa24.main.challenge.api.service.MovieInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie Controller", description = "Endpoints related to Movies and Directors")
@Slf4j
public class MovieController {

    private final MovieInfoService movieInfoService;

    public MovieController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @GetMapping("/directors")
    @Operation(summary = "Fetch Directors", description = "Fetch directors with movies exceeding the given threshold")
    public Mono<ResponseEntity<DirectorsResponse>> getDirectors(
            @Parameter(description = "Threshold for filtering directors by movie count")
            @RequestParam int threshold) {

        if (threshold <= 0) {
            return Mono.error(new IllegalArgumentException("Threshold must be greater than zero"));
        }

        return movieInfoService
                .getDirectors(threshold)
                .map(directors -> ResponseEntity.ok().body(new DirectorsResponse(directors)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSubscribe(subscription ->
                        log.info("Fetching directors with threshold: {}", threshold))
                .doOnSuccess(response ->
                        log.info("Response: {}", response.getBody()));
    }

}
