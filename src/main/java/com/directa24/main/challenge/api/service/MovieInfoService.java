package com.directa24.main.challenge.api.service;

import reactor.core.publisher.Mono;

import java.util.List;

public interface MovieInfoService {

    public Mono<List<String>> getDirectors(int threshold);

}
