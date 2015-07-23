package com.codechapin.sxpj;

import com.codechapin.sxpj.handler.ElementType;
import org.testng.annotations.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static com.codechapin.sxpj.Rule.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 *
 */
public class XmlParserTest {
    private final XmlParserFactory factory = new XmlParserFactory(XMLInputFactory.newFactory());

    @Test
    public void elementTest() {
        final Rule<MovieCategory> movieRule = element("/movie",
                (type, category, parser) -> {
                    if (type == ElementType.START) {
                        category.add(new Movie());
                    }
                }
        );

        final MovieCategory category = new MovieCategory();
        parse("<movie />", category, movieRule);

        assertEquals(category.size(), 1);

        final Movie actual = category.currentMovie();
        assertNull(actual.getId());
        assertNull(actual.getName());
    }

    @Test
    public void attributesTest() {
        final Rule<MovieCategory> movieRule = element("/movie",
                (type, category, parser) -> {
                    if (type == ElementType.START) {
                        category.add(new Movie());
                    }
                }
        );

        final Rule<MovieCategory> movieAttributesRule = attributes("/movie",
                (name, value, category, parser) -> {
                    final Movie movie = category.currentMovie();
                    switch (name) {
                        case "id":
                            movie.setId(value);
                            break;
                        case "name":
                            movie.setName(value);
                            break;

                    }

                },
                "id", "name"
        );

        final MovieCategory category = new MovieCategory();
        parse("<movie id=\"1234\" name=\"Terminator\"/>", category, movieRule, movieAttributesRule);

        assertEquals(category.size(), 1);

        final Movie actual = category.currentMovie();
        assertEquals(actual.getId(), "1234");
        assertEquals(actual.getName(), "Terminator");
    }

    @Test
    public void charactersTest() {
        final Rule<MovieCategory> movieRule = element("/movie",
                (type, category, parser) -> {
                    if (type == ElementType.START) {
                        category.add(new Movie());
                    }
                }
        );

        final Rule<MovieCategory> movieAttributesRule = attributes("/movie",
                (name, value, category, parser) -> {
                    final Movie movie = category.currentMovie();
                    switch (name) {
                        case "id":
                            movie.setId(value);
                            break;
                        case "name":
                            movie.setName(value);
                            break;

                    }

                },
                "id", "name"
        );

        final Rule<MovieCategory> movieCharactersRules = characters("/movie", (chars, category, parser) -> {
            final Movie movie = category.currentMovie();
            movie.setDescription(chars);
        });

        final MovieCategory category = new MovieCategory();

        parse("<movie id=\"1234\" name=\"Terminator\">A scifi movie from the 80's.</movie>",
                category,
                movieRule,
                movieAttributesRule,
                movieCharactersRules
        );

        assertEquals(category.size(), 1);

        final Movie actual = category.currentMovie();
        assertEquals(actual.getId(), "1234");
        assertEquals(actual.getName(), "Terminator");
        assertEquals(actual.getDescription(), "A scifi movie from the 80's.");
    }

    @Test
    public void charactersCDATATest() {
        final Rule<MovieCategory> movieRule = element("/movie",
                (type, category, parser) -> {
                    if (type == ElementType.START) {
                        category.add(new Movie());
                    }
                }
        );

        final Rule<MovieCategory> movieAttributesRule = attributes("/movie",
                (name, value, category, parser) -> {
                    final Movie movie = category.currentMovie();
                    switch (name) {
                        case "id":
                            movie.setId(value);
                            break;
                        case "name":
                            movie.setName(value);
                            break;

                    }

                },
                "id", "name"
        );

        final Rule<MovieCategory> movieCharactersRules = characters("/movie", (chars, category, parser) -> {
            final Movie movie = category.currentMovie();
            movie.setDescription(chars);
        });

        final MovieCategory category = new MovieCategory();

        parse("<movie id=\"1234\" name=\"Terminator\"><![CDATA[A scifi <b>movie</b> from the 80's.]]></movie>",
                category,
                movieRule,
                movieAttributesRule,
                movieCharactersRules
        );

        assertEquals(category.size(), 1);

        final Movie actual = category.currentMovie();
        assertEquals(actual.getId(), "1234");
        assertEquals(actual.getName(), "Terminator");
        assertEquals(actual.getDescription(), "A scifi <b>movie</b> from the 80's.");
    }

