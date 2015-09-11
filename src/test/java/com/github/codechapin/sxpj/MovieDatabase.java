package com.github.codechapin.sxpj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MovieDatabase {
    private final Map<String, MovieCategory> categories;
    private MovieCategory current;

    public MovieDatabase() {
        categories = new HashMap<>();
    }

    public void setCurrentCategory(final MovieCategory category) {
        current = category;
    }

    public MovieCategory currentCategory() {
        return current;
    }

    public MovieCategory getCategoryByName(final String name) {
        return categories.get(name);
    }

    public Iterable<MovieCategory> categories() {
        return categories.values();
    }

    public int size() {
        return categories.size();
    }

    public void currentIsDone() {
        categories.put(current.getName(), current);
        current = null;
    }


}
