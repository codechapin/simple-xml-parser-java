package com.codechapin.sxpj;

import com.codechapin.sxpj.handler.ElementType;
import org.testng.annotations.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;

import static com.codechapin.sxpj.Rule.*;
import static org.testng.Assert.assertEquals;

/**
 *
 */
public class XmlParserTest {
    private final XmlParserFactory factory = new XmlParserFactory(XMLInputFactory.newFactory());

    @Test
    public void tagTest() {
        final Rule<Movies> movieRule = element("/movie",
                (type, movies, parser) -> {
                    if (type == ElementType.START) {
                        movies.add(new Movie());
                    }
                }
        );

        final Movies movies = new Movies();
        parse("<movie />", movies, movieRule);

        assertEquals(movies.size(), 1);
        assertEquals(movies.last(), new Movie());
    }

    @Test
    public void attributesTest() {
        final Rule<Movies> movieRule = element("/movie",
                (type, movies, parser) -> {
                    if (type == ElementType.START) {
                        movies.add(new Movie());
                    }
                }
        );

        final Rule<Movies> movieAttributesRule = attributes("/movie",
                (name, value, movies, parser) -> {
                    final Movie movie = movies.last();
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

        final Movies movies = new Movies();
        parse("<movie id=\"1234\" name=\"Terminator\"/>", movies, movieRule, movieAttributesRule);

        final Movie expected = new Movie();
        expected.setId("1234");
        expected.setName("Terminator");

        assertEquals(movies.size(), 1);
        assertEquals(movies.last(), expected);
    }

    @Test
    public void charactersTest() {
        final Rule<Movies> movieRule = element("/movie",
                (type, movies, parser) -> {
                    if (type == ElementType.START) {
                        movies.add(new Movie());
                    }
                }
        );

        final Rule<Movies> movieAttributesRule = attributes("/movie",
                (name, value, movies, parser) -> {
                    final Movie movie = movies.last();
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

        final Rule<Movies> movieCharactersRules = characters("/movie", (chars, movies, parser) -> {
            final Movie movie = movies.last();
            movie.setDescription(chars);
        });

        final Movies movies = new Movies();

        parse("<movie id=\"1234\" name=\"Terminator\">A scifi movie from the 80's.</movie>",
                movies,
                movieRule,
                movieAttributesRule,
                movieCharactersRules
        );

        final Movie expected = new Movie();
        expected.setId("1234");
        expected.setName("Terminator");
        expected.setDescription("A scifi movie from the 80's.");

        assertEquals(movies.size(), 1);
        assertEquals(movies.last(), expected);
    }

    @Test
    public void charactersCDATATest() {
        final Rule<Movies> movieRule = element("/movie",
                (type, movies, parser) -> {
                    if (type == ElementType.START) {
                        movies.add(new Movie());
                    }
                }
        );

        final Rule<Movies> movieAttributesRule = attributes("/movie",
                (name, value, movies, parser) -> {
                    final Movie movie = movies.last();
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

        final Rule<Movies> movieCharactersRules = characters("/movie", (chars, movies, parser) -> {
            final Movie movie = movies.last();
            movie.setDescription(chars);
        });

        final Movies movies = new Movies();

        parse("<movie id=\"1234\" name=\"Terminator\"><![CDATA[A scifi <b>movie</b> from the 80's.]]></movie>",
                movies,
                movieRule,
                movieAttributesRule,
                movieCharactersRules
        );

        final Movie expected = new Movie();
        expected.setId("1234");
        expected.setName("Terminator");
        expected.setDescription("A scifi <b>movie</b> from the 80's.");

        assertEquals(movies.size(), 1);
        assertEquals(movies.last(), expected);
    }


    @SafeVarargs
    private final <S> void parse(final String xml, final S state, final Rule<S>... rules) {
        final XmlParser<S> parser = factory.newParser(rules);
        parser.parse(new StringReader(xml), state);

    }
}
