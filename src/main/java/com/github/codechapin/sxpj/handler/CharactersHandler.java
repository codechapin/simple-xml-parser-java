package com.github.codechapin.sxpj.handler;

import com.github.codechapin.sxpj.XmlParser;

/**
 *
 */
public interface CharactersHandler<S> {
    void handle(final String chars, final S state, final XmlParser parser);
}
