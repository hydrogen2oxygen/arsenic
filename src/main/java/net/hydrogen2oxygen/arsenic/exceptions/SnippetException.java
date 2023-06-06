package net.hydrogen2oxygen.arsenic.exceptions;

public class SnippetException extends RuntimeException {

    public SnippetException(String message) {
        super(message);
    }

    public SnippetException(String message, Throwable e) {
        super(message, e);
    }
}
