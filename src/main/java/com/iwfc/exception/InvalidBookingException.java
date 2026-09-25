package com.iwfc.exception;

/**
 * Thrown when session scheduling or booking violates domain rules
 * (e.g., double-booking, scheduling outside operating hours, capacity reached).
 */
public class InvalidBookingException extends IWFCException {

    public InvalidBookingException(String message) {
        super(message);
    }

    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
