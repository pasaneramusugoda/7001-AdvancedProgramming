package com.iwfc.pattern.behavioural.observer;

import com.iwfc.model.user.UserRole;

import java.time.LocalDateTime;

/**
 * Event object passed to observers when system alerts occur.
 */
public class NotificationEvent {

    public enum EventType {
        MAINTENANCE_STATUS_CHANGED,
        PREVENTATIVE_MAINTENANCE_DUE,
        SESSION_BOOKING_CONFIRMED,
        SESSION_CANCELLED,
        EQUIPMENT_FAULT_REPORTED
    }

    private final EventType eventType;
    private final String title;
    private final String message;
    private final UserRole targetRole;
    private final LocalDateTime timestamp;

    public NotificationEvent(EventType eventType, String title, String message, UserRole targetRole) {
        this.eventType = eventType;
        this.title = title;
        this.message = message;
        this.targetRole = targetRole;
        this.timestamp = LocalDateTime.now();
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public UserRole getTargetRole() {
        return targetRole;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Target: %s | %s",
                timestamp, title, targetRole != null ? targetRole : "ALL", message);
    }
}
