package com.codechapin.sxpj;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 */
public class MovieDatabase {
    private final Deque<MovieCategory> categories;

    public MovieDatabase() {
        categories = new LinkedList<>();
    }

    public void add(final MovieCategory category) {
        categories.add(category);
    }

    public MovieCategory currentCategory() {
        return categories.getLast();
    }

    public Iterable<MovieCategory> categories() {
        return categories;
    }

    public int size() {
        return categories.size();
    }


}
