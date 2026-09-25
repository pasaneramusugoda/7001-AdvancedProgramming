package com.iwfc.model.scheduling;

/**
 * Status of a member's reservation for a fitness session.
 */
public enum BookingStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
