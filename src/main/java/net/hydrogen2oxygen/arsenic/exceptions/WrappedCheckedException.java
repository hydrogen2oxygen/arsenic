package net.hydrogen2oxygen.arsenic.exceptions;

public class WrappedCheckedException extends RuntimeException {

    public WrappedCheckedException(Exception exception) {
        super(exception);
    }

    public WrappedCheckedException(String message, Exception exception) {
        super(message, exception);
    }
}
