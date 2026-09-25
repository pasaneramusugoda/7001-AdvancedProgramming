package com.iwfc.model.equipment;

import com.iwfc.common.Constants;

/**
 * Concrete RowingMachine entity tracking damper resistance and cable tension cycles.
 */
public class RowingMachine extends Equipment {

    private int damperSetting;

    public RowingMachine(String id, String name, String location) {
        super(id, name, EquipmentType.ROWING_MACHINE, location, Constants.DEFAULT_ROWING_MACHINE_MAINTENANCE_HOURS);
        this.damperSetting = 5; // default mid-level damper
    }

    public RowingMachine(String id, String name, String location, double maintenanceThresholdHours, int damperSetting) {
        super(id, name, EquipmentType.ROWING_MACHINE, location, maintenanceThresholdHours);
        this.damperSetting = damperSetting;
    }

    public int getDamperSetting() {
        return damperSetting;
    }

    public void setDamperSetting(int damperSetting) {
        if (damperSetting < 1 || damperSetting > 10) {
            throw new IllegalArgumentException("Damper setting must be between 1 and 10.");
        }
        this.damperSetting = damperSetting;
    }

    @Override
    public double calculateWearIndex() {
        // Cable and chain tension wear
        double baseWear = getCumulativeUsageHours() / getMaintenanceThresholdHours();
        return Math.min(baseWear * 1.10, 2.0);
    }
}
