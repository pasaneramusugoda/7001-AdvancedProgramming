package com.iwfc.pattern.behavioural.strategy;

import com.iwfc.common.Constants;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.repository.Repository;

import java.time.LocalTime;

/**
 * Concrete Strategy validating that scheduled sessions strictly conform to IWFC facility operating hours.
 */
public class OperatingHoursValidationStrategy implements BookingValidationStrategy {

    @Override
    public void validate(FitnessSession targetSession,
                         Repository<FitnessSession, String> sessionRepository,
                         Repository<Equipment, String> equipmentRepository) throws InvalidBookingException {

        LocalTime startTime = targetSession.getStartTime().toLocalTime();
        LocalTime endTime = targetSession.getEndTime().toLocalTime();

        if (startTime.isBefore(Constants.FACILITY_OPENING_TIME) || endTime.isAfter(Constants.FACILITY_CLOSING_TIME)) {
            throw new InvalidBookingException(String.format(
                    "Invalid booking time: Facility operating hours are %s to %s. Session '%s' is scheduled from %s to %s.",
                    Constants.FACILITY_OPENING_TIME, Constants.FACILITY_CLOSING_TIME,
                    targetSession.getTitle(), startTime, endTime
            ));
        }

        if (!targetSession.getStartTime().toLocalDate().isEqual(targetSession.getEndTime().toLocalDate())) {
            throw new InvalidBookingException("Invalid booking: Overnight sessions spanning across multiple days are not permitted.");
        }
    }
}
