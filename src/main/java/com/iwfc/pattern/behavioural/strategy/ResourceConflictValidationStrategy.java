package com.iwfc.pattern.behavioural.strategy;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentStatus;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Concrete Strategy strictly validating double-booking rules for studio rooms and specialized equipment.
 */
public class ResourceConflictValidationStrategy implements BookingValidationStrategy {

    @Override
    public void validate(FitnessSession targetSession,
                         Repository<FitnessSession, String> sessionRepository,
                         Repository<Equipment, String> equipmentRepository) throws InvalidBookingException {

        List<FitnessSession> existingSessions = sessionRepository.findAll();

        for (FitnessSession existing : existingSessions) {
            // Skip checking against itself
            if (existing.getId().equals(targetSession.getId())) {
                continue;
            }

            // Check if time windows overlap
            if (existing.overlapsWith(targetSession.getStartTime(), targetSession.getEndTime())) {
                // Check 1: Studio Space Conflict
                if (existing.getStudioLocation().equalsIgnoreCase(targetSession.getStudioLocation())) {
                    throw new InvalidBookingException(String.format(
                            "Studio double-booking conflict: Studio '%s' is already booked for session '%s' (%s to %s).",
                            targetSession.getStudioLocation(), existing.getTitle(),
                            existing.getStartTime(), existing.getEndTime()
                    ));
                }

                // Check 2: Equipment Overlap Conflict
                for (String equipId : targetSession.getRequiredEquipmentIds()) {
                    if (existing.getRequiredEquipmentIds().contains(equipId)) {
                        throw new InvalidBookingException(String.format(
                                "Equipment double-booking conflict: Equipment ID '%s' is already allocated to session '%s' at this time.",
                                equipId, existing.getTitle()
                        ));
                    }
                }
            }
        }

        // Check 3: Equipment Operational Status
        for (String equipId : targetSession.getRequiredEquipmentIds()) {
            Optional<Equipment> equipOpt = equipmentRepository.findById(equipId);
            if (equipOpt.isEmpty()) {
                throw new InvalidBookingException(
                        String.format("Invalid booking: Required equipment with ID '%s' does not exist in inventory.", equipId));
            }
            Equipment equipment = equipOpt.get();
            if (equipment.getStatus() != EquipmentStatus.OPERATIONAL) {
                throw new InvalidBookingException(String.format(
                        "Equipment unavailable: Equipment '%s' (ID: %s) is currently %s and cannot be scheduled.",
                        equipment.getName(), equipment.getId(), equipment.getStatus().getDescription()
                ));
            }
        }
    }
}
