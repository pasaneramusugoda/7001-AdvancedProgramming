package com.iwfc.util;

import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.scheduling.SessionType;
import com.iwfc.model.user.Admin;
import com.iwfc.model.user.Instructor;
import com.iwfc.model.user.Member;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.pattern.creational.SessionBuilder;
import com.iwfc.pattern.structural.IWFCSystemFacade;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Utility responsible for seeding the in-memory repositories with realistic IWFC data
 * for interactive exploration, test execution, and presentation demonstrations.
 */
public class DataInitializer {

    public static void initializeSeedData(IWFCSystemFacade facade) {
        try {
            // 1. Seed Users
            Admin admin = new Admin("ADM-01", "Sarah Connor", "admin@iwfc.com", "Facility Operations");
            Instructor instructor1 = new Instructor("INS-01", "Alex Rivera", "alex@iwfc.com", "Spin & HIIT Specialist");
            Instructor instructor2 = new Instructor("INS-02", "Elena Rostova", "elena@iwfc.com", "Yoga & Pilates Master");
            Member member1 = new Member("MEM-01", "David Miller", "david@gmail.com", "Platinum");
            Member member2 = new Member("MEM-02", "Emma Watson", "emma@gmail.com", "Gold");
            Member member3 = new Member("MEM-03", "John Doe", "john@gmail.com", "Standard");

            facade.registerUser(admin);
            facade.registerUser(instructor1);
            facade.registerUser(instructor2);
            facade.registerUser(member1);
            facade.registerUser(member2);
            facade.registerUser(member3);

            // 2. Seed Equipment via EquipmentFactory
            Equipment tm1 = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "TM-01", "NordicTrack Commercial 2450", "Cardio Zone");
            tm1.logUsage(196.0); // 4 hours away from 200h preventative maintenance alert!

            Equipment tm2 = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "TM-02", "NordicTrack Commercial 2450", "Cardio Zone");
            tm2.logUsage(65.0);

            Equipment sb1 = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-01", "Keiser M3i Indoor Cycle", "Studio A");
            sb1.logUsage(148.0); // 2 hours away from 150h preventative maintenance alert!

            Equipment sb2 = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "SB-02", "Keiser M3i Indoor Cycle", "Studio A");
            sb2.logUsage(42.0);

            Equipment rm1 = EquipmentFactory.createEquipment(EquipmentType.ROWING_MACHINE, "RM-01", "Concept2 Model D Rower", "Cardio Zone");
            rm1.logUsage(110.0);

            facade.getEquipmentRepository().save(tm1);
            facade.getEquipmentRepository().save(tm2);
            facade.getEquipmentRepository().save(sb1);
            facade.getEquipmentRepository().save(sb2);
            facade.getEquipmentRepository().save(rm1);

            // 3. Seed Scheduled Fitness Sessions using SessionBuilder
            LocalDate tomorrow = LocalDate.now().plusDays(1);

            FitnessSession session1 = new SessionBuilder()
                    .withId("SES-01")
                    .withTitle("Morning Spin Sprint")
                    .withSessionType(SessionType.SPIN_CLASS)
                    .withStudioLocation("Studio A")
                    .withInstructorId("INS-01")
                    .withTimes(LocalDateTime.of(tomorrow, LocalTime.of(7, 0)),
                               LocalDateTime.of(tomorrow, LocalTime.of(8, 0)))
                    .withMaxCapacity(15)
                    .withRequiredEquipment(List.of("SB-01", "SB-02"))
                    .asRecurring(DayOfWeek.MONDAY)
                    .build();

            FitnessSession session2 = new SessionBuilder()
                    .withId("SES-02")
                    .withTitle("Vinyasa Mindful Flow")
                    .withSessionType(SessionType.YOGA)
                    .withStudioLocation("Studio B")
                    .withInstructorId("INS-02")
                    .withTimes(LocalDateTime.of(tomorrow, LocalTime.of(9, 0)),
                               LocalDateTime.of(tomorrow, LocalTime.of(10, 15)))
                    .withMaxCapacity(20)
                    .asRecurring(DayOfWeek.WEDNESDAY)
                    .build();

            FitnessSession session3 = new SessionBuilder()
                    .withId("SES-03")
                    .withTitle("HIIT Cardio Blast")
                    .withSessionType(SessionType.HIIT)
                    .withStudioLocation("Studio A")
                    .withInstructorId("INS-01")
                    .withTimes(LocalDateTime.of(tomorrow, LocalTime.of(17, 30)),
                               LocalDateTime.of(tomorrow, LocalTime.of(18, 30)))
                    .withMaxCapacity(10)
                    .withRequiredEquipment(List.of("TM-01", "TM-02"))
                    .build();

            facade.getSessionRepository().save(session1);
            facade.getSessionRepository().save(session2);
            facade.getSessionRepository().save(session3);

            // 4. Seed Existing Bookings
            facade.bookSessionForMember("SES-01", "MEM-01");
            facade.bookSessionForMember("SES-02", "MEM-02");

            // 5. Seed Maintenance Ticket
            facade.login("INS-01");
            facade.reportEquipmentFault("SB-02", "Pedal crank exhibits clicking noise at high cadence resistance.", MaintenanceUrgency.MEDIUM);
            facade.logout();

        } catch (Exception e) {
            System.err.println("Warning: Seed data initialization encountered an issue: " + e.getMessage());
        }
    }
}
