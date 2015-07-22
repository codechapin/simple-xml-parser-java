package com.codechapin.sxpj;

/**
 *
 */
public enum RuleType {
    /**
     * Type used to indicate a rule interested in START_TAG and END_TAG
     * events for the matching location path.
     * <p/>
     * This can be handy when no parsed data is needed from the underlying
     * XML, but rather a simple notification that the location path existed
     * in the XML (e.g. counting element occurrences).
     */
    ELEMENT,
    /**
     * Type used to indicate that this rule describes 1 or more attribute
     * values that the caller wants parsed.
     */
    ATTRIBUTE,
    /**
     * Used to describe a rule that will be called
     * <p/>
     * Type used to indicate that this rule describes the character data
     * between an open and close tag that the caller wants parsed.
     */
    CHARACTERS;
}
