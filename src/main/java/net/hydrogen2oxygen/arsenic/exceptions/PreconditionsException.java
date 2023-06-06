package net.hydrogen2oxygen.arsenic.exceptions;

public class PreconditionsException extends RuntimeException {

    public PreconditionsException(String message) {
        super(message);
    }

    public PreconditionsException(String message, Throwable e) {
        super(message, e);
    }
}
