package com.iwfc.model.scheduling;

import com.iwfc.common.Identifiable;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a member reservation for a session slot.
 */
public class Booking implements Identifiable<String> {

    private final String id;
    private final String sessionId;
    private final String memberId;
    private final LocalDateTime bookingTime;
    private BookingStatus status;

    public Booking(String id, String sessionId, String memberId) {
        this(id, sessionId, memberId, LocalDateTime.now(), BookingStatus.CONFIRMED);
    }

    public Booking(String id, String sessionId, String memberId, LocalDateTime bookingTime, BookingStatus status) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Booking ID cannot be empty.");
        if (sessionId == null || sessionId.isBlank()) throw new IllegalArgumentException("Session ID cannot be empty.");
        if (memberId == null || memberId.isBlank()) throw new IllegalArgumentException("Member ID cannot be empty.");

        this.id = id.trim();
        this.sessionId = sessionId.trim();
        this.memberId = memberId.trim();
        this.bookingTime = Objects.requireNonNull(bookingTime, "Booking time cannot be null.");
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
    }

    @Override
    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Booking[%s] Session: %s | Member: %s | Time: %s | Status: %s",
                id, sessionId, memberId, bookingTime, status);
    }
}
