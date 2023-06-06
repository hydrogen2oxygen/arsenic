package net.hydrogen2oxygen.arsenic.exceptions;

public class CleanUpException extends RuntimeException {


    public CleanUpException(String message) {
        super(message);
    }

    public CleanUpException(String message, Throwable e) {
        super(message, e);
    }
}
