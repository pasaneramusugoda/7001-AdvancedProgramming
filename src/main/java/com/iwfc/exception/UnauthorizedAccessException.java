package com.iwfc.exception;

/**
 * Thrown when an actor attempts an action without requisite RBAC privileges
 * (e.g., Member attempting to modify inventory or assign maintenance tasks).
 */
public class UnauthorizedAccessException extends IWFCException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
