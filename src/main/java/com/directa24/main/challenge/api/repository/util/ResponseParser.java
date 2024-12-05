package com.directa24.main.challenge.api.repository.util;

import com.directa24.main.challenge.api.exception.ResponseParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

/**
 * Utility class to parse API responses into Java objects using Jackson's ObjectMapper.
 */
public class ResponseParser {

    private final ObjectMapper mapper;

    public ResponseParser(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    /**
     * Parses a JSON response string into an instance of the specified class.
     *
     * @param response The JSON response string to be parsed.
     * @param clazz    The target class type for deserialization.
     * @param <T>      The type parameter corresponding to the class type.
     * @return A Mono emitting the parsed object or an error if parsing fails.
     */
    public <T> Mono<T> parseResponse(String response, Class<T> clazz) {
        return Mono.fromCallable(() -> mapper.readValue(response, clazz))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new ResponseParsingException("Invalid response structure")))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
