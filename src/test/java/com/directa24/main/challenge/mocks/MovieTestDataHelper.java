package com.directa24.main.challenge.mocks;

import com.directa24.main.challenge.api.model.Movie;
import com.directa24.main.challenge.api.model.MoviesResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public class MovieTestDataHelper {

    public static Movie createMovie(String title, String year, String rated, String released,
                                    String runtime, String genre, String director, String writer, String actors) {
        return new Movie(title, year, rated, released, runtime, genre, director, writer, actors);
    }

    public static String warJsonPage1 = "{\"page\":1,\"per_page\":2,\"total\":2,\"total_pages\":1," +
            "\"data\":[" +
            "{\"Title\":\"Movie 1\",\"Year\":\"2012\",\"Rated\":\"PG-13\",\"Released\":\"10 Jun 2012\",\"Runtime\":\"10 min\",\"Genre\":\"Action\",\"Director\":\"Director A\",\"Writer\":\"Writer Za\",\"Actors\":\"Actor 1, Actor A\"}," +
            "{\"Title\":\"Movie 2\",\"Year\":\"1980\",\"Rated\":\"PG-13\",\"Released\":\"20 Apr 1980\",\"Runtime\":\"15 min\",\"Genre\":\"Sci-fy\",\"Director\":\"Director BC\",\"Writer\":\"Writer VC\",\"Actors\":\"Actor Y, Actor Z\"}]}";

    public static MoviesResponse getMockMoviesResponse() {

        List<Movie> movies = List.of(createMovie("Movie 1", "2012", "PG-13", "10 Jun 2012", "10 min", "Action", "Director A", "Writer Za", "Actor 1, Actor A"),
                createMovie("Movie 2", "1980", "PG-13", "20 Apr 1980", "15 min", "Sci-fy", "Director BC", "Writer VC", "Actor Y, Actor Z"));

        return new MoviesResponse(1, 2, 3, 2, movies);
    }

    public static Movie movieMock1() {
        return createMovie("Movie1", "1981", "PG-17", "10 apr 1981",
                "80 min", "Comedy, Fantasy", "Director1", "Writer A",
                "Actor A");
    }

    public static Movie movieMock2() {
        return createMovie("Movie2", "2010", "R", "10dic 2010",
                "12 min", "Fantasy", "Director1", "Writer C",
                "Actor C, Actor W");
    }

    public static Movie movieMock3() {
        return createMovie("Movie3", "2000", "PG", "1 nov 2000",
                "25 min", "Adventure, Scu-Fy", "Director2",
                "Writer A, Writer F", "Actor Z, Actor M");
    }

    public static Movie movieMock4() {
        return createMovie("Movie4", "1979", "R", "1 nov 1979",
                "3 hrs", "Horror", "Director3", "Writer G",
                " Actor J");
    }

    public static Movie movieMock5() {
        return createMovie("Movie5", "2005", "PG", "1 jun 2005",
                "55 min", "Action", "Director1",
                "Writer G, Writer H", "Actor G, Actor H");
    }

    public static Movie movieMock6() {
        return createMovie("Movie6", "1956", "PG-13", "29 feb 1956",
                "15 min", "Action, Sci-Fi", "Director2",
                "Writer A, Writer J", "Actor ZA, Actor MN");
    }

    public static Movie movieMockDirectorNull() {
        return createMovie("MovieC", "1990", "R", "29 feb 1990",
                "5 hrs 30 min", "Action", null,
                "Writer H, Writer G", "Actor G");
    }
}
