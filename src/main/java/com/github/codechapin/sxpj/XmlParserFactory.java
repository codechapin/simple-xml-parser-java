package com.github.codechapin.sxpj;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * It is recommended to shared an instance of this class between multiple threads.
 * The underlying {@link XMLInputFactory} is expensive to create so it is better to share it.
 */
public class XmlParserFactory {
    private final XMLInputFactory factory;

    /**
     * Uses a standard configured XMLInputFactory instance. Use other constructor if you
     * need to configure a XMLInputFactory.
     */
    public XmlParserFactory() {
        this(XMLInputFactory.newFactory());
    }

    /**
     * Use this method to passed a custom configured XMLInputFactory
     *
     * @param factory the configured XMLInputFactory to use for this instance.
     */
    public XmlParserFactory(final XMLInputFactory factory) {
        Objects.requireNonNull(factory, "The XMLInputFactory cannot be null.");
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);  // decode entities into one string
        this.factory = factory;
    }

    XMLStreamReader createXMLStreamReader(final Reader in) {
        try {
            return factory.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    XMLStreamReader createXMLStreamReader(final InputStream in, final Charset charset) {
        try {
            if (charset != null) {
                return factory.createXMLStreamReader(in, charset.name());
            } else {
                return factory.createXMLStreamReader(in);
            }
        } catch (XMLStreamException e) {
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    public <S> XmlParser<S> newParser(final Rule<S>... rules) {
        return new XmlParser<>(this, rules);
    }
}
