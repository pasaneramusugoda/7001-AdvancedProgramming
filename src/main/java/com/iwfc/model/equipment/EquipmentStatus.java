package com.iwfc.model.equipment;

/**
 * Operational life-cycle statuses of fitness equipment.
 */
public enum EquipmentStatus {
    OPERATIONAL("Operational"),
    FAULTY("Faulty"),
    UNDER_MAINTENANCE("Under Maintenance"),
    DECOMMISSIONED("Decommissioned");

    private final String description;

    EquipmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailableForUse() {
        return this == OPERATIONAL;
    }
}
