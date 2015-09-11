package com.github.codechapin.sxpj;

import com.github.codechapin.sxpj.handler.Element;
import org.testng.annotations.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static com.github.codechapin.sxpj.Rule.*;
import static org.testng.Assert.*;

/**
 *
 */
public class XmlParserTest {
    private final XmlParserFactory factory = new XmlParserFactory(XMLInputFactory.newFactory());

    @Test
    public void elementTest() {
        final Rule<MovieCategory> movieRule = element("/movie",
                (element, category, parser) -> {
                    if (element == Element.START) {
                        category.setCurrentMovie(new Movie());
                    } else {
                        category.currentMovieIsDone();
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
                (element, category, parser) -> {
                    if (element == Element.START) {
                        category.setCurrentMovie(new Movie());
                    } else {
                        category.currentMovieIsDone();
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
                (element, category, parser) -> {
                    if (element == Element.START) {
                        category.setCurrentMovie(new Movie());
                    } else {
                        category.currentMovieIsDone();
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
                (element, category, parser) -> {
                    if (element == Element.START) {
                        category.setCurrentMovie(new Movie());
                    } else {
                        category.currentMovieIsDone();
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
                (element, db, parser) -> {
                    if (element == Element.START) {
                        db.setCurrentCategory(new MovieCategory());
                    } else {
                        db.currentIsDone();
                    }
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
                (element, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    if (element == Element.START) {
                        category.setCurrentMovie(new Movie());
                    } else {
                        category.currentMovieIsDone();
                    }
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
                (element, db, parser) -> {
                    final MovieCategory category = db.currentCategory();
                    final Movie movie = category.currentMovie();
                    final MovieCast cast = movie.getCast();

                    if (element == Element.START) {
                        cast.setCurrentActor(new MovieActor());
                    } else {
                        cast.setCurrentIsDone();
                    }

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

        //The following test cases could probably be written in a different way to avoid so much typing,
        //but I wanted to make them easier to read and debug when they break.

        final MovieCategory action = db.getCategoryByName("Action");
        assertNotNull(action);
        assertEquals(action.getName(), "Action");
        assertEquals(action.size(), 1);

        final Movie terminator = action.getMovieByName("Terminator 2");
        assertNotNull(terminator);
        assertEquals(terminator.getId(), "1234");
        assertEquals(terminator.getName(), "Terminator 2");
        assertEquals(terminator.getYear(), "1991");

        final MovieCast terminatorCast = terminator.getCast();
        assertNotNull(terminatorCast);

        final MovieActor arnold = terminatorCast.getActorByName("Arnold Schwarzenegger");
        assertNotNull(arnold);
        assertEquals(arnold.getName(), "Arnold Schwarzenegger");
        assertEquals(arnold.getCharacterName(), "The Terminator");

        final MovieActor linda = terminatorCast.getActorByName("Linda Hamilton");
        assertNotNull(linda);
        assertEquals(linda.getName(), "Linda Hamilton");
        assertEquals(linda.getCharacterName(), "Sarah Connor");

        final MovieActor ed = terminatorCast.getActorByName("Edward Furlong");
        assertNotNull(ed);
        assertEquals(ed.getName(), "Edward Furlong");
        assertEquals(ed.getCharacterName(), "John Connor");

        final MovieCategory comedy = db.getCategoryByName("Comedy");
        assertNotNull(comedy);
        assertEquals(comedy.getName(), "Comedy");
        assertEquals(comedy.size(), 1);

        final Movie tommyBoy = comedy.getMovieByName("Tommy Boy");
        assertNotNull(tommyBoy);
        assertEquals(tommyBoy.getId(), "5678");
        assertEquals(tommyBoy.getName(), "Tommy Boy");
        assertEquals(tommyBoy.getYear(), "1995");

        final MovieCast tommyBoyCast = tommyBoy.getCast();
        assertNotNull(tommyBoyCast);

        final MovieActor chris = tommyBoyCast.getActorByName("Chris Farley");
        assertNotNull(chris);
        assertEquals(chris.getName(), "Chris Farley");
        assertEquals(chris.getCharacterName(), "Tommy");

        final MovieActor david = tommyBoyCast.getActorByName("David Spade");
        assertNotNull(david);
        assertEquals(david.getName(), "David Spade");
        assertEquals(david.getCharacterName(), "Richard");

        final MovieActor brian = tommyBoyCast.getActorByName("Brian Dennehy");
        assertNotNull(brian);
        assertEquals(brian.getName(), "Brian Dennehy");
        assertEquals(brian.getCharacterName(), "Big Tom");

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
