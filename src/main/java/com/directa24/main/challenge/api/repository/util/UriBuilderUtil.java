package com.directa24.main.challenge.api.repository.util;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for building URIs with query parameters.
 * Designed to be a helper for constructing consistent and reusable URIs.
 */
public class UriBuilderUtil {

    private UriBuilderUtil() {}

    /**
     * Builds a URI string with the given path and page query parameter.
     *
     * @param path The base path of the URI (e.g., "/search").
     * @param page The page number to be added as a query parameter.
     * @return A URI string with the provided path and query parameter.
     */
    public static String buildSearchUri(String path, int page) {
        return UriComponentsBuilder
                .fromPath(path)
                .queryParam("page", page)
                .build().toUriString();
    }

}
