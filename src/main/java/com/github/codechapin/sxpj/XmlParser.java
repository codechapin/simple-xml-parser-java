package com.github.codechapin.sxpj;

import com.github.codechapin.sxpj.handler.Element;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 */
public class XmlParser<S> {
    private final Map<Integer, List<Rule<S>>> tagRules;
    private final Map<Integer, List<Rule<S>>> attrRules;
    private final Map<Integer, List<Rule<S>>> charRules;

    private final Location location;
    private final XmlParserFactory factory;

    private boolean continueParsing;

    XmlParser(final XmlParserFactory factory, final Rule<S>... rules) {
        Objects.requireNonNull(factory, "The XmlParserFactory cannot be null");
        if (rules == null || rules.length == 0) {
            throw new IllegalArgumentException(
                    "rules cannot be null or empty, you must provide at least 1 rule to execute otherwise parsing will do nothing.");
        }

        // calculate a rough optimal size for the rule maps
        final int optSize = (rules.length > 64 ? rules.length * 2 : 64);

        tagRules = new HashMap<>(optSize);
        attrRules = new HashMap<>(optSize);
        charRules = new HashMap<>(optSize);

        location = new Location();

        this.factory = factory;

        initRules(rules);
    }

    /**
     * <p>
     * Parse the XML out of the given input (producing content matching the
     * given charset) matching the handlers provided with the
     * different register methods.
     * </p>
     * <p>
     * This class will make no attempt at closing the given {@link Reader},
     * the caller must take care to clean up that resource.
     * </p>
     * <h3>Stopping Parsing</h3>
     * <p>
     * Parsing can be safely stopped by calling {@link #stop()}. This allows
     * handlers control over stopping parsing, for example,
     * if an arbitrary threshold is hit. A followup call to any of the
     * <code>parse</code> methods will reset the stopped state.
     *</p>
     * @param in    the XML content to be read.
     * @param state object that stores data constructed by the handlers.
     */
    public void parse(final Reader in, final S state) {
        Objects.requireNonNull(in, "The Reader cannot be null for XmlParser.parse");

        try {
            doParse(factory.createXMLStreamReader(in), state);
        } catch (XMLStreamException e) {
            throw new XmlParserException(e.getMessage(), e);
        }
    }

    /**
     * <p>
     * Parse the XML out of the given input (producing content matching the
     * given charset) matching the handlers provided with the
     * different register methods.
     * </p>
     * <p>
     * This class will make no attempt at closing the given {@link InputStream},
     * the caller must take care to clean up that resource.
     * </p>
     * <h3>Stopping Parsing</h3>
     * <p>
     * Parsing can be safely stopped by calling {@link #stop()}. This allows
     * handlers control over stopping parsing, for example,
     * if an arbitrary threshold is hit. A followup call to any of the
     * <code>parse</code> methods will reset the stopped state.
     * </p>
     *
     * @param in    the XML content to be read.
     * @param state object that stores data constructed by the handlers.
     */
    public void parse(final InputStream in, final S state) {
        parse(in, null, state);
    }

    /**
     * <p>
     * Parse the XML out of the given input (producing content matching the
     * given charset) matching the handlers provided with the
     * different register methods.
     * </p>
     * <p>
     * This class will make no attempt at closing the given {@link InputStream},
     * the caller must take care to clean up that resource.
     * </p>
     * <h3>Stopping Parsing</h3>
     * <p>
     * Parsing can be safely stopped by calling {@link #stop()}. This allows
     * handlers control over stopping parsing, for example,
     * if an arbitrary threshold is hit. A followup call to any of the
     * <code>parse</code> methods will reset the stopped state.
     * </p>
     *
     * @param in      the XML content to be read.
     * @param charset the character encoding of the given XML. If not known pass null or use
     *                {@link XmlParser#parse(InputStream, Object)} to let the underlying
     *                {@link XMLInputFactory} auto-detect the encoding.
     * @param state   object that stores data constructed by the handlers.
     */
    public void parse(final InputStream in, final Charset charset, final S state) {
        Objects.requireNonNull(in, "The InputStream cannot be null for XmlParser.parse");

        try {
            doParse(factory.createXMLStreamReader(in, charset), state);
        } catch (XMLStreamException e) {
            throw new XmlParserException(e.getMessage(), e);
        }
    }


