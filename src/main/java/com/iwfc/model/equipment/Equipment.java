package com.iwfc.model.equipment;

import com.iwfc.common.Identifiable;

import java.util.Objects;

/**
 * Abstract base class representing physical fitness equipment at IWFC.
 * Demonstrates Abstraction, Encapsulation, and Polymorphism.
 */
public abstract class Equipment implements Identifiable<String> {

    private final String id;
    private String name;
    private final EquipmentType type;
    private EquipmentStatus status;
    private String location;
    private double cumulativeUsageHours;
    private double maintenanceThresholdHours;

    protected Equipment(String id, String name, EquipmentType type, String location, double maintenanceThresholdHours) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Equipment ID cannot be null or empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Equipment name cannot be null or empty.");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or empty.");
        }
        if (maintenanceThresholdHours <= 0) {
            throw new IllegalArgumentException("Maintenance threshold must be greater than zero.");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.type = Objects.requireNonNull(type, "Equipment type cannot be null.");
        this.location = location.trim();
        this.status = EquipmentStatus.OPERATIONAL;
        this.cumulativeUsageHours = 0.0;
        this.maintenanceThresholdHours = maintenanceThresholdHours;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        this.name = name.trim();
    }

    public EquipmentType getType() {
        return type;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be blank.");
        }
        this.location = location.trim();
    }

    public double getCumulativeUsageHours() {
        return cumulativeUsageHours;
    }

    public double getMaintenanceThresholdHours() {
        return maintenanceThresholdHours;
    }

    public void setMaintenanceThresholdHours(double maintenanceThresholdHours) {
        if (maintenanceThresholdHours <= 0) {
            throw new IllegalArgumentException("Threshold must be positive.");
        }
        this.maintenanceThresholdHours = maintenanceThresholdHours;
    }

    /**
     * Increments cumulative usage hours.
     *
     * @param hours hours of operation to add
     */
    public void logUsage(double hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("Usage hours must be positive.");
        }
        this.cumulativeUsageHours += hours;
    }

    /**
     * Checks whether cumulative hours have reached or exceeded the preventative maintenance threshold.
     *
     * @return true if maintenance is due
     */
    public boolean isMaintenanceDue() {
        return this.cumulativeUsageHours >= this.maintenanceThresholdHours;
    }

    /**
     * Resets cumulative usage hours following completed maintenance service.
     */
    public void resetMaintenanceCycle() {
        this.cumulativeUsageHours = 0.0;
        this.status = EquipmentStatus.OPERATIONAL;
    }

    /**
     * Polymorphic method to calculate a mechanical wear index (0.0 to 1.0+)
     * based on equipment-specific mechanical stress factors.
     *
     * @return wear percentage index
     */
    public abstract double calculateWearIndex();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | %s | Status: %s | Loc: %s | Usage: %.1f/%.1fh",
                type, id, name, status, location, cumulativeUsageHours, maintenanceThresholdHours);
    }
}
