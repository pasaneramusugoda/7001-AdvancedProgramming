package com.iwfc.common;

import java.time.LocalTime;

/**
 * Global system constants defining business rules and operational boundaries for IWFC.
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation of utility/constant class
    }

    // Facility Operating Hours (06:00 to 22:00)
    public static final LocalTime FACILITY_OPENING_TIME = LocalTime.of(6, 0);
    public static final LocalTime FACILITY_CLOSING_TIME = LocalTime.of(22, 0);

    // Preventative Maintenance Default Thresholds (Hours)
    public static final double DEFAULT_TREADMILL_MAINTENANCE_HOURS = 200.0;
    public static final double DEFAULT_SPIN_BIKE_MAINTENANCE_HOURS = 150.0;
    public static final double DEFAULT_ROWING_MACHINE_MAINTENANCE_HOURS = 180.0;

    // Default Session Capacity
    public static final int DEFAULT_STUDIO_CAPACITY = 20;
}
