package com.codechapin.sxpj;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 */
public class Movies {
    private final Deque<Movie> movies;

    public Movies() {
        movies = new LinkedList<>();
    }

    public void add(final Movie movie) {
        movies.add(movie);
    }

    public Movie last() {
        return movies.peekLast();
    }

    public int size() {
        return movies.size();
    }
}
