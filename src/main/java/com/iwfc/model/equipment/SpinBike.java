package com.iwfc.model.equipment;

import com.iwfc.common.Constants;

/**
 * Concrete SpinBike entity tracking flywheel magnetic resistance and pedal crank stress.
 */
public class SpinBike extends Equipment {

    private boolean magneticResistanceEnabled;

    public SpinBike(String id, String name, String location) {
        super(id, name, EquipmentType.SPIN_BIKE, location, Constants.DEFAULT_SPIN_BIKE_MAINTENANCE_HOURS);
        this.magneticResistanceEnabled = true;
    }

    public SpinBike(String id, String name, String location, double maintenanceThresholdHours, boolean magneticResistanceEnabled) {
        super(id, name, EquipmentType.SPIN_BIKE, location, maintenanceThresholdHours);
        this.magneticResistanceEnabled = magneticResistanceEnabled;
    }

    public boolean isMagneticResistanceEnabled() {
        return magneticResistanceEnabled;
    }

    public void setMagneticResistanceEnabled(boolean magneticResistanceEnabled) {
        this.magneticResistanceEnabled = magneticResistanceEnabled;
    }

    @Override
    public double calculateWearIndex() {
        // High-cadence spin bike wear calculation
        double baseWear = getCumulativeUsageHours() / getMaintenanceThresholdHours();
        return Math.min(baseWear * 1.05, 2.0);
    }
}
