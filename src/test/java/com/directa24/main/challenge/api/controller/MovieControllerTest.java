package com.directa24.main.challenge.api.controller;

import com.directa24.main.challenge.api.security.SecurityConfig;
import com.directa24.main.challenge.api.service.MovieInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@Import(SecurityConfig.class)
@WebFluxTest(controllers = MovieController.class)
class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;

    @Test
    void testGetDirectors_Success() {
        // Arrange
        List<String> mockDirectors = List.of("Martin Scorsese", "Woody Allen");
        when(movieInfoService.getDirectors(anyInt())).thenReturn(Mono.just(mockDirectors));

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/directors")
                        .queryParam("threshold", 4)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors[0]").isEqualTo("Martin Scorsese")
                .jsonPath("$.directors[1]").isEqualTo("Woody Allen");
    }

    @Test
    void testGetDirectors_NotFound() {
        // Arrange
        when(movieInfoService.getDirectors(anyInt())).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/directors")
                        .queryParam("threshold", 10)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Test GetDirector, Expected Invalid Threshold")
    void testGetDirectors_InvalidThreshold() {
        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/directors")
                        .queryParam("threshold", 0) // Invalid threshold
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid Input")
                .jsonPath("$.message").isEqualTo("Threshold must be greater than zero");
    }

}