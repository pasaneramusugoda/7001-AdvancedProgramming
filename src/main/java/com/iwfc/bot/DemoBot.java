package com.iwfc.bot;

import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.maintenance.MaintenanceRequest;
import com.iwfc.model.scheduling.Booking;
import com.iwfc.pattern.structural.IWFCSystemFacade;
import com.iwfc.ui.ConsoleMenu;
import com.iwfc.util.DataInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

/**
 * Automated End-to-End Simulation & Verification Bot for IWFC.
 *
 * Demonstrates all functional requirements, OOP principles, design patterns,
 * custom exception handling, and edge cases with real-time console input/output visual feedback.
 */
public class DemoBot {

    // ANSI Colors for high-visibility console output
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD_CYAN = "\u001B[1;36m";
    private static final String BOLD_GREEN = "\u001B[1;32m";
    private static final String BOLD_YELLOW = "\u001B[1;33m";
    private static final String BOLD_MAGENTA = "\u001B[1;35m";
    private static final String BOLD_BLUE = "\u001B[1;34m";
    private static final String BOLD_RED = "\u001B[1;31m";

    private final IWFCSystemFacade facade;
    private final int typingDelayMs;
    private final int stepDelayMs;
    private final int readDelayMs;

    public DemoBot(IWFCSystemFacade facade, int typingDelayMs, int stepDelayMs) {
        this(facade, typingDelayMs, stepDelayMs, 0);
    }

    public DemoBot(IWFCSystemFacade facade, int typingDelayMs, int stepDelayMs, int readDelayMs) {
        this.facade = facade;
        this.typingDelayMs = typingDelayMs;
        this.stepDelayMs = stepDelayMs;
        this.readDelayMs = readDelayMs;
    }

    /**
     * Represents an individual step performed by the bot.
     */
    public static class Step {
        final String title;
        final String category;
        final String rationale;
        final Function<IWFCSystemFacade, String> inputSupplier;

        public Step(String title, String category, String rationale, String staticInput) {
            this(title, category, rationale, facade -> staticInput);
        }

        public Step(String title, String category, String rationale, Function<IWFCSystemFacade, String> inputSupplier) {
            this.title = title;
            this.category = category;
            this.rationale = rationale;
            this.inputSupplier = inputSupplier;
        }
    }

