package com.iwfc.pattern.behavioural.strategy;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.repository.Repository;

/**
 * Behavioural Design Pattern: Strategy Pattern.
 * Defines interchangeable validation algorithms applied when scheduling sessions or booking member slots.
 */
public interface BookingValidationStrategy {

    /**
     * Executes validation checks against domain rules and resource constraints.
     *
     * @param targetSession      the session being scheduled or booked
     * @param sessionRepository  repository of all sessions
     * @param equipmentRepository repository of all equipment
     * @throws InvalidBookingException if validation fails
     */
    void validate(FitnessSession targetSession,
                  Repository<FitnessSession, String> sessionRepository,
                  Repository<Equipment, String> equipmentRepository) throws InvalidBookingException;
}
