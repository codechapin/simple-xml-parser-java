package com.codechapin.sxpj;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 */
public class MovieCast {
    private final Deque<MovieActor> actors;

    public MovieCast() {
        actors = new LinkedList<>();
    }

    public void add(final MovieActor actor) {
        actors.add(actor);
    }

    public MovieActor currentActor() {
        return actors.peekLast();
    }

    public int size() {
        return actors.size();
    }

    public Iterable<MovieActor> actors() {
        return actors;
    }
}
