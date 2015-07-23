package com.codechapin.sxpj;

/**
 *
 */
public class Movie {
    private final MovieCast cast;

    private String id;
    private String name;
    private String description;
    private String year;

    public Movie() {
        cast = new MovieCast();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public MovieCast getCast() {
        return cast;
    }
}