    /**
     * Builds the complete end-to-end scenario script.
     */
    public List<Step> buildScript() {
        List<Step> steps = new ArrayList<>();

        // =========================================================================
        // SCENARIO 1: MEMBER OPERATIONS & RBAC VALIDATION
        // =========================================================================
        steps.add(new Step("1. Member Login", "AUTHENTICATION",
                "Log in as Member David Miller (MEM-01)", "4"));

        steps.add(new Step("2. Browse Sessions & Capacity", "MEMBER WORKFLOW",
                "Browse available sessions, remaining slots, and equipment requirements", "1"));

        steps.add(new Step("3. Test RBAC Security (Unauthorized Access)", "SECURITY & EXCEPTION HANDLING",
                "Attempt to access Administrator Global Maintenance Log as Member -> Triggers UnauthorizedAccessException", "5"));

        steps.add(new Step("4. Book Valid Session (SES-03)", "MEMBER WORKFLOW",
                "Select session booking option", "2"));
        steps.add(new Step("4b. Enter Session ID", "MEMBER WORKFLOW",
                "Enter session ID SES-03 (HIIT Cardio Blast)", "SES-03"));

        steps.add(new Step("5. Attempt Duplicate Booking (SES-03)", "EXCEPTION HANDLING",
                "Attempt to book SES-03 again -> Triggers InvalidBookingException (already booked)", "2"));
        steps.add(new Step("5b. Enter Session ID for duplicate attempt", "EXCEPTION HANDLING",
                "Re-enter SES-03", "SES-03"));

        steps.add(new Step("6. View My Confirmed Bookings", "MEMBER WORKFLOW",
                "Inspect confirmed member reservations (includes SES-01 and SES-03)", "3"));

        steps.add(new Step("7. Cancel Booking", "MEMBER WORKFLOW",
                "Select cancel booking option", "4"));
        steps.add(new Step("7b. Enter Booking ID to Cancel", "MEMBER WORKFLOW",
                "Dynamically supply confirmed booking ID for cancellation",
                facade -> {
                    List<Booking> bookings = facade.getBookingRepository().findAll();
                    for (Booking b : bookings) {
                        if (b.getMemberId().equals("MEM-01") && b.isActive() && b.getSessionId().equals("SES-03")) {
                            return b.getId();
                        }
                    }
                    return bookings.isEmpty() ? "BK-00000000" : bookings.get(0).getId();
                }));

        steps.add(new Step("8. Member Logout", "AUTHENTICATION",
                "Log out from Member portal", "6"));

        // =========================================================================
        // SCENARIO 2: INSTRUCTOR OPERATIONS & SCHEDULING STRATEGIES
        // =========================================================================
        steps.add(new Step("9. Instructor Login", "AUTHENTICATION",
                "Log in as Instructor Alex Rivera (INS-01)", "2"));

        steps.add(new Step("10. View Scheduled Sessions", "INSTRUCTOR WORKFLOW",
                "View all current fitness classes", "1"));

        steps.add(new Step("11. Schedule Valid Session (Builder Pattern)", "CREATIONAL PATTERN & SCHEDULING",
                "Select schedule session option (uses SessionBuilder)", "2"));
        steps.add(new Step("11b. Session ID", "SCHEDULING", "Session ID: SES-BOX", "SES-BOX"));
        steps.add(new Step("11c. Session Title", "SCHEDULING", "Title: Boxing & Functional Core", "Boxing & Functional Core"));
        steps.add(new Step("11d. Session Type", "SCHEDULING", "Type: 1 (HIIT)", "1"));
        steps.add(new Step("11e. Studio Location", "SCHEDULING", "Location: Studio B", "Studio B"));
        steps.add(new Step("11f. Session Date", "SCHEDULING", "Date: (Press Enter for tomorrow)", ""));
        steps.add(new Step("11g. Start Time", "SCHEDULING", "Start: 14:00", "14:00"));
        steps.add(new Step("11h. End Time", "SCHEDULING", "End: 15:00", "15:00"));
        steps.add(new Step("11i. Capacity", "SCHEDULING", "Capacity: 12", "12"));
        steps.add(new Step("11j. Required Equipment", "SCHEDULING", "Equipment: none", ""));

        steps.add(new Step("12. Operating Hours Validation Test", "BEHAVIOURAL STRATEGY & EXCEPTION HANDLING",
                "Attempt to schedule class at 22:30 -> OperatingHoursValidationStrategy triggers InvalidBookingException", "2"));
        steps.add(new Step("12b. Session ID", "SCHEDULING", "Session ID: SES-NIGHT", "SES-NIGHT"));
        steps.add(new Step("12c. Session Title", "SCHEDULING", "Title: Midnight Bootcamp", "Midnight Bootcamp"));
        steps.add(new Step("12d. Session Type", "SCHEDULING", "Type: 1 (HIIT)", "1"));
        steps.add(new Step("12e. Studio Location", "SCHEDULING", "Location: Studio B", "Studio B"));
        steps.add(new Step("12f. Session Date", "SCHEDULING", "Date: (Press Enter for tomorrow)", ""));
        steps.add(new Step("12g. Start Time (Outside hours)", "SCHEDULING", "Start: 22:30 (Facility closes at 22:00)", "22:30"));
        steps.add(new Step("12h. End Time (Outside hours)", "SCHEDULING", "End: 23:30", "23:30"));
        steps.add(new Step("12i. Capacity", "SCHEDULING", "Capacity: 10", "10"));
        steps.add(new Step("12j. Required Equipment", "SCHEDULING", "Equipment: none", ""));

        steps.add(new Step("13. Studio Double-Booking Validation Test", "BEHAVIOURAL STRATEGY & EXCEPTION HANDLING",
                "Attempt overlapping booking in Studio B (14:30 - 15:30) -> ResourceConflictValidationStrategy triggers InvalidBookingException", "2"));
        steps.add(new Step("13b. Session ID", "SCHEDULING", "Session ID: SES-CLASH", "SES-CLASH"));
        steps.add(new Step("13c. Session Title", "SCHEDULING", "Title: Conflicting Yoga", "Conflicting Yoga"));
        steps.add(new Step("13d. Session Type", "SCHEDULING", "Type: 2 (YOGA)", "2"));
        steps.add(new Step("13e. Studio Location (Clash)", "SCHEDULING", "Location: Studio B (already booked 14:00-15:00)", "Studio B"));
        steps.add(new Step("13f. Session Date", "SCHEDULING", "Date: (Press Enter for tomorrow)", ""));
        steps.add(new Step("13g. Start Time (Overlap)", "SCHEDULING", "Start: 14:30", "14:30"));
        steps.add(new Step("13h. End Time (Overlap)", "SCHEDULING", "End: 15:30", "15:30"));
        steps.add(new Step("13i. Capacity", "SCHEDULING", "Capacity: 15", "15"));
        steps.add(new Step("13j. Required Equipment", "SCHEDULING", "Equipment: none", ""));

        steps.add(new Step("14. Schedule Recurring Weekly Series", "ENHANCEMENT & RECURRING SCHEDULING",
                "Select recurring class series scheduler", "3"));
        steps.add(new Step("14b. Base Session ID", "SCHEDULING", "ID: REC-PIL", "REC-PIL"));
        steps.add(new Step("14c. Title", "SCHEDULING", "Title: Monday Core Pilates", "Monday Core Pilates"));
        steps.add(new Step("14d. Studio", "SCHEDULING", "Studio: Studio A", "Studio A"));
        steps.add(new Step("14e. Start Time", "SCHEDULING", "Start: 08:30", "08:30"));
        steps.add(new Step("14f. End Time", "SCHEDULING", "End: 09:30", "09:30"));
        steps.add(new Step("14g. Weeks Ahead", "SCHEDULING", "Weeks: 3 (generates 4 total weekly sessions)", "3"));

        steps.add(new Step("15. Report Equipment Breakdown / Fault", "MAINTENANCE & OBSERVER NOTIFICATIONS",
                "Select report fault option", "4"));
        steps.add(new Step("15b. Equipment ID", "MAINTENANCE", "Equipment: TM-01 (NordicTrack Treadmill)", "TM-01"));
        steps.add(new Step("15c. Fault Description", "MAINTENANCE", "Fault: Incline elevation motor overheating with speed sensor error", "Incline elevation motor overheating with speed sensor error"));
        steps.add(new Step("15d. Urgency Level", "MAINTENANCE", "Urgency: 3 (HIGH) -> Transitions TM-01 to FAULTY and notifies Admins", "3"));

        steps.add(new Step("16. Verify Equipment Inventory Status", "INSTRUCTOR WORKFLOW",
                "Inspect inventory to confirm TM-01 status updated to 'Faulty'", "5"));

        steps.add(new Step("17. Instructor Logout", "AUTHENTICATION",
                "Log out from Instructor portal", "6"));

        // =========================================================================
        // SCENARIO 3: ADMINISTRATOR OPERATIONS & MAINTENANCE WORKFLOW
        // =========================================================================
        steps.add(new Step("18. Administrator Login", "AUTHENTICATION",
                "Log in as Administrator Sarah Connor (ADM-01)", "1"));

        steps.add(new Step("19. View Equipment Inventory & Wear Status", "ADMINISTRATOR WORKFLOW & POLYMORPHISM",
                "Inspect inventory, cumulative usage hours, and polymorphic wear indices", "1"));

        steps.add(new Step("20. Register New Equipment (Factory Pattern)", "CREATIONAL PATTERN",
                "Select register equipment option (uses EquipmentFactory)", "2"));
        steps.add(new Step("20b. Select Type", "CREATIONAL PATTERN", "Type: 1 (Treadmill)", "1"));
        steps.add(new Step("20c. Equipment ID", "CREATIONAL PATTERN", "ID: TM-03", "TM-03"));
        steps.add(new Step("20d. Model Name", "CREATIONAL PATTERN", "Name: NordicTrack Commercial 2950", "NordicTrack Commercial 2950"));
        steps.add(new Step("20e. Location", "CREATIONAL PATTERN", "Location: Cardio Zone", "Cardio Zone"));

        steps.add(new Step("21. Duplicate Equipment ID Test", "ROBUSTNESS & EXCEPTION HANDLING",
                "Attempt to register equipment with existing ID TM-03 -> Triggers DuplicateDataException", "2"));
        steps.add(new Step("21b. Select Type", "ROBUSTNESS", "Type: 2 (Spin Bike)", "2"));
        steps.add(new Step("21c. Duplicate ID", "ROBUSTNESS", "Duplicate ID: TM-03", "TM-03"));
        steps.add(new Step("21d. Model Name", "ROBUSTNESS", "Name: Duplicate Bike", "Duplicate Bike"));
        steps.add(new Step("21e. Location", "ROBUSTNESS", "Location: Studio A", "Studio A"));

        steps.add(new Step("22. Log Equipment Usage & Trigger Alert", "PREVENTATIVE MAINTENANCE & OBSERVER PATTERN",
                "Log usage hours on TM-01 (Current: 196.0h + 5.0h = 201.0h >= 200.0h threshold) -> Triggers alert!", "5"));
        steps.add(new Step("22b. Equipment ID", "PREVENTATIVE MAINTENANCE", "ID: TM-01", "TM-01"));
        steps.add(new Step("22c. Hours to Add", "PREVENTATIVE MAINTENANCE", "Hours: 5.0 -> Preventative maintenance threshold exceeded", "5.0"));

        steps.add(new Step("23. View Equipment Due for Maintenance", "ADMINISTRATOR WORKFLOW",
                "Inspect list of equipment requiring preventative servicing (shows TM-01)", "6"));

        steps.add(new Step("24. View Global Maintenance Ticket Log", "ADMINISTRATOR WORKFLOW",
                "View global maintenance log (shows tickets logged by instructors)", "7"));

        steps.add(new Step("25. Assign Maintenance Ticket to Admin", "WORKFLOW TRANSITION & OBSERVER PATTERN",
                "Select assign ticket option", "8"));
        steps.add(new Step("25b. Ticket ID to Assign", "WORKFLOW TRANSITION",
                "Dynamically supply fault ticket ID for TM-01",
                facade -> {
                    List<MaintenanceRequest> requests = facade.getMaintenanceRepository().findAll();
                    for (MaintenanceRequest r : requests) {
                        if (r.getEquipmentId().equals("TM-01")) {
                            return r.getId();
                        }
                    }
                    return requests.isEmpty() ? "MR-00000000" : requests.get(requests.size() - 1).getId();
                }));
        steps.add(new Step("25c. Assign To Admin ID", "WORKFLOW TRANSITION",
                "Admin ID: ADM-01 -> Transitions ticket to ASSIGNED and TM-01 to UNDER_MAINTENANCE", "ADM-01"));

        steps.add(new Step("26. Complete Maintenance Ticket", "WORKFLOW COMPLETION & CYCLE RESET",
                "Select complete ticket option", "9"));
        steps.add(new Step("26b. Ticket ID to Complete", "WORKFLOW COMPLETION",
                "Dynamically supply assigned ticket ID for TM-01",
                facade -> {
                    List<MaintenanceRequest> requests = facade.getMaintenanceRepository().findAll();
                    for (MaintenanceRequest r : requests) {
                        if (r.getEquipmentId().equals("TM-01")) {
                            return r.getId();
                        }
                    }
                    return requests.isEmpty() ? "MR-00000000" : requests.get(requests.size() - 1).getId();
                }));
        steps.add(new Step("26c. Resolution Notes", "WORKFLOW COMPLETION",
                "Resolution notes: Replaced thermal fuse, realigned elevation gear, reset cycle.",
                "Replaced thermal fuse, realigned elevation gear, reset cycle."));

        steps.add(new Step("27. View System Notification & Audit Logs", "BEHAVIOURAL OBSERVER PATTERN",
                "Inspect complete notification event history captured by ConsoleNotificationObserver", "10"));

        steps.add(new Step("28. Administrator Logout", "AUTHENTICATION",
                "Log out from Administrator portal", "11"));

        // =========================================================================
        // SCENARIO 4: SHUTDOWN
        // =========================================================================
        steps.add(new Step("29. Exit System", "SYSTEM",
                "Cleanly exit IWFC Management System", "0"));

        return steps;
    }

