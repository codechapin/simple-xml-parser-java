package com.codechapin.sxpj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MovieCategory {
    private final Map<String, Movie> movies;
    private Movie current;

    private String name;

    public MovieCategory() {
        movies = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setCurrentMovie(final Movie movie) {
        current = movie;
    }

    public Movie currentMovie() {
        return current;
    }

    public int size() {
        return movies.size();
    }

    public Movie getMovieByName(final String name) {
        return movies.get(name);
    }

    public void currentMovieIsDone() {
        movies.put(current.getName(), current);
    }
}
