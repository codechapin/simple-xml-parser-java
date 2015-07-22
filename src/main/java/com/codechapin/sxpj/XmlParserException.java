package com.codechapin.sxpj;

/**
 * Unchecked exception used to unify and report everything that can go wrong
 * during an XML parse.
 * <p/>
 * Using this helps simplify caller code by allowing them to optionally catch
 * this unchecked exception. Each exception of this type will include a detailed
 * explanation of what caused the underlying exception to occur and avoids
 * pushing up the concerns of the underlying impl to the caller.
 * <p/>
 * 90% of the time you just want to parse XML and know if it succeeded or
 * failed, this simplifies for this scenario.
 * <p/>
 * For callers that do want to know exactly what went wrong, you can use
 * {@link #getCause()} to get the source exception that this one is wrapping.
 */
public class XmlParserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception with the given message.
     *
     * @param message The explanation of why the exception was thrown.
     */
    public XmlParserException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the given message and cause.
     *
     * @param message The explanation of why the exception was thrown.
     * @param cause   The underlying exception that occurred that caused this one to
     *                be created.
     */
    public XmlParserException(String message, Exception cause) {
        super(message, cause);
    }
}
