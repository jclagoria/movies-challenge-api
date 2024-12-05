package com.directa24.main.challenge.api.repository;

import com.directa24.main.challenge.api.model.Movie;
import reactor.core.publisher.Flux;

public interface MovieRepository {

    public Flux<Movie> fetchAllMovies();

}
