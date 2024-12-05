package com.directa24.main.challenge.api.service.impl;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.repository.MovieRepository;
import com.directa24.main.challenge.api.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class MovieInfoServiceImpl implements MovieInfoService {

    private final MovieRepository movieRepository;
    private final int PROCESSING_THRESHOLD;

    public MovieInfoServiceImpl(@Value("${configuration.process.movies}") int processingThreshold,
                                MovieRepository movieRepository) {
        this.PROCESSING_THRESHOLD = processingThreshold;
        this.movieRepository = movieRepository;
    }

    /**
     * Fetches a list of distinct directors who have directed more movies than the specified threshold.
     * Results are cached for performance improvement.
     *
     * @param threshold The minimum number of movies directed by a director to be included in the result.
     * @return A Mono emitting a list of director names satisfying the threshold condition.
     */
    @Override
    @Cacheable(value = "directorsCache", key = "#threshold")
    public Mono<List<String>> getDirectors(int threshold) {
        return movieRepository
                .fetchAllMovies()
                .filter(movie -> movie.getDirector() != null)
                .window(PROCESSING_THRESHOLD) // Process movies in batches to reduce memory pressure
                .flatMap(batch -> batch
                        .groupBy(Movie::getDirector)
                        .flatMap(group -> group.count()
                                .filter(count -> count > threshold) // Log directors passing the threshold
                                .map(count -> group.key()))
                )
                .distinct() // Ensure distinct directors
                .sort() // Sort the directors alphabetically
                .collectList();
    }

}
