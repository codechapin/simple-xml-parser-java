package com.codechapin.sxpj;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 */
public class MovieCategory {
    private final Deque<Movie> movies;

    private String name;

    public MovieCategory() {
        movies = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void add(final Movie movie) {
        movies.add(movie);
    }

    public Movie currentMovie() {
        return movies.peekLast();
    }

    public int size() {
        return movies.size();
    }
}
