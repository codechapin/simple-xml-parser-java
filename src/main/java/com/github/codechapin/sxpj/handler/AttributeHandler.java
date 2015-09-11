package com.github.codechapin.sxpj.handler;

import com.github.codechapin.sxpj.XmlParser;

/**
 *
 */
public interface AttributeHandler<S> {
    void handle(final String name, final String value, final S state, final XmlParser parser);
}
