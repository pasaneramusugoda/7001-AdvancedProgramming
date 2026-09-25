package com.iwfc.exception;

/**
 * Base checked exception for the IWFC management system.
 * Enforces structured exception handling across business workflows (LO3).
 */
public class IWFCException extends Exception {

    public IWFCException(String message) {
        super(message);
    }

    public IWFCException(String message, Throwable cause) {
        super(message, cause);
    }
}
