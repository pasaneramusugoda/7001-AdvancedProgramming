package com.iwfc.exception;

/**
 * Thrown when an expected entity (User, Equipment, Session, or Request) cannot be found by its identifier.
 */
public class EntityNotFoundException extends IWFCException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
