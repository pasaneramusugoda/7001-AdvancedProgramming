package com.iwfc.pattern.creational;

import com.iwfc.common.Constants;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.equipment.RowingMachine;
import com.iwfc.model.equipment.SpinBike;
import com.iwfc.model.equipment.Treadmill;

/**
 * Creational Design Pattern: Factory Method Pattern.
 * Encapsulates the instantiation logic for various specialized gym equipment entities,
 * ensuring proper initialization of maintenance thresholds and specific mechanical attributes.
 */
public class EquipmentFactory {

    /**
     * Factory method creating a specialized Equipment instance based on the specified type.
     *
     * @param type     the type of fitness equipment
     * @param id       unique identifier
     * @param name     equipment model/display name
     * @param location designated studio or gym zone
     * @return concrete Equipment subclass instance
     * @throws IllegalArgumentException if type is unsupported or arguments are invalid
     */
    public static Equipment createEquipment(EquipmentType type, String id, String name, String location) {
        if (type == null) {
            throw new IllegalArgumentException("Equipment type cannot be null.");
        }

        return switch (type) {
            case TREADMILL -> new Treadmill(id, name, location, Constants.DEFAULT_TREADMILL_MAINTENANCE_HOURS, 15.0);
            case SPIN_BIKE -> new SpinBike(id, name, location, Constants.DEFAULT_SPIN_BIKE_MAINTENANCE_HOURS, true);
            case ROWING_MACHINE -> new RowingMachine(id, name, location, Constants.DEFAULT_ROWING_MACHINE_MAINTENANCE_HOURS, 5);
            case ELLIPTICAL, WEIGHT_RACK ->
                // Uses generic treadmill or fallback specialization
                new Treadmill(id, name, location, 250.0, 0.0);
        };
    }
}
