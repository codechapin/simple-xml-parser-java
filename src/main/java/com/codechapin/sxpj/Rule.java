package com.codechapin.sxpj;

import com.codechapin.sxpj.handler.AttributeHandler;
import com.codechapin.sxpj.handler.CharactersHandler;
import com.codechapin.sxpj.handler.ElementHandler;

import java.util.Objects;

/**
 *
 */
public class Rule<S> {
    private final RuleType type;
    private final String path;

    private ElementHandler<S> elementHandler;
    private AttributeHandler<S> attributeHandler;
    private CharactersHandler<S> charactersHandler;

    private String[] attributeNames;

    private Rule(final RuleType type, final String path) {
        Objects.requireNonNull(type, "the RuleType cannot be null");
        Objects.requireNonNull(path, "the path to match this rule cannot be null");

        if (path.length() == 0) {
            throw new IllegalArgumentException("the path to match this rule cannot be empty");
        }

        /*
         * Pedantic, while we could remove a single trailing slash easily
		 * enough, there is the very-small-chance the users has multiple
		 * trailing slashes... again easy to remove, but at this point they are
		 * being really sloppy and we are letting it slide. Instead, fire an
		 * exception up-front and teach people how the API behaves immediately
		 * and what is required. Makes everyone's lives easier.
		 */
        if (path.charAt(path.length() - 1) == '/') {
            throw new IllegalArgumentException("path cannot end in a trailing slash (/), please remove it.");
        }

        this.type = type;
        this.path = path;
    }

    private Rule(final String path, final ElementHandler<S> handler) {
        this(RuleType.ELEMENT, path);

        Objects.requireNonNull(handler, "the TagHandler cannot be null.");

        this.elementHandler = handler;
    }

    private Rule(final String path, final AttributeHandler<S> handler, String... attributeNames) {
        this(RuleType.ATTRIBUTE, path);

        Objects.requireNonNull(handler, "The AttributeHandler cannot be null.");

        if (attributeNames == null || attributeNames.length == 0) {
            throw new IllegalArgumentException(
                    "setting an AttributeHandler but attributeNames was null or empty. One or more attribute names must be provided for this rule if it is going to match any attribute values.");
        }

        this.attributeNames = attributeNames;
        this.attributeHandler = handler;
    }

    private Rule(final String path, final CharactersHandler<S> handler) {
        this(RuleType.CHARACTERS, path);

        Objects.requireNonNull(handler, "The CharactersHandler cannot be null.");

        this.charactersHandler = handler;
    }

    public static <S> Rule<S> element(final String path, final ElementHandler<S> handler) {
        return new Rule<>(path, handler);
    }

    public static <S> Rule<S> characters(final String path, final CharactersHandler<S> handler) {
        return new Rule<>(path, handler);
    }

    public static <S> Rule<S> attributes(final String path, final AttributeHandler<S> handler, String... names) {
        return new Rule<>(path, handler, names);
    }

    public RuleType getType() {
        return type;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    public String getPath() {
        return path;
    }

    public ElementHandler<S> getElementHandler() {
        return elementHandler;
    }

    public AttributeHandler<S> getAttributeHandler() {
        return attributeHandler;
    }

    public CharactersHandler<S> getCharactersHandler() {
        return charactersHandler;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "path='" + path + '\'' +
                ", type=" + type +
                '}';
    }
}
