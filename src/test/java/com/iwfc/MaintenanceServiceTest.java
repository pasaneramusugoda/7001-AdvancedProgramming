package com.iwfc;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentStatus;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.maintenance.MaintenanceRequest;
import com.iwfc.model.maintenance.MaintenanceStatus;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.pattern.behavioural.observer.ConsoleNotificationObserver;
import com.iwfc.pattern.behavioural.observer.NotificationEvent;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import com.iwfc.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Maintenance Workflow & Observer Notification Tests")
public class MaintenanceServiceTest {

    private Repository<MaintenanceRequest, String> maintenanceRepo;
    private Repository<Equipment, String> equipmentRepo;
    private NotificationSubject notificationSubject;
    private ConsoleNotificationObserver testObserver;
    private MaintenanceService maintenanceService;

    private Equipment testBike;

    @BeforeEach
    void setUp() throws DuplicateDataException {
        maintenanceRepo = new InMemoryRepository<>();
        equipmentRepo = new InMemoryRepository<>();
        notificationSubject = new NotificationSubject();

        // Attach test observer without console printing during test
        testObserver = new ConsoleNotificationObserver("TestObserver", false);
        notificationSubject.attach(testObserver);

        maintenanceService = new MaintenanceService(maintenanceRepo, equipmentRepo, notificationSubject);

        testBike = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-TEST", "Keiser Spin Pro", "Studio A");
        testBike.logUsage(120.0);
        equipmentRepo.save(testBike);
    }

    @Test
    @DisplayName("Should successfully report fault, set equipment to FAULTY, and notify observers")
    void testReportFaultTransitions() throws Exception {
        MaintenanceRequest request = maintenanceService.reportFault(
                "SB-TEST", "INS-01", "Resistance belt slipping under load", MaintenanceUrgency.HIGH);

        assertNotNull(request);
        assertEquals(MaintenanceStatus.PENDING, request.getStatus());
        assertEquals("SB-TEST", request.getEquipmentId());
        assertEquals(MaintenanceUrgency.HIGH, request.getUrgency());

        // Equipment status must transition to FAULTY
        Equipment updatedBike = equipmentRepo.findById("SB-TEST").orElseThrow();
        assertEquals(EquipmentStatus.FAULTY, updatedBike.getStatus());

        // Verify Observer received event
        List<NotificationEvent> events = testObserver.getEventLog();
        assertFalse(events.isEmpty());
        assertEquals(NotificationEvent.EventType.EQUIPMENT_FAULT_REPORTED, events.get(0).getEventType());
    }

    @Test
    @DisplayName("Should transition ticket to ASSIGNED and equipment to UNDER_MAINTENANCE")
    void testAssignMaintenanceWorkflow() throws Exception {
        MaintenanceRequest request = maintenanceService.reportFault(
                "SB-TEST", "INS-01", "Crank noise", MaintenanceUrgency.MEDIUM);

        MaintenanceRequest assigned = maintenanceService.assignRequest(request.getId(), "ADM-01");

        assertEquals(MaintenanceStatus.ASSIGNED, assigned.getStatus());
        assertEquals("ADM-01", assigned.getAssignedToAdminId());

        Equipment updatedBike = equipmentRepo.findById("SB-TEST").orElseThrow();
        assertEquals(EquipmentStatus.UNDER_MAINTENANCE, updatedBike.getStatus());

        // Check observer events
        List<NotificationEvent> events = testObserver.getEventLog();
        assertTrue(events.stream().anyMatch(e -> e.getEventType() == NotificationEvent.EventType.MAINTENANCE_STATUS_CHANGED));
    }

    @Test
    @DisplayName("Should complete ticket, reset cumulative usage, and restore equipment to OPERATIONAL")
    void testCompleteMaintenanceWorkflow() throws Exception {
        MaintenanceRequest request = maintenanceService.reportFault(
                "SB-TEST", "INS-01", "Flywheel calibration", MaintenanceUrgency.LOW);
        maintenanceService.assignRequest(request.getId(), "ADM-01");

        MaintenanceRequest completed = maintenanceService.completeRequest(
                request.getId(), "Flywheel realigned and bearings lubricated.");

        assertEquals(MaintenanceStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getResolvedAt());
        assertEquals("Flywheel realigned and bearings lubricated.", completed.getResolutionNotes());

        // Verify equipment is restored to OPERATIONAL and usage reset
        Equipment restoredBike = equipmentRepo.findById("SB-TEST").orElseThrow();
        assertEquals(EquipmentStatus.OPERATIONAL, restoredBike.getStatus());
        assertEquals(0.0, restoredBike.getCumulativeUsageHours());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when reporting fault for non-existent equipment")
    void testReportFaultNonExistentEquipment() {
        assertThrows(EntityNotFoundException.class, () ->
                maintenanceService.reportFault("INVALID-ID", "INS-01", "Broken", MaintenanceUrgency.LOW)
        );
    }
}
