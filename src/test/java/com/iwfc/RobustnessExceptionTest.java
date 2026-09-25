package com.iwfc;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.scheduling.SessionType;
import com.iwfc.model.user.Admin;
import com.iwfc.model.user.Instructor;
import com.iwfc.model.user.Member;
import com.iwfc.pattern.creational.SessionBuilder;
import com.iwfc.pattern.structural.IWFCSystemFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Robustness Verification Test Suite.
 * Validates that custom domain exceptions (LO3) are strictly thrown and verified via JUnit 5 assertThrows.
 */
@DisplayName("Robustness Verification & Custom Exception Handling Tests")
public class RobustnessExceptionTest {

    private IWFCSystemFacade facade;
    private LocalDate testDate;

    @BeforeEach
    void setUp() throws DuplicateDataException {
        facade = new IWFCSystemFacade();
        testDate = LocalDate.now().plusDays(3);

        // Register actors
        facade.registerUser(new Admin("ADM-TEST", "Admin User", "admin@iwfc.com", "Operations"));
        facade.registerUser(new Instructor("INS-TEST", "Instructor User", "ins@iwfc.com", "Fitness"));
        facade.registerUser(new Member("MEM-TEST", "Member User", "mem@iwfc.com", "Standard"));
    }

    // =========================================================================
    // 1. MANDATORY CUSTOM EXCEPTION: UnauthorizedAccessException
    // =========================================================================

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when Member attempts to access Administrator maintenance logs")
    void testUnauthorizedAccessToAdminMaintenanceLog() throws Exception {
        // Log in as Member
        facade.login("MEM-TEST");

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class, () -> {
            facade.getGlobalMaintenanceLog();
        });

        assertTrue(ex.getMessage().contains("Access denied"));
        assertTrue(ex.getMessage().contains("Administrator"));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when Member attempts to register new equipment")
    void testUnauthorizedAccessToEquipmentRegistration() throws Exception {
        // Log in as Member
        facade.login("MEM-TEST");

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class, () -> {
            facade.registerEquipment(EquipmentType.TREADMILL, "TM-HACK", "Hacked Treadmill", "Studio A");
        });

        assertTrue(ex.getMessage().contains("Access denied"));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when unauthenticated user attempts to schedule session")
    void testUnauthorizedSessionSchedulingWithoutLogin() {
        // Ensure no active user session
        facade.logout();

        FitnessSession session = new SessionBuilder()
                .withId("SES-UNAUTH")
                .withTitle("Unauthorized Yoga")
                .withSessionType(SessionType.YOGA)
                .withStudioLocation("Studio B")
                .withInstructorId("INS-TEST")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(10, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(11, 0)))
                .build();

        assertThrows(UnauthorizedAccessException.class, () -> {
            facade.scheduleSession(session);
        });
    }

    // =========================================================================
    // 2. MANDATORY CUSTOM EXCEPTION: DuplicateDataException
    // =========================================================================

    @Test
    @DisplayName("Should throw DuplicateDataException when attempting to register equipment with an existing ID")
    void testDuplicateEquipmentRegistration() throws Exception {
        facade.login("ADM-TEST");

        // First registration succeeds
        facade.registerEquipment(EquipmentType.SPIN_BIKE, "SB-UNIQUE-1", "Keiser Bike", "Studio A");

        // Second registration with duplicate ID SB-UNIQUE-1 must throw DuplicateDataException
        DuplicateDataException ex = assertThrows(DuplicateDataException.class, () -> {
            facade.registerEquipment(EquipmentType.TREADMILL, "SB-UNIQUE-1", "Duplicate Treadmill", "Cardio Zone");
        });

        assertTrue(ex.getMessage().contains("Duplicate entry error"));
        assertTrue(ex.getMessage().contains("SB-UNIQUE-1"));
    }

    @Test
    @DisplayName("Should throw DuplicateDataException when registering user with existing ID")
    void testDuplicateUserRegistration() {
        DuplicateDataException ex = assertThrows(DuplicateDataException.class, () -> {
            facade.registerUser(new Member("MEM-TEST", "Duplicate Member", "dup@test.com", "Gold"));
        });
        assertTrue(ex.getMessage().contains("Duplicate entry"));
    }

    // =========================================================================
    // 3. MANDATORY CUSTOM EXCEPTION: InvalidBookingException
    // =========================================================================

    @Test
    @DisplayName("Should throw InvalidBookingException when scheduling outside facility operating hours (06:00 - 22:00)")
    void testInvalidBookingOutsideOperatingHours() throws Exception {
        facade.login("INS-TEST");

        // Session scheduled at 04:30 AM (Facility opens at 06:00)
        FitnessSession earlySession = new SessionBuilder()
                .withId("SES-EARLY")
                .withTitle("Dawn Bootcamp")
                .withSessionType(SessionType.HIIT)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-TEST")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(4, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(5, 30)))
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () -> {
            facade.scheduleSession(earlySession);
        });

        assertTrue(ex.getMessage().contains("operating hours"));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when studio double-booking occurs")
    void testInvalidBookingStudioConflict() throws Exception {
        facade.login("INS-TEST");

        FitnessSession session1 = new SessionBuilder()
                .withId("SES-100")
                .withTitle("Morning Spin")
                .withSessionType(SessionType.SPIN_CLASS)
                .withStudioLocation("Studio A")
                .withInstructorId("INS-TEST")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(9, 0)),
                           LocalDateTime.of(testDate, LocalTime.of(10, 0)))
                .build();
        facade.scheduleSession(session1);

        FitnessSession clashingSession = new SessionBuilder()
                .withId("SES-101")
                .withTitle("Zumba Dance")
                .withSessionType(SessionType.CARDIO_BLAST)
                .withStudioLocation("Studio A") // Collision in Studio A
                .withInstructorId("INS-TEST")
                .withTimes(LocalDateTime.of(testDate, LocalTime.of(9, 30)),
                           LocalDateTime.of(testDate, LocalTime.of(10, 30)))
                .build();

        InvalidBookingException ex = assertThrows(InvalidBookingException.class, () -> {
            facade.scheduleSession(clashingSession);
        });

        assertTrue(ex.getMessage().contains("Studio double-booking conflict"));
    }

    // =========================================================================
    // 4. ENTITY NOT FOUND EXCEPTION
    // =========================================================================

    @Test
    @DisplayName("Should throw EntityNotFoundException when logging in with non-existent user ID")
    void testEntityNotFoundOnLogin() {
        assertThrows(EntityNotFoundException.class, () -> {
            facade.login("NON-EXISTENT-ID");
        });
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when reporting fault on non-existent equipment")
    void testEntityNotFoundOnFaultReporting() throws Exception {
        facade.login("INS-TEST");

        assertThrows(EntityNotFoundException.class, () -> {
            facade.reportEquipmentFault("GHOST-EQUIP-999", "Broken belt", MaintenanceUrgency.LOW);
        });
    }
}
