package com.iwfc;

import com.iwfc.common.Constants;
import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentStatus;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.scheduling.Booking;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.scheduling.SessionType;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.pattern.creational.SessionBuilder;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import com.iwfc.service.SchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Scheduling Service & Validation Unit Tests")
public class SchedulingServiceTest {

    private Repository<FitnessSession, String> sessionRepo;
    private Repository<Booking, String> bookingRepo;
    private Repository<Equipment, String> equipmentRepo;
    private NotificationSubject notificationSubject;
    private SchedulingService schedulingService;

    private LocalDate testDate;

    @BeforeEach
    void setUp() throws DuplicateDataException {
        sessionRepo = new InMemoryRepository<>();
        bookingRepo = new InMemoryRepository<>();
        equipmentRepo = new InMemoryRepository<>();
        notificationSubject = new NotificationSubject();

        schedulingService = new SchedulingService(sessionRepo, bookingRepo, equipmentRepo, notificationSubject);

        testDate = LocalDate.now().plusDays(2);

        // Pre-register test equipment
        Equipment bike1 = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-10", "Keiser Bike 10", "Studio A");
        Equipment bike2 = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-11", "Keiser Bike 11", "Studio A");
        Equipment faultyTreadmill = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "TM-99", "Broken Treadmill", "Cardio Zone");
        faultyTreadmill.setStatus(EquipmentStatus.FAULTY);

        equipmentRepo.save(bike1);
        equipmentRepo.save(bike2);
        equipmentRepo.save(faultyTreadmill);
    }

    @Test
    @DisplayName("Should successfully schedule a valid fitness session")
    void testValidSessionScheduling() throws Exception {
        FitnessSession session = new SessionBuilder()
                .withId("SES-VALID")
                .withTitle("Morning Vinyasa")
                .withSessionType(SessionType.YOGA)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(8, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(9, 0)))
                .withMaxCapacity(20)
                .build();

        FitnessSession saved = schedulingService.scheduleSession(session);
        assertNotNull(saved);
        assertEquals("SES-VALID", saved.getId());
        assertEquals(1, sessionRepo.findAll().size());
    }

    @Test
    @DisplayName("Should reject scheduling session outside facility operating hours")
    void testOperatingHoursValidation() {
        // Facility closes at 22:00; test session scheduled 22:30 to 23:30
        FitnessSession lateSession = new SessionBuilder()
                .withId("SES-LATE")
                .withTitle("Late Night Cardio")
                .withSessionType(SessionType.HIIT)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-01")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(22, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(23, 30)))
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.scheduleSession(lateSession)
        );
        assertTrue(ex.getMessage().contains("operating hours"));
    }

    @Test
    @DisplayName("Should strictly prevent studio double-booking for overlapping time slots")
    void testStudioDoubleBookingPrevention() throws Exception {
        FitnessSession session1 = new SessionBuilder()
                .withId("SES-S1")
                .withTitle("Spin Blast")
                .withSessionType(SessionType.SPIN_CLASS)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-01")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(10, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(11, 0)))
                .build();
        schedulingService.scheduleSession(session1);

        // Conflicting session in same Studio A from 10:30 to 11:30
        FitnessSession conflictingSession = new SessionBuilder()
                .withId("SES-S2")
                .withTitle("HIIT Fusion")
                .withSessionType(SessionType.HIIT)
                .withStudioLocation("Studio A") // Same studio
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(10, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(11, 30)))
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.scheduleSession(conflictingSession)
        );
        assertTrue(ex.getMessage().contains("Studio double-booking conflict"));
    }

    @Test
    @DisplayName("Should prevent allocating the same equipment to multiple concurrent sessions")
    void testEquipmentDoubleBookingPrevention() throws Exception {
        FitnessSession session1 = new SessionBuilder()
                .withId("SES-EQ1")
                .withTitle("Spin Group 1")
                .withSessionType(SessionType.SPIN_CLASS)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-01")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(14, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(15, 0)))
                .withRequiredEquipment(List.of("SB-10"))
                .build();
        schedulingService.scheduleSession(session1);

        // Another studio tries to use the same equipment (SB-10) during overlapping time
        FitnessSession session2 = new SessionBuilder()
                .withId("SES-EQ2")
                .withTitle("Spin Group 2")
                .withSessionType(SessionType.SPIN_CLASS)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(14, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(15, 30)))
                .withRequiredEquipment(List.of("SB-10")) // Equipment conflict
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.scheduleSession(session2)
        );
        assertTrue(ex.getMessage().contains("Equipment double-booking conflict"));
    }

    @Test
    @DisplayName("Should reject scheduling sessions requiring faulty equipment")
    void testFaultyEquipmentRejection() {
        FitnessSession faultyEquipSession = new SessionBuilder()
                .withId("SES-FAULTY")
                .withTitle("Treadmill Speed Run")
                .withSessionType(SessionType.HIIT)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-01")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(16, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(17, 0)))
                .withRequiredEquipment(List.of("TM-99")) // Marked as FAULTY
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.scheduleSession(faultyEquipSession)
        );
        assertTrue(ex.getMessage().contains("Equipment unavailable"));
    }

    @Test
    @DisplayName("Should enforce session maximum capacity limit on member bookings")
    void testSessionCapacityEnforcement() throws Exception {
        FitnessSession smallSession = new SessionBuilder()
                .withId("SES-CAP")
                .withTitle("Exclusive Workshop")
                .withSessionType(SessionType.PILATES)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(11, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(12, 0)))
                .withMaxCapacity(2) // Only 2 slots
                .build();
        schedulingService.scheduleSession(smallSession);

        // Book slot 1 and 2
        schedulingService.bookSession("SES-CAP", "MEM-01");
        schedulingService.bookSession("SES-CAP", "MEM-02");

        // Attempting 3rd booking must throw InvalidBookingException
        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.bookSession("SES-CAP", "MEM-03")
        );
        assertTrue(ex.getMessage().contains("maximum capacity"));
    }

    @Test
    @DisplayName("Should prevent duplicate booking by the same member in the same session")
    void testMemberDuplicateBookingPrevention() throws Exception {
        FitnessSession session = new SessionBuilder()
                .withId("SES-DUP")
                .withTitle("Yoga Flow")
                .withSessionType(SessionType.YOGA)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(13, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(14, 0)))
                .build();
        schedulingService.scheduleSession(session);

        schedulingService.bookSession("SES-DUP", "MEM-01");

        // Duplicate attempt by MEM-01
        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () ->
                schedulingService.bookSession("SES-DUP", "MEM-01")
        );
        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    @DisplayName("Should schedule recurring weekly session series successfully")
    void testRecurringSessionScheduling() throws Exception {
        FitnessSession base = new SessionBuilder()
                .withId("SES-REC")
                .withTitle("Monday Morning Pilates")
                .withSessionType(SessionType.PILATES)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-02")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(7, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(8, 30)))
                .asRecurring(DayOfWeek.MONDAY)
                .build();

        List<FitnessSession> scheduled = schedulingService.scheduleRecurringWeeklySession(base, 4);
        // Base week + 4 weeks ahead = 5 occurrences
        assertEquals(5, scheduled.size());
        assertEquals(5, sessionRepo.findAll().size());
    }
}
