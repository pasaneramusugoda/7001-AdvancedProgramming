package com.iwfc.model.equipment;

/**
 * Types of fitness equipment available at the IWFC facility.
 */
public enum EquipmentType {
    TREADMILL("Treadmill"),
    SPIN_BIKE("Spin Bike"),
    ROWING_MACHINE("Rowing Machine"),
    ELLIPTICAL("Elliptical Trainer"),
    WEIGHT_RACK("Power / Weight Rack");

    private final String displayName;

    EquipmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
