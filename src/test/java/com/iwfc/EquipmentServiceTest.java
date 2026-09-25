package com.iwfc;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.equipment.RowingMachine;
import com.iwfc.model.equipment.SpinBike;
import com.iwfc.model.equipment.Treadmill;
import com.iwfc.pattern.behavioural.observer.ConsoleNotificationObserver;
import com.iwfc.pattern.behavioural.observer.NotificationEvent;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import com.iwfc.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Equipment Service & Factory Pattern Unit Tests")
public class EquipmentServiceTest {

    private Repository<Equipment, String> equipmentRepo;
    private NotificationSubject notificationSubject;
    private ConsoleNotificationObserver testObserver;
    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        equipmentRepo = new InMemoryRepository<>();
        notificationSubject = new NotificationSubject();
        testObserver = new ConsoleNotificationObserver("TestObserver", false);
        notificationSubject.attach(testObserver);

        equipmentService = new EquipmentService(equipmentRepo, notificationSubject);
    }

    @Test
    @DisplayName("Should create correct subtypes and initial values via EquipmentFactory")
    void testEquipmentFactoryCreations() {
        Equipment tm = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "TM-01", "NordicTrack", "Cardio Zone");
        Equipment sb = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-01", "Keiser Bike", "Studio A");
        Equipment rm = EquipmentFactory.createEquipment(EquipmentType.ROWING_MACHINE, "RM-01", "Concept2", "Cardio Zone");

        assertInstanceOf(Treadmill.class, tm);
        assertInstanceOf(SpinBike.class, sb);
        assertInstanceOf(RowingMachine.class, rm);

        assertEquals("Cardio Zone", tm.getLocation());
        assertEquals(0.0, tm.getCumulativeUsageHours());
    }

    @Test
    @DisplayName("Should demonstrate polymorphic wear calculation across equipment subtypes")
    void testPolymorphicWearCalculation() {
        Equipment tm = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "TM-01", "NordicTrack", "Cardio Zone");
        Equipment sb = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-01", "Keiser Bike", "Studio A");

        // Log 100 hours on both
        tm.logUsage(100.0);
        sb.logUsage(100.0);

        // Treadmill wear index = (100 / 200) * 1.15 = 0.575
        // Spin bike wear index = (100 / 150) * 1.05 = 0.700
        assertTrue(tm.calculateWearIndex() > 0);
        assertTrue(sb.calculateWearIndex() > 0);
        assertNotEquals(tm.calculateWearIndex(), sb.calculateWearIndex());
    }

    @Test
    @DisplayName("Should trigger preventative maintenance notification when usage hours reach threshold")
    void testPreventativeMaintenanceAlertTrigger() throws Exception {
        Equipment tm = equipmentService.registerEquipment(EquipmentType.TREADMILL, "TM-ALERT", "Commercial Treadmill", "Cardio Zone");

        // Treadmill threshold is 200 hours. Log 199 hours -> no alert
        equipmentService.logUsage("TM-ALERT", 199.0);
        assertFalse(tm.isMaintenanceDue());
        assertTrue(testObserver.getEventLog().isEmpty());

        // Log 2 more hours -> total 201.0 >= 200.0 -> trigger alert!
        equipmentService.logUsage("TM-ALERT", 2.0);
        assertTrue(tm.isMaintenanceDue());

        List<NotificationEvent> events = testObserver.getEventLog();
        assertEquals(1, events.size());
        assertEquals(NotificationEvent.EventType.PREVENTATIVE_MAINTENANCE_DUE, events.get(0).getEventType());
        assertTrue(events.get(0).getMessage().contains("reached 201.0 hours"));
    }

    @Test
    @DisplayName("Should reject registering equipment with duplicate ID")
    void testDuplicateEquipmentIdRejection() throws Exception {
        equipmentService.registerEquipment(EquipmentType.TREADMILL, "TM-DUP", "Treadmill 1", "Zone A");

        DuplicateDataException ex = assertThrows(DuplicateDataException.class, () ->
                equipmentService.registerEquipment(EquipmentType.SPIN_BIKE, "TM-DUP", "Spin Bike 1", "Studio B")
        );
        assertTrue(ex.getMessage().contains("Duplicate entry"));
    }
}
