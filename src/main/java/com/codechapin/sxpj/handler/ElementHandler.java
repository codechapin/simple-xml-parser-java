package com.codechapin.sxpj.handler;

import com.codechapin.sxpj.XmlParser;

/**
 * This handler is merely called to give custom handling
 * code a chance to respond to the matching open or close tag.
 */
public interface ElementHandler<S> {
    /**
     * This is a notification-style method, no data is parsed from the
     * underlying document, the handler is merely called to give custom handling
     * code a chance to respond to the matching open or close tag.
     *
     * @param type   Used to indicate if this notification is for a {@link ElementType#START} or a
     *               {@link ElementType#END}
     * @param state  Supplied object that contains the data/state stored as handlers are called.
     * @param parser The parser executing the current XML document. Use {@link XmlParser#stop()}
     *               to stop the parsing.
     */
    void handle(final ElementType type, final S state, final XmlParser parser);
}
