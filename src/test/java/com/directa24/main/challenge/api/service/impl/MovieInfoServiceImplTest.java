package com.directa24.main.challenge.api.service.impl;

import com.directa24.main.challenge.api.repository.MovieRepository;
import com.directa24.main.challenge.mocks.MovieTestDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class MovieInfoServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    private MovieInfoServiceImpl movieInfoService;

    private static final int PROCESSING_THRESHOLD = 10;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieInfoService = new MovieInfoServiceImpl(PROCESSING_THRESHOLD, movieRepository);
    }

    @Test
    void testGetDirectorsReturnsDirectorsAboveThreshold() {

        // Call method under test
        when(movieRepository.fetchAllMovies())
                .thenReturn(Flux.just(MovieTestDataHelper.movieMock1(), MovieTestDataHelper.movieMock2(),
                        MovieTestDataHelper.movieMock3(), MovieTestDataHelper.movieMock4(),
                        MovieTestDataHelper.movieMock5(), MovieTestDataHelper.movieMock6()));

        // Call method under test
        StepVerifier.create(movieInfoService.getDirectors(2))
                .expectNextMatches(directors -> directors.contains("Director1")
                        && !directors.contains("Director2"))
                .verifyComplete();
    }

    @Test
    void testGetDirectorsEmptyResultWhenNoDirectorsAboveThreshold() {

        when(movieRepository.fetchAllMovies()).thenReturn(Flux.just(MovieTestDataHelper.movieMock1(),
                MovieTestDataHelper.movieMock2(),
                MovieTestDataHelper.movieMock3(), MovieTestDataHelper.movieMock4(),
                MovieTestDataHelper.movieMock5(), MovieTestDataHelper.movieMock6()));

        // Call method under test
        StepVerifier.create(movieInfoService.getDirectors(5))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void testGetDirectorsIgnoresMoviesWithNullDirectors() {

        when(movieRepository.fetchAllMovies()).thenReturn(Flux.just(MovieTestDataHelper.movieMock1(),
                MovieTestDataHelper.movieMockDirectorNull(),
                MovieTestDataHelper.movieMock2()));

        // Call method under test
        StepVerifier.create(movieInfoService.getDirectors(1))
                .expectNextMatches(directors -> directors.contains("Director1"))
                .verifyComplete();
    }

}