    @Test
    public void testMoviesXml() {
        final Rule<MovieDatabase> categoryRule = element("/imdb/category",
                (type, db, parser) -> {
                    if (type == ElementType.END) {
                        return;
                    }

                    db.add(new MovieCategory());
                }
        );

        final Rule<MovieDatabase> categoryAttributesRule = attributes("/imdb/category",
                (name, value, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    category.setName(value);
                },
                "name"
        );

        final Rule<MovieDatabase> movieRule = element("/imdb/category/movie",
                (type, db, parser) -> {
                    if (type == ElementType.END) {
                        return;
                    }

                    final MovieCategory category = db.currentCategory();
                    category.add(new Movie());
                }
        );

        final Rule<MovieDatabase> movieAttributesRule = attributes("/imdb/category/movie",
                (name, value, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();

                    movie.setId(value);
                },
                "id"
        );

        final Rule<MovieDatabase> movieNameRule = characters("/imdb/category/movie/name",
                (chars, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();

                    movie.setName(chars);
                }
        );

        final Rule<MovieDatabase> movieYearRule = characters("/imdb/category/movie/year",
                (chars, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();

                    movie.setYear(chars);
                }
        );

        final Rule<MovieDatabase> movieActorRule = element("/imdb/category/movie/cast/actor",
                (type, db, parser) -> {
                    if (type == ElementType.END) {
                        return;
                    }

                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();
                    final MovieCast cast = movie.getCast();
                    cast.add(new MovieActor());
                }
        );

        final Rule<MovieDatabase> movieActorAttributesRule = attributes("/imdb/category/movie/cast/actor",
                (name, value, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();
                    final MovieActor actor = movie.getCast().currentActor();

                    switch (name) {
                        case "realName":
                            actor.setName(value);
                            break;
                        case "charName":
                            actor.setCharacterName(value);
                            break;
                    }
                },
                "realName", "charName"
        );

        final MovieDatabase db = new MovieDatabase();

        parseResource("/movies.xml", db,
                categoryRule,
                categoryAttributesRule,
                movieRule,
                movieAttributesRule,
                movieNameRule,
                movieYearRule,
                movieActorRule,
                movieActorAttributesRule);

        assertEquals(db.size(), 2);

        //The following test cases could be written in a different way to avoid so much typing,
        //but I wanted to make them easier to read and debug when they break.

        int i = 0;
        int j;
        for (MovieCategory category : db.categories()) {
            assertEquals(category.size(), 1);

            final Movie movie = category.currentMovie();
            final MovieCast cast = movie.getCast();

            assertEquals(cast.size(), 3);

            switch (i) {
                case 0:
                    assertEquals(category.getName(), "Action");

                    assertEquals(movie.getId(), "1234");
                    assertEquals(movie.getName(), "Terminator 2");
                    assertEquals(movie.getYear(), "1991");

                    j = 0;
                    for (MovieActor actor : cast.actors()) {
                        switch (j) {
                            case 0:
                                assertEquals(actor.getName(), "Arnold Schwarzenegger");
                                assertEquals(actor.getCharacterName(), "The Terminator");
                                break;
                            case 1:
                                assertEquals(actor.getName(), "Linda Hamilton");
                                assertEquals(actor.getCharacterName(), "Sarah Connor");
                                break;
                            case 2:
                                assertEquals(actor.getName(), "Edward Furlong");
                                assertEquals(actor.getCharacterName(), "John Connor");
                                break;
                        }

                        j++;
                    }

                    break;
                case 1:
                    assertEquals(category.getName(), "Comedy");

                    assertEquals(movie.getId(), "5678");
                    assertEquals(movie.getName(), "Tommy Boy");
                    assertEquals(movie.getYear(), "1995");

                    j = 0;
                    for (MovieActor actor : cast.actors()) {
                        switch (j) {
                            case 0:
                                assertEquals(actor.getName(), "Chris Farley");
                                assertEquals(actor.getCharacterName(), "Tommy");
                                break;
                            case 1:
                                assertEquals(actor.getName(), "David Spade");
                                assertEquals(actor.getCharacterName(), "Richard");
                                break;
                            case 2:
                                assertEquals(actor.getName(), "Brian Dennehy");
                                assertEquals(actor.getCharacterName(), "Big Tom");
                                break;
                        }

                        j++;
                    }

                    break;
            }

            i++;
        }

    }


    @SafeVarargs
    private final <S> void parse(final String xml, final S state, final Rule<S>... rules) {
        final XmlParser<S> parser = factory.newParser(rules);
        parser.parse(new StringReader(xml), state);

    }

    @SafeVarargs
    private final <S> void parseResource(final String path, final S state, final Rule<S>... rules) {
        final URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("The path " + path + " was not found in the path: " + getClass().getResource("/"));
        }

        try {
            final XmlParser<S> parser = factory.newParser(rules);
            parser.parse(url.openStream(), state);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }


}