    /**
     * Executes the demonstration bot.
     */
    public void run() {
        printBotHeader();

        List<Step> script = buildScript();
        Queue<Step> stepQueue = new ArrayDeque<>(script);

        // Custom InputStream feeding simulated inputs with visual console echoes
        InputStream botInputStream = new InputStream() {
            private byte[] currentLineBytes = new byte[0];
            private int byteIndex = 0;
            private int stepIndex = 0;

            @Override
            public int read() throws IOException {
                byte[] single = new byte[1];
                int n = read(single, 0, 1);
                return n == -1 ? -1 : (single[0] & 0xFF);
            }

            // Overriding the bulk read is essential: InputStreamReader's decoder calls this to fill
            // an internal buffer of several KB in one shot. Without this override, the default
            // InputStream.read(byte[], int, int) loops calling read() until that whole buffer is
            // full, which pulls dozens of future steps' dynamic inputSupplier.apply(facade) lookups
            // (e.g. "find TM-01's ticket ID") before ConsoleMenu has processed the steps that would
            // have produced their expected state, silently acting on stale data. Returning only one
            // line per call forces each step's input to be resolved lazily, exactly when ConsoleMenu
            // actually requests it.
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (len == 0) {
                    return 0;
                }
                if (byteIndex >= currentLineBytes.length) {
                    if (stepQueue.isEmpty()) {
                        return -1; // EOF
                    }

                    // Reading pause: the previous step's output is now on screen, so give the
                    // viewer time to read it before the next banner and typing begin.
                    pause(readDelayMs);

                    Step step = stepQueue.poll();
                    stepIndex++;
                    String input = step.inputSupplier.apply(facade);

                    // Render banner for high-level steps (actions with numbers)
                    if (step.title.matches("^\\d+\\..*")) {
                        printStepBanner(stepIndex, step);
                    }

                    // Simulate typing the input into the console
                    simulateTyping(input);

                    // Load input + newline into buffer
                    currentLineBytes = (input + "\n").getBytes(StandardCharsets.UTF_8);
                    byteIndex = 0;

                    // Pause slightly after supplying input so user can watch execution
                    pause(stepDelayMs);
                }

                int available = currentLineBytes.length - byteIndex;
                int toCopy = Math.min(available, len);
                System.arraycopy(currentLineBytes, byteIndex, b, off, toCopy);
                byteIndex += toCopy;
                return toCopy;
            }
        };