    public void stop() {
        continueParsing = false;
    }

    private void initRules(final Rule<S>... rules) {

        for (Rule<S> rule : rules) {
            final int hash = rule.getPath().hashCode();
            List<Rule<S>> list;
            switch (rule.getType()) {
                case ELEMENT:
                    list = tagRules.get(hash);
                    if (list == null) {
                        list = new ArrayList<>(3);
                        tagRules.put(hash, list);
                    }
                    if (rule.getElementHandler() == null) {
                        throw new IllegalStateException(String.format("The TagHandler for Rule '%s' is null.", rule));
                    }
                    break;
                case ATTRIBUTE:
                    list = attrRules.get(hash);
                    if (list == null) {
                        list = new ArrayList<>(3);
                        attrRules.put(hash, list);
                    }
                    if (rule.getAttributeHandler() == null) {
                        throw new IllegalStateException(String.format("The AttributeHandler for Rule '%s' is null.", rule));
                    }
                    break;
                case CHARACTERS:
                    list = charRules.get(hash);
                    if (list == null) {
                        list = new ArrayList<>(3);
                        charRules.put(hash, list);
                    }
                    if (rule.getCharactersHandler() == null) {
                        throw new IllegalStateException(String.format("The CharactersHandler for Rule '%s' is null.", rule));
                    }
                    break;
                default:
                    throw new IllegalStateException(String.format("The RuleType '%s' is not recognized.", rule.getType()));
            }

            list.add(rule);
        }
    }

    private void doParse(final XMLStreamReader reader, final S state) throws XMLStreamException {
        location.clear();
        continueParsing = true;

        while (continueParsing) {
            switch (reader.next()) {
                case XMLEvent.START_ELEMENT:
                    doStartElement(reader, state);
                    break;
                case XMLEvent.CHARACTERS:
                    doCharacters(reader, state);
                    break;
                case XMLEvent.END_ELEMENT:
                    doEndElement(state);
                    break;
                case XMLEvent.END_DOCUMENT:
                    continueParsing = false;
                    break;
            }
        }
    }


    private void doStartElement(final XMLStreamReader reader, final S state) {

        location.push(reader.getLocalName(), reader.getNamespaceURI());
        final int hash = location.getCachedHashCode();

        final List<Rule<S>> tagRuleList = tagRules.get(hash);
        if (tagRuleList == null || tagRuleList.isEmpty()) {
            return;
        }

        for (Rule<S> rule : tagRuleList) {
            rule.getElementHandler().handle(Element.START, state, this);
        }

        final List<Rule<S>> attrRuleList = attrRules.get(hash);
        if (attrRuleList == null || attrRuleList.isEmpty()) {
            return;
        }

        for (Rule<S> rule : attrRuleList) {
            final String[] attrNames = rule.getAttributeNames();
            // Be safe, jump to the next rule if this one has no name entries
            if (attrNames == null || attrNames.length == 0) {
                continue;
            }

            	/*
                 * PERFORMANCE: Generating the substrings is the fastest way to
				 * parse out the matching rules as it shares the same underlying
				 * char[] used to represent the entire location path or
				 * attribute name and just creates a new simple String instance
				 * with modified index/offset values that is GC'ed quickly and
				 * easily (uses a special package-protected String constructor).
				 *
				 * Using regexp to match, splitting the rule or just about any
				 * other approach would have been magnitudes more expensive both
				 * in memory and CPU requirements than doing a simple substring.
				 */
            for (int j = 0; j < attrNames.length; j++) {
                String attrName = attrNames[j];
                String localName;
                String namespaceURI = null;

                // Parse the namespaceURI out of the name if necessary
                if (attrName.charAt(0) == '[') {
                    final int endIndex = attrName.indexOf(']');

						/*
                         * Make sure the rule is valid so we avoid out of bounds
						 * and keep the caller informed when their rules are
						 * busted by failing fast.
						 */
                    if (endIndex <= 2) {
                        throw new XmlParserException(
                                "namespace URI for rule looks to be incomplete or empty for Rule: "
                                        + rule);
                    }

                    namespaceURI = attrName.substring(1, endIndex);
                }

                final int startIndex = (namespaceURI == null ? 0 : namespaceURI.length() + 2);

                /*
                 * Make sure the rule is valid so we avoid out of bounds and
                 * keep the caller informed when their rules are busted by
                 * failing fast.
                 */
                if (attrName.length() - startIndex <= 1) {
                    throw new XmlParserException(
                            "local name for rule looks to be missing for Rule: "
                                    + rule);
                }

                // Parse the local name
                localName = attrName.substring(startIndex, attrName.length());

                // Give the parsed attribute value to the matching rule
                rule.getAttributeHandler().handle(localName,
                        reader.getAttributeValue(namespaceURI, localName),
                        state,
                        this);
            }
        }

    }

