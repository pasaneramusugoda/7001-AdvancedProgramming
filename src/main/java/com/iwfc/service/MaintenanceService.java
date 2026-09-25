package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentStatus;
import com.iwfc.model.maintenance.MaintenanceRequest;
import com.iwfc.model.maintenance.MaintenanceStatus;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.model.user.UserRole;
import com.iwfc.pattern.behavioural.observer.NotificationEvent;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service managing equipment fault reporting, task delegation, status lifecycle transitions,
 * and automated observer notification broadcasts.
 */
public class MaintenanceService {

    private final Repository<MaintenanceRequest, String> maintenanceRepository;
    private final Repository<Equipment, String> equipmentRepository;
    private final NotificationSubject notificationSubject;

    public MaintenanceService(Repository<MaintenanceRequest, String> maintenanceRepository,
                              Repository<Equipment, String> equipmentRepository,
                              NotificationSubject notificationSubject) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationSubject = notificationSubject;
    }

    /**
     * Reports an equipment fault, marking equipment as FAULTY and broadcasting an alert to administrators.
     *
     * @param equipmentId  ID of malfunctioning equipment
     * @param instructorId ID of reporting instructor
     * @param description  fault description
     * @param urgency      priority level
     * @return created MaintenanceRequest
     * @throws EntityNotFoundException if equipment does not exist
     * @throws DuplicateDataException  if generated ID collides
     */
    public MaintenanceRequest reportFault(String equipmentId,
                                          String instructorId,
                                          String description,
                                          MaintenanceUrgency urgency)
            throws EntityNotFoundException, DuplicateDataException {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Cannot report fault: Equipment ID '%s' not found.", equipmentId)));

        // Update equipment status
        equipment.setStatus(EquipmentStatus.FAULTY);
        equipmentRepository.update(equipment);

        String requestId = "MR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        MaintenanceRequest request = new MaintenanceRequest(requestId, equipmentId, instructorId, description, urgency);
        maintenanceRepository.save(request);

        // Notify Admins
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.EQUIPMENT_FAULT_REPORTED,
                "Equipment Fault Reported",
                String.format("Fault reported for '%s' (ID: %s) by Instructor '%s' [Urgency: %s]: %s",
                        equipment.getName(), equipmentId, instructorId, urgency, description),
                UserRole.ADMIN
        );
        notificationSubject.notifyObservers(event);

        return request;
    }

    /**
     * Assigns a pending maintenance ticket to an administrator for servicing.
     * Transitions ticket status from PENDING -> ASSIGNED and equipment to UNDER_MAINTENANCE.
     *
     * @param requestId ticket ID
     * @param adminId   administrator ID
     * @return updated MaintenanceRequest
     * @throws EntityNotFoundException if request or equipment not found
     */
    public MaintenanceRequest assignRequest(String requestId, String adminId)
            throws EntityNotFoundException {

        MaintenanceRequest request = getRequestById(requestId);
        request.assignTo(adminId);
        maintenanceRepository.update(request);

        // Set equipment to UNDER_MAINTENANCE
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId()).orElse(null);
        if (equipment != null) {
            equipment.setStatus(EquipmentStatus.UNDER_MAINTENANCE);
            equipmentRepository.update(equipment);
        }

        // Notify staff of assignment
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.MAINTENANCE_STATUS_CHANGED,
                "Maintenance Ticket Assigned",
                String.format("Ticket '%s' for Equipment '%s' has been assigned to Admin '%s'.",
                        requestId, request.getEquipmentId(), adminId),
                UserRole.INSTRUCTOR
        );
        notificationSubject.notifyObservers(event);

        return request;
    }

    /**
     * Marks a maintenance request as completed, resetting the equipment's usage hours and restoring OPERATIONAL status.
     *
     * @param requestId       ticket ID
     * @param resolutionNotes notes explaining servicing outcome
     * @return updated MaintenanceRequest
     * @throws EntityNotFoundException if request not found
     */
    public MaintenanceRequest completeRequest(String requestId, String resolutionNotes)
            throws EntityNotFoundException {

        MaintenanceRequest request = getRequestById(requestId);
        request.complete(resolutionNotes);
        maintenanceRepository.update(request);

        // Restore equipment to OPERATIONAL and reset cumulative usage counter
        Equipment equipment = equipmentRepository.findById(request.getEquipmentId()).orElse(null);
        if (equipment != null) {
            equipment.resetMaintenanceCycle();
            equipmentRepository.update(equipment);
        }

        // Broadcast completion notification
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.MAINTENANCE_STATUS_CHANGED,
                "Maintenance Completed",
                String.format("Service completed on Equipment '%s' (Ticket '%s'). Equipment restored to OPERATIONAL.",
                        request.getEquipmentId(), requestId),
                null // Broadcast to all roles
        );
        notificationSubject.notifyObservers(event);

        return request;
    }

    public MaintenanceRequest getRequestById(String requestId) throws EntityNotFoundException {
        return maintenanceRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Maintenance request with ID '%s' was not found.", requestId)));
    }

    public List<MaintenanceRequest> getAllRequests() {
        return maintenanceRepository.findAll();
    }

    public List<MaintenanceRequest> getRequestsByStatus(MaintenanceStatus status) {
        return maintenanceRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequest> getRequestsByUrgency(MaintenanceUrgency urgency) {
        return maintenanceRepository.findAll().stream()
                .filter(r -> r.getUrgency() == urgency)
                .collect(Collectors.toList());
    }
}
