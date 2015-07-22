package com.codechapin.sxpj.handler;

import com.codechapin.sxpj.XmlParser;

/**
 *
 */
public interface CharactersHandler<S> {
    void handle(final String chars, final S state, final XmlParser parser);
}
