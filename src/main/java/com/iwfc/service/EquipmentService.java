package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentStatus;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.user.UserRole;
import com.iwfc.pattern.behavioural.observer.NotificationEvent;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing equipment inventory, lifecycle tracking, usage accumulation,
 * and preventative maintenance alert triggers.
 */
public class EquipmentService {

    private final Repository<Equipment, String> equipmentRepository;
    private final NotificationSubject notificationSubject;

    public EquipmentService(Repository<Equipment, String> equipmentRepository,
                            NotificationSubject notificationSubject) {
        this.equipmentRepository = equipmentRepository;
        this.notificationSubject = notificationSubject;
    }

    /**
     * Registers a new equipment item using the Factory pattern.
     *
     * @param type     equipment type
     * @param id       unique identifier
     * @param name     model name
     * @param location designated location/room
     * @return persisted Equipment
     * @throws DuplicateDataException if ID already exists
     */
    public Equipment registerEquipment(EquipmentType type, String id, String name, String location)
            throws DuplicateDataException {
        Equipment equipment = EquipmentFactory.createEquipment(type, id, name, location);
        return equipmentRepository.save(equipment);
    }

    /**
     * Updates editable properties of an equipment unit.
     *
     * @param id          equipment ID
     * @param newName     new name
     * @param newLocation new location
     * @return updated Equipment
     * @throws EntityNotFoundException if equipment not found
     */
    public Equipment updateEquipment(String id, String newName, String newLocation)
            throws EntityNotFoundException {
        Equipment equipment = getEquipmentById(id);
        if (newName != null && !newName.isBlank()) equipment.setName(newName);
        if (newLocation != null && !newLocation.isBlank()) equipment.setLocation(newLocation);
        return equipmentRepository.update(equipment);
    }

    /**
     * Deactivates / decommissions an equipment item.
     *
     * @param id equipment ID
     * @return updated Equipment with DECOMMISSIONED status
     * @throws EntityNotFoundException if equipment not found
     */
    public Equipment deactivateEquipment(String id) throws EntityNotFoundException {
        Equipment equipment = getEquipmentById(id);
        equipment.setStatus(EquipmentStatus.DECOMMISSIONED);
        return equipmentRepository.update(equipment);
    }

    /**
     * Logs cumulative usage hours on an equipment unit and triggers preventative alerts if threshold reached.
     *
     * @param id    equipment ID
     * @param hours operating hours to accumulate
     * @return updated Equipment
     * @throws EntityNotFoundException if equipment not found
     */
    public Equipment logUsage(String id, double hours) throws EntityNotFoundException {
        Equipment equipment = getEquipmentById(id);
        equipment.logUsage(hours);

        // Check if preventative maintenance is triggered
        if (equipment.isMaintenanceDue()) {
            NotificationEvent alert = new NotificationEvent(
                    NotificationEvent.EventType.PREVENTATIVE_MAINTENANCE_DUE,
                    "Preventative Maintenance Due",
                    String.format("Equipment '%s' (ID: %s, Loc: %s) has reached %.1f hours (Threshold: %.1fh). Preventative service required.",
                            equipment.getName(), equipment.getId(), equipment.getLocation(),
                            equipment.getCumulativeUsageHours(), equipment.getMaintenanceThresholdHours()),
                    UserRole.ADMIN
            );
            notificationSubject.notifyObservers(alert);
        }

        return equipmentRepository.update(equipment);
    }

    public Equipment getEquipmentById(String id) throws EntityNotFoundException {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Equipment with ID '%s' was not found in inventory.", id)));
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public List<Equipment> getEquipmentDueForMaintenance() {
        return equipmentRepository.findAll().stream()
                .filter(Equipment::isMaintenanceDue)
                .collect(Collectors.toList());
    }

    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.findAll().stream()
                .filter(e -> e.getStatus() == EquipmentStatus.OPERATIONAL)
                .collect(Collectors.toList());
    }
}
