package com.codechapin.sxpj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MovieCast {
    private final Map<String, MovieActor> actors;

    private MovieActor current;

    public MovieCast() {
        actors = new HashMap<>();
    }

    public void setCurrentActor(final MovieActor actor) {
        current = actor;
    }

    public MovieActor currentActor() {
        return current;
    }

    public int size() {
        return actors.size();
    }

    public Iterable<MovieActor> actors() {
        return actors.values();
    }

    public void setCurrentIsDone() {
        actors.put(current.getName(), current);
        current = null;
    }

    public MovieActor getActorByName(final String name) {
        return actors.get(name);
    }
}
