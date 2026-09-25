package com.iwfc.ui;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.maintenance.MaintenanceRequest;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.model.scheduling.Booking;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.scheduling.SessionType;
import com.iwfc.model.user.User;
import com.iwfc.pattern.creational.SessionBuilder;
import com.iwfc.pattern.structural.IWFCSystemFacade;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console-based User Interface for IWFC.
 * Provides role-based navigation and clear presentation of features.
 */
public class ConsoleMenu {

    private final IWFCSystemFacade facade;
    private final Scanner scanner;
    private boolean inputExhausted = false;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleMenu(IWFCSystemFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            inputExhausted = true;
            return "0";
        }
        return scanner.nextLine().trim();
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running && !inputExhausted) {
            User currentUser = facade.getCurrentUser();
            if (currentUser == null) {
                running = showLoginMenu();
            } else {
                switch (currentUser.getRole()) {
                    case ADMIN -> showAdminMenu();
                    case INSTRUCTOR -> showInstructorMenu();
                    case MEMBER -> showMemberMenu();
                }
            }
        }
        System.out.println("\nThank you for using IWFC Management System. Goodbye!");
    }

    private void printBanner() {
        System.out.println("==================================================================");
        System.out.println("   INTELLIGENT WELLNESS AND FITNESS CENTER (IWFC) SYSTEM");
        System.out.println("   Advanced Programming (CMP 7001) - Cardiff Met / ICBT");
        System.out.println("==================================================================");
    }

    private boolean showLoginMenu() {
        System.out.println("\n--- USER LOGIN & ROLE SELECTION ---");
        System.out.println("Select a demo profile or enter custom ID:");
        System.out.println("1. [Admin]      ADM-01 (Sarah Connor - Facility Operations)");
        System.out.println("2. [Instructor] INS-01 (Alex Rivera - Spin & HIIT Specialist)");
        System.out.println("3. [Instructor] INS-02 (Elena Rostova - Yoga & Pilates Master)");
        System.out.println("4. [Member]     MEM-01 (David Miller - Platinum Member)");
        System.out.println("5. [Member]     MEM-02 (Emma Watson - Gold Member)");
        System.out.println("6. [Member]     MEM-03 (John Doe - Standard Member)");
        System.out.println("7. Enter Custom User ID");
        System.out.println("0. Exit System");
        System.out.print("Enter choice: ");

        String choice = readLine();
        String userIdToLogin = null;

        switch (choice) {
            case "1" -> userIdToLogin = "ADM-01";
            case "2" -> userIdToLogin = "INS-01";
            case "3" -> userIdToLogin = "INS-02";
            case "4" -> userIdToLogin = "MEM-01";
            case "5" -> userIdToLogin = "MEM-02";
            case "6" -> userIdToLogin = "MEM-03";
            case "7" -> {
                System.out.print("Enter User ID: ");
                userIdToLogin = readLine();
            }
            case "0" -> {
                return false;
            }
            default -> {
                System.out.println("Invalid selection. Please try again.");
                return true;
            }
        }

        try {
            User user = facade.login(userIdToLogin);
            System.out.printf("%n[LOGIN SUCCESS] Welcome, %s (%s)!%n", user.getName(), user.getRole().getDisplayName());
            System.out.println(user.getDashboardSummary());
        } catch (EntityNotFoundException e) {
            System.out.printf("[ERROR] %s%n", e.getMessage());
        }
        return true;
    }

    // ==========================================
    // ADMINISTRATOR MENU
    // ==========================================

    private void showAdminMenu() {
        System.out.println("\n==========================================");
        System.out.println("       ADMINISTRATOR CONTROL PANEL");
        System.out.println("==========================================");
        System.out.println("1. View Equipment Inventory & Wear Status");
        System.out.println("2. Register New Equipment (Factory Pattern)");
        System.out.println("3. Edit Equipment Details");
        System.out.println("4. Decommission / Deactivate Equipment");
        System.out.println("5. Log Equipment Usage Hours (Triggers Alerts)");
        System.out.println("6. View Equipment Due for Preventative Maintenance");
        System.out.println("7. View Global Maintenance Ticket Log");
        System.out.println("8. Assign Maintenance Ticket to Admin");
        System.out.println("9. Complete Maintenance Ticket (Reset Cycle)");
        System.out.println("10. View System Notification & Audit Logs");
        System.out.println("11. Switch User / Logout");
        System.out.print("Enter command: ");

        String choice = readLine();
        switch (choice) {
            case "1" -> viewAllEquipment();
            case "2" -> handleRegisterEquipment();
            case "3" -> handleEditEquipment();
            case "4" -> handleDecommissionEquipment();
            case "5" -> handleLogEquipmentUsage();
            case "6" -> viewPreventativeMaintenanceDue();
            case "7" -> viewMaintenanceLog();
            case "8" -> handleAssignMaintenance();
            case "9" -> handleCompleteMaintenance();
            case "10" -> viewSystemAuditLogs();
            case "11" -> {
                facade.logout();
                System.out.println("Logged out successfully.");
            }
            default -> System.out.println("Invalid option.");
        }
    }

    // ==========================================
    // INSTRUCTOR MENU
    // ==========================================

    private void showInstructorMenu() {
        System.out.println("\n==========================================");
        System.out.println("        INSTRUCTOR PORTAL");
        System.out.println("==========================================");
        System.out.println("1. View Scheduled Fitness Sessions");
        System.out.println("2. Schedule New Class (Builder & Strategy)");
        System.out.println("3. Schedule Recurring Weekly Class Series");
        System.out.println("4. Report Equipment Breakdown / Fault");
        System.out.println("5. View Equipment Inventory Status");
        System.out.println("6. Switch User / Logout");
        System.out.print("Enter command: ");

        String choice = readLine();
        switch (choice) {
            case "1" -> viewAllSessions();
            case "2" -> handleScheduleSession();
            case "3" -> handleScheduleRecurring();
            case "4" -> handleReportFault();
            case "5" -> viewAllEquipment();
            case "6" -> {
                facade.logout();
                System.out.println("Logged out successfully.");
            }
            default -> System.out.println("Invalid option.");
        }
    }

    // ==========================================
    // MEMBER MENU
    // ==========================================

    private void showMemberMenu() {
        System.out.println("\n==========================================");
        System.out.println("          MEMBER WELLNESS PORTAL");
        System.out.println("==========================================");
        System.out.println("1. Browse Available Fitness Sessions & Slots");
        System.out.println("2. Book a Fitness Session Slot");
        System.out.println("3. View My Confirmed Bookings");
        System.out.println("4. Cancel a Booking");
        System.out.println("5. Attempt Admin Log Access (Demonstrates Security RBAC)");
        System.out.println("6. Switch User / Logout");
        System.out.print("Enter command: ");

        String choice = readLine();
        switch (choice) {
            case "1" -> viewAllSessions();
            case "2" -> handleMemberBooking();
            case "3" -> handleViewMyBookings();
            case "4" -> handleCancelBooking();
            case "5" -> handleTestUnauthorizedAccess();
            case "6" -> {
                facade.logout();
                System.out.println("Logged out successfully.");
            }
            default -> System.out.println("Invalid option.");
        }
    }

    // ==========================================
    // HANDLERS & IMPLEMENTATIONS
    // ==========================================

    private void viewAllEquipment() {
        System.out.println("\n--- EQUIPMENT INVENTORY ---");
        List<Equipment> equipmentList = facade.getAllEquipment();
        if (equipmentList.isEmpty()) {
            System.out.println("No equipment registered.");
            return;
        }
        for (Equipment eq : equipmentList) {
            System.out.printf("• [%s] %s | Type: %s | Status: %-16s | Loc: %-12s | Usage: %5.1f / %5.1fh | Wear: %3.0f%%%n",
                    eq.getId(), eq.getName(), eq.getType(), eq.getStatus().getDescription(),
                    eq.getLocation(), eq.getCumulativeUsageHours(), eq.getMaintenanceThresholdHours(),
                    eq.calculateWearIndex() * 100);
        }
    }

    private void handleRegisterEquipment() {
        System.out.println("\n--- REGISTER NEW EQUIPMENT (FACTORY PATTERN) ---");
        System.out.println("Select Type: 1. Treadmill | 2. Spin Bike | 3. Rowing Machine");
        System.out.print("Choice: ");
        String typeChoice = readLine();
        EquipmentType type = switch (typeChoice) {
            case "1" -> EquipmentType.TREADMILL;
            case "2" -> EquipmentType.SPIN_BIKE;
            case "3" -> EquipmentType.ROWING_MACHINE;
            default -> null;
        };

        if (type == null) {
            System.out.println("Invalid equipment type.");
            return;
        }

        System.out.print("Enter Unique ID (e.g. TM-03): ");
        String id = readLine();
        System.out.print("Enter Model Name: ");
        String name = readLine();
        System.out.print("Enter Location (e.g. Studio A / Cardio Zone): ");
        String location = readLine();

        try {
            Equipment eq = facade.registerEquipment(type, id, name, location);
            System.out.printf("[SUCCESS] Registered %s (ID: %s) via EquipmentFactory.%n", eq.getName(), eq.getId());
        } catch (DuplicateDataException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleEditEquipment() {
        System.out.print("Enter Equipment ID to edit: ");
        String id = readLine();
        System.out.print("Enter New Name (leave blank to keep unchanged): ");
        String name = readLine();
        System.out.print("Enter New Location (leave blank to keep unchanged): ");
        String loc = readLine();

        try {
            Equipment updated = facade.updateEquipment(id, name, loc);
            System.out.printf("[SUCCESS] Updated: %s%n", updated);
        } catch (EntityNotFoundException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleDecommissionEquipment() {
        System.out.print("Enter Equipment ID to decommission: ");
        String id = readLine();

        try {
            Equipment eq = facade.deactivateEquipment(id);
            System.out.printf("[SUCCESS] Equipment %s marked as DECOMMISSIONED.%n", eq.getId());
        } catch (EntityNotFoundException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleLogEquipmentUsage() {
        System.out.print("Enter Equipment ID: ");
        String id = readLine();
        System.out.print("Enter Hours to add (e.g. 5.0): ");
        try {
            double hours = Double.parseDouble(readLine());
            Equipment eq = facade.logEquipmentUsage(id, hours);
            System.out.printf("[SUCCESS] Logged %.1f hours. New total: %.1f / %.1fh.%n",
                    hours, eq.getCumulativeUsageHours(), eq.getMaintenanceThresholdHours());
            if (eq.isMaintenanceDue()) {
                System.out.println(">>> [ALERT] PREVENTATIVE MAINTENANCE THRESHOLD EXCEEDED! <<<");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric value.");
        } catch (EntityNotFoundException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void viewPreventativeMaintenanceDue() {
        System.out.println("\n--- EQUIPMENT REQUIRING PREVENTATIVE MAINTENANCE ---");
        List<Equipment> dueList = facade.getEquipmentDueForMaintenance();
        if (dueList.isEmpty()) {
            System.out.println("All equipment is currently within safe operational usage thresholds.");
        } else {
            for (Equipment eq : dueList) {
                System.out.printf("• ALERT: [%s] %s | Usage: %.1fh / Threshold: %.1fh (Exceeded by %.1fh) | Loc: %s%n",
                        eq.getId(), eq.getName(), eq.getCumulativeUsageHours(),
                        eq.getMaintenanceThresholdHours(),
                        eq.getCumulativeUsageHours() - eq.getMaintenanceThresholdHours(),
                        eq.getLocation());
            }
        }
    }

    private void viewMaintenanceLog() {
        System.out.println("\n--- GLOBAL MAINTENANCE LOG ---");
        try {
            List<MaintenanceRequest> requests = facade.getGlobalMaintenanceLog();
            if (requests.isEmpty()) {
                System.out.println("No maintenance tickets found.");
            } else {
                for (MaintenanceRequest r : requests) {
                    System.out.printf("• Ticket: %-11s | Equip: %-6s | Urgency: %-8s | Status: %-12s | AssignedTo: %-8s | Desc: %s%n",
                            r.getId(), r.getEquipmentId(), r.getUrgency(), r.getStatus(),
                            r.getAssignedToAdminId() != null ? r.getAssignedToAdminId() : "Unassigned",
                            r.getIssueDescription());
                }
            }
        } catch (UnauthorizedAccessException e) {
            System.out.printf("[SECURITY EXCEPTION] %s%n", e.getMessage());
        }
    }

    private void handleAssignMaintenance() {
        System.out.print("Enter Ticket ID to assign: ");
        String ticketId = readLine();
        System.out.print("Enter Administrator ID to assign to (e.g. ADM-01): ");
        String adminId = readLine();

        try {
            MaintenanceRequest req = facade.assignMaintenanceTicket(ticketId, adminId);
            System.out.printf("[SUCCESS] Ticket %s transitioned to ASSIGNED (Assigned to %s).%n", req.getId(), adminId);
        } catch (EntityNotFoundException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleCompleteMaintenance() {
        System.out.print("Enter Ticket ID to complete: ");
        String ticketId = readLine();
        System.out.print("Enter Resolution Service Notes: ");
        String notes = readLine();

        try {
            MaintenanceRequest req = facade.completeMaintenanceTicket(ticketId, notes);
            System.out.printf("[SUCCESS] Ticket %s completed! Associated equipment has been reset to OPERATIONAL.%n", req.getId());
        } catch (EntityNotFoundException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void viewSystemAuditLogs() {
        System.out.println("\n--- SYSTEM NOTIFICATION & AUDIT LOGS (OBSERVER PATTERN) ---");
        var logs = facade.getSystemAuditLogs();
        if (logs.isEmpty()) {
            System.out.println("No audit logs captured yet.");
        } else {
            for (var event : logs) {
                System.out.println(event);
            }
        }
    }

    private void viewAllSessions() {
        System.out.println("\n--- SCHEDULED FITNESS SESSIONS ---");
        List<FitnessSession> sessions = facade.getAllSessions();
        if (sessions.isEmpty()) {
            System.out.println("No sessions currently scheduled.");
            return;
        }
        for (FitnessSession s : sessions) {
            int bookedSlots = facade.getSchedulingService().getActiveBookingsForSession(s.getId()).size();
            System.out.printf("• [%s] %-22s | Type: %-12s | Studio: %-8s | %s to %s | Filled: %d/%d | Equip: %s%n",
                    s.getId(), s.getTitle(), s.getSessionType(), s.getStudioLocation(),
                    s.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                    s.getEndTime().format(TIME_FORMATTER),
                    bookedSlots, s.getMaxCapacity(),
                    s.getRequiredEquipmentIds().isEmpty() ? "None" : String.join(", ", s.getRequiredEquipmentIds()));
        }
    }

    private void handleScheduleSession() {
        System.out.println("\n--- SCHEDULE NEW FITNESS SESSION (BUILDER PATTERN) ---");
        System.out.print("Enter Session ID (e.g. SES-04): ");
        String id = readLine();
        System.out.print("Enter Session Title (e.g. Power Core Pilates): ");
        String title = readLine();

        System.out.println("Select Type: 1. HIIT | 2. YOGA | 3. PILATES | 4. SPIN_CLASS | 5. STRENGTH_CIRCUIT");
        System.out.print("Choice: ");
        String typeStr = readLine();
        SessionType type = switch (typeStr) {
            case "1" -> SessionType.HIIT;
            case "2" -> SessionType.YOGA;
            case "3" -> SessionType.PILATES;
            case "4" -> SessionType.SPIN_CLASS;
            case "5" -> SessionType.STRENGTH_CIRCUIT;
            default -> SessionType.HIIT;
        };

        System.out.print("Enter Studio Location (e.g. Studio A / Studio B): ");
        String studio = readLine();

        System.out.print("Enter Date (YYYY-MM-DD, press Enter for tomorrow): ");
        String dateStr = readLine();
        LocalDate date = dateStr.isBlank() ? LocalDate.now().plusDays(1) : LocalDate.parse(dateStr, DATE_FORMATTER);

        System.out.print("Enter Start Time (HH:mm, e.g. 08:30): ");
        LocalTime startTime = LocalTime.parse(readLine(), TIME_FORMATTER);
        System.out.print("Enter End Time (HH:mm, e.g. 09:30): ");
        LocalTime endTime = LocalTime.parse(readLine(), TIME_FORMATTER);

        System.out.print("Enter Max Participant Capacity (e.g. 15): ");
        int capacity = Integer.parseInt(readLine());

        System.out.print("Enter Required Equipment IDs separated by comma (or leave blank): ");
        String eqInput = readLine();
        List<String> equipmentList = new ArrayList<>();
        if (!eqInput.isBlank()) {
            for (String eq : eqInput.split(",")) {
                equipmentList.add(eq.trim());
            }
        }

        try {
            FitnessSession session = new SessionBuilder()
                    .withId(id)
                    .withTitle(title)
                    .withSessionType(type)
                    .withStudioLocation(studio)
                    .withInstructorId(facade.getCurrentUser().getId())
                    .withTimes(LocalDateTime.of(date, startTime), LocalDateTime.of(date, endTime))
                    .withMaxCapacity(capacity)
                    .withRequiredEquipment(equipmentList)
                    .build();

            FitnessSession scheduled = facade.scheduleSession(session);
            System.out.printf("[SUCCESS] Successfully scheduled session: %s%n", scheduled.getTitle());
        } catch (InvalidBookingException | DuplicateDataException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use specified format.");
        }
    }

    private void handleScheduleRecurring() {
        System.out.println("\n--- SCHEDULE RECURRING WEEKLY SESSION ---");
        System.out.print("Enter Base Session ID (e.g. REC-01): ");
        String id = readLine();
        System.out.print("Enter Title (e.g. Monday Morning Pilates): ");
        String title = readLine();
        System.out.print("Enter Studio (e.g. Studio B): ");
        String studio = readLine();
        System.out.print("Enter Start Time (HH:mm): ");
        LocalTime start = LocalTime.parse(readLine(), TIME_FORMATTER);
        System.out.print("Enter End Time (HH:mm): ");
        LocalTime end = LocalTime.parse(readLine(), TIME_FORMATTER);
        System.out.print("Number of consecutive weeks ahead to schedule (e.g. 4): ");
        int weeks = Integer.parseInt(readLine());

        LocalDate nextMonday = LocalDate.now().plusDays(1);
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plusDays(1);
        }

        try {
            FitnessSession base = new SessionBuilder()
                    .withId(id)
                    .withTitle(title)
                    .withSessionType(SessionType.PILATES)
                    .withStudioLocation(studio)
                    .withInstructorId(facade.getCurrentUser().getId())
                    .withTimes(LocalDateTime.of(nextMonday, start), LocalDateTime.of(nextMonday, end))
                    .withMaxCapacity(15)
                    .asRecurring(DayOfWeek.MONDAY)
                    .build();

            List<FitnessSession> scheduled = facade.scheduleRecurringWeeklySession(base, weeks);
            System.out.printf("[SUCCESS] Scheduled %d recurring weekly occurrences of '%s'!%n", scheduled.size(), title);
        } catch (InvalidBookingException | DuplicateDataException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleReportFault() {
        System.out.println("\n--- REPORT EQUIPMENT FAULT ---");
        System.out.print("Enter Equipment ID (e.g. SB-01, TM-01): ");
        String eqId = readLine();
        System.out.print("Enter Fault Description: ");
        String desc = readLine();
        System.out.println("Select Urgency: 1. LOW | 2. MEDIUM | 3. HIGH | 4. CRITICAL");
        System.out.print("Choice: ");
        String urgChoice = readLine();
        MaintenanceUrgency urgency = switch (urgChoice) {
            case "1" -> MaintenanceUrgency.LOW;
            case "2" -> MaintenanceUrgency.MEDIUM;
            case "3" -> MaintenanceUrgency.HIGH;
            case "4" -> MaintenanceUrgency.CRITICAL;
            default -> MaintenanceUrgency.MEDIUM;
        };

        try {
            MaintenanceRequest req = facade.reportEquipmentFault(eqId, desc, urgency);
            System.out.printf("[SUCCESS] Fault ticket logged (%s). Equipment marked as FAULTY. Administrators notified!%n", req.getId());
        } catch (EntityNotFoundException | DuplicateDataException | UnauthorizedAccessException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleMemberBooking() {
        System.out.println("\n--- BOOK A SESSION SLOT ---");
        System.out.print("Enter Session ID to book (e.g. SES-01): ");
        String sessionId = readLine();

        try {
            Booking booking = facade.bookSession(sessionId);
            System.out.printf("[BOOKING SUCCESS] Confirmed! Ticket ID: %s for Session: %s%n",
                    booking.getId(), booking.getSessionId());
        } catch (InvalidBookingException | EntityNotFoundException | UnauthorizedAccessException e) {
            System.out.printf("[BOOKING REJECTED] %s%n", e.getMessage());
        }
    }

    private void handleViewMyBookings() {
        System.out.println("\n--- MY CONFIRMED BOOKINGS ---");
        try {
            List<Booking> bookings = facade.getMyBookings();
            if (bookings.isEmpty()) {
                System.out.println("You have no active bookings.");
            } else {
                for (Booking b : bookings) {
                    System.out.printf("• Booking ID: %-11s | Session: %-8s | Status: %-10s | Reserved At: %s%n",
                            b.getId(), b.getSessionId(), b.getStatus(),
                            b.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        } catch (UnauthorizedAccessException e) {
            System.out.printf("[SECURITY EXCEPTION] %s%n", e.getMessage());
        }
    }

    private void handleCancelBooking() {
        System.out.print("Enter Booking ID to cancel (e.g. BK-...): ");
        String bookingId = readLine();
        try {
            Booking b = facade.cancelBooking(bookingId);
            System.out.printf("[SUCCESS] Booking %s has been cancelled.%n", b.getId());
        } catch (EntityNotFoundException e) {
            System.out.printf("[EXCEPTION CAUGHT] %s%n", e.getMessage());
        }
    }

    private void handleTestUnauthorizedAccess() {
        System.out.println("\n--- TESTING SECURITY ROLE-BASED ACCESS CONTROL (RBAC) ---");
        System.out.println("Attempting to access Administrator Global Maintenance Log as Member...");
        try {
            facade.getGlobalMaintenanceLog();
            System.out.println("[WARNING] Security bypass: Unauthorized access was erroneously permitted!");
        } catch (UnauthorizedAccessException e) {
            System.out.println("[RBAC SECURITY VERIFIED] UnauthorizedAccessException properly thrown and handled!");
            System.out.printf("System message: %s%n", e.getMessage());
        }
    }
}