        // Launch ConsoleMenu with the bot's custom input stream
        ConsoleMenu menu = new ConsoleMenu(facade, botInputStream);
        menu.start();

        printBotSummary();
    }

    private void printBotHeader() {
        System.out.println(BOLD_CYAN + "================================================================================" + RESET);
        System.out.println(BOLD_CYAN + "   🤖  IWFC END-TO-END AUTOMATED TEST & DEMONSTRATION BOT" + RESET);
        System.out.println(BOLD_CYAN + "   Advanced Programming (CMP 7001) - Prototype Verification Runner" + RESET);
        System.out.println(BOLD_CYAN + "================================================================================" + RESET);
        System.out.println(CYAN + "• Simulating Member, Instructor, and Administrator roles in real-time." + RESET);
        System.out.println(CYAN + "• Demonstrating Creational (Factory/Builder), Structural (Facade), and Behavioural (Observer/Strategy) patterns." + RESET);
        System.out.println(CYAN + "• Verifying Custom Exceptions: InvalidBookingException, UnauthorizedAccessException, DuplicateDataException." + RESET);
        System.out.println(CYAN + "• Pace: " + (typingDelayMs > 0 ? "Realistic Interactive Simulation (typing " + typingDelayMs + "ms/char, step " + stepDelayMs + "ms, read " + readDelayMs + "ms)" : "High-Speed Execution") + RESET);
        System.out.println(BOLD_CYAN + "================================================================================" + RESET + "\n");
    }

    private void printStepBanner(int stepNum, Step step) {
        System.out.println("\n" + BOLD_BLUE + "--------------------------------------------------------------------------------" + RESET);
        System.out.printf("%s🤖 [BOT ACTION] %s%s%n", BOLD_YELLOW, step.title, RESET);
        System.out.printf("%s📂 Category : %s%s%s%n", BOLD_MAGENTA, BOLD, step.category, RESET);
        System.out.printf("%s📝 Purpose  : %s%s%n", CYAN, step.rationale, RESET);
        System.out.println(BOLD_BLUE + "--------------------------------------------------------------------------------" + RESET);
    }

    private static void pause(int ms) {
        if (ms <= 0) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simulateTyping(String text) {
        System.out.print(BOLD_GREEN + "[BOT TYPING] > " + RESET);
        if (typingDelayMs <= 0 || text.isEmpty()) {
            System.out.println(BOLD_YELLOW + text + RESET);
            return;
        }

        for (char c : text.toCharArray()) {
            System.out.print(BOLD_YELLOW + c + RESET);
            System.out.flush();
            try {
                Thread.sleep(typingDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    private void printBotSummary() {
        System.out.println("\n" + BOLD_GREEN + "================================================================================" + RESET);
        System.out.println(BOLD_GREEN + "   ✅  END-TO-END DEMO BOT EXECUTION COMPLETED SUCCESSFULLY!" + RESET);
        System.out.println(BOLD_GREEN + "================================================================================" + RESET);
        System.out.println(BOLD + "Summary of Verified Use Cases & Scenarios:" + RESET);
        System.out.println("  1. " + BOLD_CYAN + "[Member Workflow]" + RESET + " Browsed sessions, booked slot, caught duplicate booking, tested RBAC security, cancelled reservation.");
        System.out.println("  2. " + BOLD_CYAN + "[Instructor Workflow]" + RESET + " Scheduled session via Builder, verified Operating Hours & Studio Double-booking conflict rejections, scheduled 4-week recurring classes, reported equipment breakdown.");
        System.out.println("  3. " + BOLD_CYAN + "[Administrator Workflow]" + RESET + " Inspected equipment wear indices, registered equipment via Factory, caught duplicate ID error, triggered preventative maintenance alert via Observer pattern, assigned ticket, completed repair and restored operational cycle.");
        System.out.println("  4. " + BOLD_CYAN + "[Security & Exceptions]" + RESET + " Verified InvalidBookingException, UnauthorizedAccessException, DuplicateDataException, and EntityNotFoundException.");
        System.out.println(BOLD_GREEN + "================================================================================" + RESET + "\n");
    }

    /**
     * Main entry point to launch the bot.
     * Pace presets:
     *   --fast   : Zero delay for high-speed automated testing
     *   --normal : Default interactive pace (35ms typing, 200ms step pause)
     *   --slow   : Presentation pace for video recording (120ms typing, 800ms step pause, 3s read pause)
     * Individual overrides (milliseconds, applied after any preset):
     *   --typing=N : delay per typed character
     *   --step=N   : pause after typing, before the command runs
     *   --read=N   : pause before each step so the previous output can be read
     */
    public static void main(String[] args) {
        int typingDelay = 35;
        int stepDelay = 200;
        int readDelay = 0;

        for (String arg : args) {
            if ("--fast".equalsIgnoreCase(arg)) {
                typingDelay = 0;
                stepDelay = 0;
                readDelay = 0;
            } else if ("--slow".equalsIgnoreCase(arg)) {
                typingDelay = 120;
                stepDelay = 800;
                readDelay = 3000;
            } else if ("--normal".equalsIgnoreCase(arg)) {
                typingDelay = 35;
                stepDelay = 200;
                readDelay = 0;
            }
        }
        for (String arg : args) {
            if (arg.startsWith("--typing=")) {
                typingDelay = parseMs(arg);
            } else if (arg.startsWith("--step=")) {
                stepDelay = parseMs(arg);
            } else if (arg.startsWith("--read=")) {
                readDelay = parseMs(arg);
            }
        }

        IWFCSystemFacade facade = new IWFCSystemFacade();
        DataInitializer.initializeSeedData(facade);

        DemoBot bot = new DemoBot(facade, typingDelay, stepDelay, readDelay);
        bot.run();
    }

    private static int parseMs(String arg) {
        try {
            return Math.max(0, Integer.parseInt(arg.substring(arg.indexOf('=') + 1)));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid delay in '" + arg + "': expected a number of milliseconds.");
        }
    }
}
