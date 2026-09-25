package com.iwfc.model.maintenance;

/**
 * Priority levels for equipment maintenance tickets.
 */
public enum MaintenanceUrgency {
    LOW("Low - Routine / Cosmetic"),
    MEDIUM("Medium - Minor performance degradation"),
    HIGH("High - Machine malfunctioning"),
    CRITICAL("Critical - Safety hazard / Non-operational");

    private final String description;

    MaintenanceUrgency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
