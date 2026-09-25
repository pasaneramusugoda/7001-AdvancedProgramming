package com.iwfc.exception;

/**
 * Thrown when attempting to register an entity with an identifier or unique attribute that already exists.
 */
public class DuplicateDataException extends IWFCException {

    public DuplicateDataException(String message) {
        super(message);
    }

    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
