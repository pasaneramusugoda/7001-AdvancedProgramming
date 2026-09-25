package com.iwfc.model.equipment;

import com.iwfc.common.Constants;

/**
 * Concrete Treadmill entity featuring motor incline and belt strain tracking.
 */
public class Treadmill extends Equipment {

    private double maxInclineGrade;

    public Treadmill(String id, String name, String location) {
        super(id, name, EquipmentType.TREADMILL, location, Constants.DEFAULT_TREADMILL_MAINTENANCE_HOURS);
        this.maxInclineGrade = 15.0; // default 15% incline capability
    }

    public Treadmill(String id, String name, String location, double maintenanceThresholdHours, double maxInclineGrade) {
        super(id, name, EquipmentType.TREADMILL, location, maintenanceThresholdHours);
        this.maxInclineGrade = maxInclineGrade;
    }

    public double getMaxInclineGrade() {
        return maxInclineGrade;
    }

    public void setMaxInclineGrade(double maxInclineGrade) {
        this.maxInclineGrade = maxInclineGrade;
    }

    @Override
    public double calculateWearIndex() {
        // Treadmills suffer motor and belt wear proportional to hours with an incline stress multiplier (1.15)
        double baseWear = getCumulativeUsageHours() / getMaintenanceThresholdHours();
        return Math.min(baseWear * 1.15, 2.0);
    }
}