    private void doCharacters(final XMLStreamReader reader, final S state) {
        final List<Rule<S>> rules = charRules.get(location.getCachedHashCode());

        // If there are no rules for the current path, then we are done.
        if (rules == null || rules.isEmpty()) {
            return;
        }

        final String chars = reader.getText().trim();

        for (Rule<S> rule : rules) {
            rule.getCharactersHandler().handle(chars, state, this);
        }
    }

    private void doEndElement(final S state) {
        final List<Rule<S>> rules = tagRules.get(location.getCachedHashCode());
        if (rules != null && !rules.isEmpty()) {
            for (Rule<S> rule : rules) {
                rule.getElementHandler().handle(Element.END, state, this);
            }
        }

        location.pop();
    }


    /**
     * Simple and fast class used to mock the behavior of a stack in the form of
     * a string for the purposes of "pushing" and "popping" the parser's current
     * location within an XML document as it processes START and END_TAG events.
     * <p/>
     * Performance is optimized by using a {@link StringBuilder} who's length is
     * chopped (which just adjusts an <code>int</code> value) to simulate a
     * "pop" off the top.
     * <h3>Performance</h3>
     * Instead of String object creation and char[] duplication (e.g.
     * {@link System#arraycopy(Object, int, Object, int, int)}) this uses a simple
     * integer hash codes.
     * <p/>
     * The performance improvement is huge over a toString-based
     * method of matching <code>path</code>s against the
     * parser's current location.
     */
    private class Location {
        private static final int HASH_CODE_CACHE_SIZE = 512;

        private int hashCode;
        private Integer[] hashCodeCache;

        private StringBuilder path;
        private List<Integer> lengthList;

        /**
         * Creates a new empty location.
         */
        public Location() {
            hashCode = 0;
            hashCodeCache = new Integer[HASH_CODE_CACHE_SIZE];

            path = new StringBuilder(256);
            lengthList = new ArrayList<>(16);
        }

        /**
         * Overridden to calculate the hash code of this location using the
         * exact same hash code calculation that {@link String#hashCode()} uses.
         * This allows us to say a <code>String</code> with the content
         * "/library/book/title" is equal to an instance of this class
         * representing the same location when doing lookups in a {@link Map}.
         * <br />
         * This method calculates the hash code and then caches it, followup
         * calls to {@link #push(String, String)} or {@link #pop()} invalidate
         * the cached hash code allowing it to be recalculated again on the next
         * call.
         */
        @Override
        public int hashCode() {
            /*
			 * If the hash code is already 0 and our path is empty, there is
			 * nothing to compute so the hash code stays 0. Otherwise we drop
			 * into the for-loop and calculate the String-equivalent hash code.
			 */
            if (hashCode == 0 && path.length() > 0) {
                for (int i = 0, length = path.length(); i < length; i++) {
                    hashCode = 31 * hashCode + path.charAt(i);
                }
            }

            return hashCode;
        }

