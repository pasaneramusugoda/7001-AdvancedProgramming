package com.iwfc.model.maintenance;

/**
 * Lifecycle states of an IWFC maintenance workflow request.
 */
public enum MaintenanceStatus {
    PENDING("Pending Initial Review"),
    ASSIGNED("Assigned for Servicing"),
    COMPLETED("Service Completed & Operational"),
    CANCELLED("Request Cancelled");

    private final String display;

    MaintenanceStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
