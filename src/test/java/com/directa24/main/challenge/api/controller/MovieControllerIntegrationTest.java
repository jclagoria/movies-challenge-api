package com.directa24.main.challenge.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MovieControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetDirectorsWithRealApi() {
        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/movies/directors")
                        .queryParam("threshold", 2)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors[0]").isEqualTo("Clint Eastwood")
                .jsonPath("$.directors[1]").isEqualTo("M. Night Shyamalan")
                .jsonPath("$.directors[2]").isEqualTo("Martin Scorsese")
                .jsonPath("$.directors[3]").isEqualTo("Pedro Almodóvar")
                .jsonPath("$.directors[4]").isEqualTo("Woody Allen");
    }

}