        /**
         * Used to get a cached {@link Integer} version of the <code>int</code>
         * {@link #hashCode()} return value.
         * <br />
         * To avoid unnecessary {@link Integer} allocations, this method caches
         * up to a certain number of {@link Integer} instances, re-using them
         * every time the same hash code value comes back up and creating new
         * instances when it doesn't.
         * <br />
         * If a larger number of {@link Integer} instances are created than the
         * underlying cache can hold, then a new instance will be created and
         * returned like normal.
         * <h3>Design</h3>
         * The reason this works so well for parsing XML is because of the
         * nested, tag-matching structure of XML. When considering unique paths
         * inside of an XML doc (e.g. "/library", "/library/book", etc.) there
         * are typically not that many; maybe 20, 50 or less than a 100 in most
         * cases.
         * <br />
         * Once the hash code {@link Integer} values for these unique paths is
         * created and cached, once we re-encounter that path again and again,
         * we don't need to recreate that hash code {@link Integer}, we can just
         * use the one from the previous occurrence.
         *
         * @return a cached {@link Integer} version of the <code>int</code>
         * {@link #hashCode()} return value.
         */
        public Integer getCachedHashCode() {
            // Recalculate the hash code
            hashCode();

            // Figure out the index, in our cache, where this value WOULD be.
            int index = hashCode % hashCodeCache.length;

            // Absolute value only
            if (index < 0) {
                index = -index;
            }

            // Get the Integer we think represents our value.
            Integer value = hashCodeCache[index];

            // If we haven't created an Integer for this value yet, do it now.
            if (value == null) {
                hashCodeCache[index] = (value = hashCode);
            } else if (hashCode != value) {
                /*
                 * If a collision has occurred and we have filled up our cache
                 * already and the Integer we grabbed doesn't represent our int
                 * value, forget the cache and just create a new Integer the old
                 * fashion way and return it.
                 *
                 * The hope is that the cache is always large enough that we only
                 * ever hit it and have no misses like this.
                 */
                value = hashCode;
            }

            return value;
        }

        /**
         * Used to clear all the internal state of the location.
         */
        public void clear() {
            hashCode = 0;
            hashCodeCache = new Integer[HASH_CODE_CACHE_SIZE];

            path.setLength(0);
            lengthList.clear();
        }

        /**
         * "Pushes" a new local name and optional namespace URI onto the "stack"
         * by appending it to the current location path that represents the
         * parser's location inside of the XML doc.
         *
         * @param localName    The local name of the tag (e.g. "title").
         * @param namespaceURI Optionally, the full qualifying namespace URI for this
         *                     tag.
         */
        public void push(String localName, String namespaceURI) {
            // Clear the hash code cache first to be safe.
            hashCode = 0;

            // Remember the length before we inserted this last entry
            lengthList.add(path.length());

            // Add separator
            path.append('/');

            // Add the namespace URI if there is one.
            if (namespaceURI != null && namespaceURI.length() > 0) {
                path.append('[').append(namespaceURI).append(']');
            }

            // Append the local name
            path.append(localName);
        }

        /**
         * "Pops" the last pushed path element off the "stack" by re-adjusting
         * the {@link StringBuilder}'s length to what it was before the last
         * element was appended.
         * <br />
         * This effectively chops the last element off the path without doing a
         * more costly {@link StringBuilder#delete(int, int)} operation that
         * would incur a call to
         * {@link System#arraycopy(Object, int, Object, int, int)} by simply
         * adjusting a single <code>int</code> counter inside of
         * {@link StringBuilder}.
         */
        public void pop() {
            // Clear the hash code cache first to be safe.
            hashCode = 0;

            // Get the length before the last insertion
            Integer lastLength = lengthList.remove(lengthList.size() - 1);

            // 'Pop' the last insertion by cropping the length to exclude it.
            path.setLength(lastLength);
        }
    }
}
