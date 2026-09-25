# CARDIFF METROPOLITAN UNIVERSITY / ICBT CAMPUS
## SCHOOL OF TECHNOLOGIES
### MODULE: CMP 7001 – ADVANCED PROGRAMMING
---
# PRESENTATION OF A SOFTWARE PROTOTYPE (PRES 1)
## SLIDE DECK SPECIFICATION, 10-MINUTE VIDEO SCRIPT, AND REFLECTIVE REPORT

**Academic Year:** 2025/2026 | **Semester:** Semester 1  
**Module Leader:** royian11@gmail.com  
**Assessment Nature:** PRES 1 – Presentation (25% Weighting)  
**Target Word Count:** 1,000 words equivalent (10-minute video presentation)  
**Submission Format:** PDF & Word Document (Slides & Script) with YouTube/OneDrive video link on Slide 1  

---

### Student Declaration
*I certify that the attached material is my original work. No other person’s work or ideas have been used without acknowledgement. Except where I have clearly stated that I have used some of this material elsewhere, I have not presented it for examination / assessment in any other course or unit at this or any other institution.*

**Student Name:** [Your Name]  
**Batch Number:** [Your Batch Number]  
**Cardiff Met ID:** [Your Cardiff Met ID]  
**ICBT ID:** [Your ICBT ID]  
**Video Presentation Link:** `https://youtu.be/YOUR_VIDEO_LINK_HERE` *(or OneDrive Shared URL)*  

---

## Part 1: Complete 12-Slide PowerPoint Presentation Deck

```
========================================================================================
SLIDE 1: TITLE SLIDE & COVERSHEET
========================================================================================
[Visual Layout: Dark navy background with crisp white typography, university logos]

Title: Intelligent Wellness and Fitness Center (IWFC) Management System
Subtitle: Architectural Conception, Design Patterns, and Prototype Verification
Module: CMP 7001 – Advanced Programming (Cardiff Metropolitan University / ICBT)

Student Details:
• Student Name: [Your Name]
• Cardiff Met ID: [Your ID] | ICBT ID: [Your ICBT ID]
• Video Walkthrough Link: https://youtu.be/YOUR_VIDEO_LINK_HERE

SPEAKER NOTES (0:00 - 0:45):
"Good day, everyone. My name is [Your Name], and today I am presenting the software architecture, object-oriented design, and empirical verification of the Intelligent Wellness and Fitness Center prototype, developed for CMP 7001 Advanced Programming. The video link on this title slide provides direct access to the live recording of this walkthrough. Today, we will explore how advanced Java techniques, Gang-of-Four design patterns, and robust exception handling were combined to solve the complex operational challenges of a modern fitness facility."
```

```
========================================================================================
SLIDE 2: PROBLEM SCENARIO & STAKEHOLDER REQUIREMENTS
========================================================================================
[Visual Layout: 3-column layout showing the three primary actors and their responsibilities]

Background:
• Transitioning IWFC from fragmented manual records to a unified Java management tool.
• Core Goal: Eliminate unexpected machinery downtime, prevent double-bookings, and streamline maintenance handovers.

Primary Actors:
1. Administrator:
   - Equipment inventory management (Add, update, decommission).
   - Preventative maintenance threshold oversight & usage tracking.
   - Global maintenance log management and ticket assignment.
2. Instructor:
   - Fitness class scheduling (HIIT, Yoga, Pilates, Spin).
   - Recurring weekly session planning.
   - Mechanical fault and safety incident reporting.
3. Member:
   - Session schedule browsing and slot capacity tracking.
   - Session reservations and cancellations.
   - Automated schedule alerts and wellness notifications.

SPEAKER NOTES (0:45 - 1:30):
"To ground our design in realistic requirements, our problem scenario centers on the Intelligent Wellness and Fitness Center. IWFC was previously hindered by three major operational bottlenecks: unexpected machine breakdowns during peak hours, studio and equipment double-booking conflicts, and untracked maintenance requests. To address this, our system serves three primary actors: Administrators who oversee equipment and global maintenance, Instructors who plan classes and report faults, and Members who view schedules and reserve class slots. Each actor has distinct operational boundaries enforced by Role-Based Access Control."
```

```
========================================================================================
SLIDE 3: DOMAIN MODEL & ARCHITECTURAL CLASS DIAGRAM
========================================================================================
[Visual Layout: High-resolution UML Class Diagram highlighting decoupled layering]

Key Architectural Layers:
• Common / Generics: Identifiable<ID>, Constants, Repository<T, ID>
• Domain Entities: User Hierarchy (Admin, Instructor, Member), Equipment Hierarchy (Treadmill, SpinBike, RowingMachine)
• Scheduling & Maintenance: FitnessSession, Booking, MaintenanceRequest
• Facade & Services: IWFCSystemFacade coordinating AuthService, EquipmentService, SchedulingService, MaintenanceService

Highlights:
• 100% adherence to Single Responsibility and Open/Closed Principles.
• Domain contracts separated from storage implementations via generic repositories.

SPEAKER NOTES (1:30 - 2:30):
"Moving to the architecture, here is the complete UML class diagram. The system is engineered around a clean six-tier decoupled architecture. At the foundation, we define an Identifiable<ID> interface, which powers our generic Repository<T, ID> contract. Notice our two primary domain taxonomies: the User hierarchy, branching into Admin, Instructor, and Member; and the Equipment hierarchy, branching into specialized models like Treadmill, Spin Bike, and Rowing Machine. Rather than coupling client code to individual controllers, all subsystem interactions pass through our IWFCSystemFacade, which enforces security and atomic execution across all workflows."
```

```
========================================================================================
SLIDE 4: OBJECT-ORIENTED PRINCIPLES & POLYMORPHISM
========================================================================================
[Visual Layout: Split slide showing abstract method definition on left and mathematical formula on right]

1. Abstraction & Encapsulation:
   • Private state variables, validated accessors/mutators, unmodifiable collection wrappers.
   • Abstract base classes (User, Equipment) define shared behavior and state.

2. Polymorphic Dynamic Dispatch:
   • Polymorphic Wear Index Calculation: eq.calculateWearIndex()
   • Base Wear = Cumulative Usage Hours / Maintenance Threshold Hours
   - Treadmill: BaseWear * 1.15 (motor incline & deck friction strain)
   - SpinBike: BaseWear * 1.05 (high-cadence flywheel resistance)
   - RowingMachine: BaseWear * 1.10 (drive chain damper tension)
   • Dynamic Role Summaries: user.getDashboardSummary()

SPEAKER NOTES (2:30 - 3:30):
"A central requirement of learning outcome one and two is the critical application of OOP principles and polymorphism. Encapsulation is enforced by declaring all domain fields private and protecting internal collections with unmodifiable wrappers. Dynamic polymorphism is prominently showcased in our mechanical wear tracking. Gym equipment experiences different mechanical stresses. By overriding calculateWearIndex() in Treadmill, SpinBike, and RowingMachine, our reporting subsystems compute wear scores polymorphically without knowing the concrete machine type. If we add new equipment in the future, zero existing code needs to be modified, satisfying the Open/Closed Principle."
```

```
========================================================================================
SLIDE 5: CREATIONAL DESIGN PATTERNS
========================================================================================
[Visual Layout: Diagrams illustrating Factory Method and Fluent Builder structures]

1. Factory Method Pattern (EquipmentFactory):
   • Problem: Instantiating specialized equipment requires specific threshold calibration.
   • Solution: Encapsulates instantiation logic behind EquipmentFactory.createEquipment().
   • Benefit: Decouples high-level services from concrete machine constructors; isolates threshold constants (e.g., 200h for Treadmills, 150h for Spin Bikes).

2. Fluent Builder Pattern (SessionBuilder):
   • Problem: FitnessSession requires 11 distinct configuration parameters. Telescoping constructors cause error-prone parameter lists.
   • Solution: Method-chaining builder with pre-build validation invariants.
   • Example:
     new SessionBuilder().withId("SES-01").withTitle("Morning Spin")
         .withTimes(start, end).withMaxCapacity(15).build();

SPEAKER NOTES (3:30 - 4:30):
"For our creational design patterns, we implemented two complementary patterns. First, the Factory Method pattern via EquipmentFactory. When an administrator registers equipment, the factory encapsulates the instantiation details and attaches default maintenance thresholds—such as 200 hours for treadmills and 150 hours for spin bikes. Second, we implemented the Fluent Builder pattern via SessionBuilder. Creating a fitness session requires eleven parameters, including time windows, equipment requirements, capacity, and recurrence flags. The Builder eliminates telescoping constructors, promotes readable code, and enforces domain validation before building the immutable session."
```

```
========================================================================================
SLIDE 6: STRUCTURAL & BEHAVIOURAL DESIGN PATTERNS
========================================================================================
[Visual Layout: Two visual flow diagrams: Facade subsystem orchestration and Observer publish/subscribe]

1. Facade Pattern (IWFCSystemFacade) - Structural:
   • Provides a unified, high-level interface shielding UI layers from multi-service complexity.
   • Enforces Role-Based Access Control (RBAC) across all operations.

2. Observer Pattern (NotificationSubject & Observers) - Behavioural:
   • Decouples event producers from consumers.
   • Automatically broadcasts:
     - Preventative Maintenance Alerts (when usage >= threshold).
     - Equipment Fault Reports.
     - Maintenance Status Lifecycle Updates.

3. Strategy Pattern (BookingValidationStrategy) - Behavioural:
   • Pluggable, interchangeable validation algorithms:
     - OperatingHoursValidationStrategy (Enforces 06:00 - 22:00 bounds).
     - ResourceConflictValidationStrategy (Prevents studio and equipment collisions).

SPEAKER NOTES (4:30 - 5:30):
"Looking at structural and behavioural design patterns: structurally, the IWFCSystemFacade acts as our central orchestration hub. It shields client applications from the complexity of four separate services while intercepting every call to verify role permissions. In the behavioural category, we implemented the Observer pattern. When cumulative machine hours surpass safety thresholds or a technician updates a repair ticket, the NotificationSubject broadcasts typed events to registered observers without tight coupling. Furthermore, we used the Strategy pattern for session booking validation, encapsulating operating hour checks and spatial collision checks into interchangeable validation algorithms."
```

```
========================================================================================
SLIDE 7: CUSTOM EXCEPTION HANDLING & APPLICATION SECURITY
========================================================================================
[Visual Layout: Hierarchical tree diagram of custom exceptions]

Checked Exception Hierarchy:
• Root: IWFCException extends Exception (Compile-time checked enforcement)
  ├── InvalidBookingException
  │    • Operating hours violation (06:00 - 22:00)
  │    • Studio double-booking clash
  │    • Specialized equipment clash
  │    • Class capacity limit exceeded
  ├── UnauthorizedAccessException
  │    • Role-based security violations (e.g., Member accessing Admin logs)
  ├── DuplicateDataException
  │    • Primary key collisions (e.g., duplicate equipment ID "TM-01")
  └── EntityNotFoundException
       • Non-existent entity queries (User, Equipment, Session, Ticket)

Design-by-Contract & Defensive Programming:
• Zero silent failures; descriptive diagnostic messaging; fail-fast validation.

SPEAKER NOTES (5:30 - 6:15):
"To satisfy learning outcome three and industry security standards, we avoided generic runtime exceptions in favor of a strongly-typed checked exception hierarchy rooted in IWFCException. We created specialized exceptions: InvalidBookingException handles double-bookings, capacity bounds, and out-of-hours scheduling; UnauthorizedAccessException protects role-based boundaries, ensuring members cannot view administrator logs; and DuplicateDataException prevents ID collisions. Using checked exceptions compels calling layers to explicitly handle error scenarios, ensuring that our application fails safely without crashing."
```

```
========================================================================================
SLIDE 8: LIVE SYSTEM DEMONSTRATION & USE CASE WALKTHROUGH
========================================================================================
[Visual Layout: Terminal screenshot showing the colored interactive console and DemoBot in action]

Live Demonstration Highlights:
1. Member Journey:
   • Browses sessions -> Attempts Admin log access (Catches UnauthorizedAccessException) -> Books session -> Catches duplicate booking -> Cancels reservation.
2. Instructor Journey:
   • Schedules class via Builder -> Catches out-of-hours scheduling (22:30) -> Catches Studio B clash -> Schedules 4-week recurring series -> Reports TM-01 fault.
3. Administrator Journey:
   • Views wear indices -> Registers TM-03 via Factory -> Catches duplicate ID -> Logs 5h usage on TM-01 (Triggers Preventative Alert) -> Assigns & completes maintenance ticket -> Resets cycle to Operational.

SPEAKER NOTES (6:15 - 7:30):
"[SWITCH SCREEN TO TERMINAL]. Let us now observe the live application in action. We created an automated simulation bot, DemoBot, which executes all 29 lifecycle steps across all three roles with live console feedback. Watching the console, first the Member logs in, views class capacities, and attempts to access the administrator maintenance log. As expected, our security layer throws an UnauthorizedAccessException. Next, the Instructor logs in and attempts to schedule a class at 10:30 PM. The OperatingHoursStrategy instantly rejects it. The instructor then reports a fault on Treadmill TM-01, automatically transitioning its status to Faulty. Finally, the Administrator logs in, logs usage hours that trigger a Preventative Maintenance Alert via the Observer pattern, assigns the ticket, and marks it complete—resetting the usage counter and restoring TM-01 to Operational status."
```

```
========================================================================================
SLIDE 9: UNIT TESTING & ROBUSTNESS VERIFICATION
========================================================================================
[Visual Layout: Maven test execution report showing 26/26 passing tests with green checkmarks]

Testing Strategy (JUnit 5 Jupiter):
• Automated Test Harness: 26 total tests across 5 test classes.
• Test Isolation: @BeforeEach instantiates fresh repositories and subjects.

Test Class Breakdown:
1. SchedulingServiceTest (8 Tests): Session booking, capacity bounds, operating hours, studio clashes, equipment clashes, recurring classes.
2. MaintenanceServiceTest (4 Tests): State transitions (Pending -> Assigned -> Completed), observer notification verification.
3. EquipmentServiceTest (4 Tests): Factory creation, polymorphic wear scores, preventative threshold triggers.
4. RobustnessExceptionTest (9 Tests): Mandatory intentional negative testing verifying assertThrows for all custom exceptions.
5. DemoBotIntegrationTest (1 Test): Full 29-step automated end-to-end integration pass.

Results: 26 Tests Run, 0 Failures, 0 Errors, Execution Time: 1.28s.

SPEAKER NOTES (7:30 - 8:15):
"To empirically verify our prototype, we engineered a thorough unit test suite using JUnit 5. Our 26 tests cover core logic, workflow transitions, and boundary conditions. In strict accordance with the assessment brief, we included a dedicated test suite, RobustnessExceptionTest, containing intentional failing and negative tests. These tests use JUnit's assertThrows to verify that custom exceptions are thrown when error conditions are triggered, such as duplicate equipment registrations, member privilege escalation, and out-of-hours bookings. The entire test suite executes in under 1.5 seconds with zero failures."
```

```
========================================================================================
SLIDE 10: BUGS IDENTIFIED, DEBUGGING PROCESS & RESOLUTIONS
========================================================================================
[Visual Layout: Table contrasting initial bug symptoms, root cause analysis, and applied fixes]

| Bug Identified | Root Cause Analysis | Engineering Resolution |
| :--- | :--- | :--- |
| 1. NoSuchElementException on EOF | ConsoleMenu crashed when automated input streams exhausted lines. | Implemented safe readLine() wrapper detecting stream termination gracefully. |
| 2. Role Display Name Mismatch in Tests | Test expected raw enum "ADMIN" while AuthService returned display name "Administrator". | Synchronized test assertion with domain display model; verified message intent. |
| 3. Temporal Overlap Boundary Bug | Standard between() checks miscalculated sessions ending exactly when another started. | Implemented strict interval overlap: this.start < other.end && other.start < this.end. |
| 4. Dynamic In-Memory UUIDs | Random UUIDs generated for tickets prevented hardcoded bot input scripts. | Engineered dynamic lambda suppliers in DemoBot querying live facade state. |

SPEAKER NOTES (8:15 - 9:00):
"Testing is invaluable not only for verification, but for exposing subtle design flaws. During development, we identified four key challenges. First, our console scanner threw NoSuchElementException when fed scripted inputs from a closed stream; we solved this by implementing a safe readLine() wrapper with stream exhaustion guards. Second, an assertion failure occurred during robustness testing because our exception message used the display name 'Administrator' while the test checked for the raw enum 'ADMIN'. Third, temporal overlap checks initially flagged adjacent sessions as collisions; we resolved this by adopting the strict interval formula startA < endB and startB < endA. Finally, dynamic UUIDs generated for tickets made batch testing difficult, which we resolved by implementing dynamic lambda suppliers in our DemoBot."
```

```
========================================================================================
SLIDE 11: CRITICAL REFLECTION & CARDIFF MET EDGE ATTRIBUTES
========================================================================================
[Visual Layout: Quadrant graphic illustrating the four Cardiff Met EDGE pillars]

1. Digital Skills:
   • Mastery of modern Java 21/22 features (pattern matching switches, records).
   • Advanced application of generics, thread-safe collections, GoF design patterns, and Maven build lifecycles.
2. Ethical Practice:
   • Robust RBAC security protecting customer data privacy.
   • Equipment preventative maintenance safeguards member physical safety.
3. Global Perspectives:
   • Timezone-safe temporal scheduling using java.time (LocalDateTime, DayOfWeek).
4. Entrepreneurial Acumen:
   • Modular architecture designed for multi-facility scaling; reducing overhead via automated preventative maintenance.

Challenging Elements:
• Balancing GoF pattern flexibility against architectural simplicity without over-engineering.

SPEAKER NOTES (9:00 - 9:45):
"Reflecting on the module, this project directly cultivated the four Cardiff Met EDGE pillars. Digital skills were sharpened through modern Java idioms, generics, and automated test orchestration. Ethical awareness was applied by securing user data through role-based access control and protecting member safety through proactive equipment servicing. Global perspectives were fostered through timezone-resilient scheduling using java.time, and entrepreneurial mindset was reflected in our decoupled design, which allows the software to scale across multiple gym branches. The most intellectually challenging aspect was striking the right balance with design patterns—ensuring decoupling without introducing unnecessary boilerplate."
```

```
========================================================================================
SLIDE 12: CONCLUSION & FUTURE ENHANCEMENTS
========================================================================================
[Visual Layout: Summary bullet list and architecture evolution roadmap]

Summary of Achievements:
• Delivered a fully-functional, robust Java prototype for IWFC.
• Implemented 5 GoF Design Patterns (Factory, Builder, Facade, Observer, Strategy).
• Implemented 4 custom checked exceptions with robust error recovery.
• 100% test success rate (26/26 JUnit 5 tests passing).
• Automated End-to-End Simulation Bot with live visual feedback.

Future Enterprise Roadmap:
• Database Persistence: Migrate Repository<T, ID> to Spring Data JPA / PostgreSQL.
• Distributed Messaging: Replace in-memory Observer with Apache Kafka / RabbitMQ.
• Mobile & Web Frontend: Expose RESTful APIs for React Native mobile client.

Thank you! Questions and feedback are welcome.

SPEAKER NOTES (9:45 - 10:00):
"In conclusion, the IWFC prototype demonstrates how advanced object-oriented programming, design patterns, and rigorous defensive programming can solve realistic operational challenges. The system is fully tested, verified by an automated bot, and structured for enterprise scalability. Thank you for your time, and I welcome any questions."
```

---

## Part 2: Detailed 10-Minute Video Presentation Script with Exact Timestamps and Visual Cues

This script provides word-for-word spoken dialogue and precise visual cues for recording your 10-minute video presentation.

```
========================================================================================
TIMECODE: [0:00 - 0:45] (45 Seconds)
SECTION: INTRODUCTION & PROJECT MOTIVATION
VISUAL: Slide 1 (Title Slide & Student Details), Web Camera active in corner.
========================================================================================

SPOKEN DIALOGUE:
"Hello and welcome. My name is [Your Name], student ID [Your Student ID]. Today I am presenting 
my practical software prototype for CMP 7001 Advanced Programming at Cardiff Metropolitan 
University and ICBT Campus.

The topic of this project is the Intelligent Wellness and Fitness Center, or IWFC. 
As commercial gym facilities modernize, they face substantial operational challenges: 
equipment deteriorates unnoticed, class reservations clash, and communication between staff 
and administration breaks down.

In this presentation, I will walk you through the architectural design, object-oriented 
principles, Gang-of-Four design patterns, custom exception handling, and empirical test 
results of a Java prototype engineered to solve these exact problems. 

The video recording link is provided on the first slide of the accompanying presentation, 
and full source code is version-controlled via Git."
```

```
========================================================================================
TIMECODE: [0:45 - 1:45] (60 Seconds)
SECTION: DOMAIN SCENARIO, ACTORS & REQUIREMENTS
VISUAL: Slide 2 (Problem Scenario & Stakeholder Requirements).
========================================================================================

SPOKEN DIALOGUE:
"Let us begin with the problem domain. The IWFC facility operates specialized workout 
zones—including Cardio areas, strength training racks, and group exercise studios. 

To replace disjointed manual logbooks and spreadsheets, our prototype addresses three 
distinct actor classifications:
First, Administrators, who manage equipment inventory, oversee preventative maintenance 
thresholds, assign repair tickets, and maintain global audit records.
Second, Instructors, who schedule fitness classes, reserve specialized equipment, plan 
recurring weekly sessions, and report mechanical breakdowns.
And third, Members, who browse class timetables, check slot capacity in real-time, reserve 
bookings, and receive automated schedule alerts.

A core requirement is ensuring strict Role-Based Access Control so that unauthorized actions—
such as members attempting to inspect administrative logs—are systematically intercepted."
```

```
========================================================================================
TIMECODE: [1:45 - 3:00] (75 Seconds)
SECTION: ARCHITECTURE, CLASS DIAGRAM & POLYMORPHISM
VISUAL: Slide 3 & Slide 4, then switch to IDE displaying Equipment.java and Treadmill.java.
========================================================================================

SPOKEN DIALOGUE:
"Turning to our software architecture: the system is structured into cohesive, decoupled layers. 
[Switch to Slide 3]. 

At the foundation, we establish an Identifiable interface, enabling our generic Repository 
abstraction. Our domain model features two inheritance hierarchies: the User hierarchy, 
specialized into Admin, Instructor, and Member; and the Equipment hierarchy, branching into 
Treadmill, SpinBike, and RowingMachine.

Now let us examine how dynamic polymorphism is applied. [Switch to IDE displaying Equipment.java]. 
In our Equipment base class, we declare an abstract method called calculateWearIndex(). 
Because different gym machinery degrades under distinct physical stresses, each concrete subclass 
overrides this method with its own mechanical wear model. 

For example, in Treadmill.java [Show code], we apply an incline motor stress multiplier of 1.15. 
In SpinBike.java, we calculate wear based on flywheel resistance cycles at 1.05. 
When our inventory service evaluates asset health, it invokes calculateWearIndex() polymorphically 
across a heterogeneous collection of machines. We never need to write brittle 'instanceof' checks, 
fully satisfying the Open/Closed Principle."
```

```
========================================================================================
TIMECODE: [3:00 - 4:30] (90 Seconds)
SECTION: DESIGN PATTERNS IMPLEMENTATION
VISUAL: Slide 5 & Slide 6, then switch to IDE displaying EquipmentFactory.java and IWFCSystemFacade.java.
========================================================================================

SPOKEN DIALOGUE:
"To address the module's criteria, we implemented five Gang-of-Four design patterns across all 
three classic categories.

In the Creational category, we implemented the Factory Method pattern via EquipmentFactory. 
[Show EquipmentFactory.java in IDE]. The factory encapsulates equipment instantiation, ensuring 
that when a treadmill or spin bike is created, it is pre-configured with the correct default 
maintenance threshold hours. Alongside it, we implemented the Fluent Builder pattern in 
SessionBuilder. A fitness session requires eleven configuration attributes; the builder eliminates 
error-prone telescoping constructors and enforces data validation before building the session.

In the Structural category, we engineered the Facade pattern through IWFCSystemFacade. 
[Show IWFCSystemFacade.java in IDE]. The facade provides a unified API that orchestrates our 
underlying services—Equipment, Scheduling, Maintenance, and Authentication—while enforcing 
Role-Based Access Control checks on every method call.

Finally, in the Behavioural category, we implemented both the Observer and Strategy patterns. 
The Observer pattern decouples our notification infrastructure. When machine usage hours 
reach calibrated thresholds, or when maintenance tickets change status, the NotificationSubject 
automatically broadcasts events to registered observers. Meanwhile, the Strategy pattern in 
BookingValidationStrategy encapsulates our scheduling rules—such as operating hours verification 
and studio conflict detection—into interchangeable algorithms."
```

```
========================================================================================
TIMECODE: [4:30 - 5:30] (60 Seconds)
SECTION: CUSTOM EXCEPTION HANDLING & ROBUSTNESS
VISUAL: Slide 7, then switch to IDE displaying InvalidBookingException.java and UnauthorizedAccessException.java.
========================================================================================

SPOKEN DIALOGUE:
"Robustness and defensive programming are enforced through a custom checked exception hierarchy, 
rooted in IWFCException. [Show Slide 7].

Rather than relying on generic runtime exceptions, our system categorizes failures into 
specific domain exceptions:
InvalidBookingException is thrown whenever scheduling boundaries are breached—such as 
double-booking a studio room, allocating equipment that is already faulty, booking outside 
facility operating hours, or exceeding maximum room capacity.
UnauthorizedAccessException enforces security. If a Member attempts to register equipment or 
inspect administrative logs, the system halts the operation and throws this exception.
DuplicateDataException ensures entity uniqueness by intercepting duplicate primary key registrations.

Because these are checked exceptions, the Java compiler forces calling methods to explicitly 
catch and handle each failure mode, ensuring our user interface can report meaningful diagnostic 
messages without abnormal termination."
```

```
========================================================================================
TIMECODE: [5:30 - 7:30] (120 Seconds)
SECTION: LIVE SYSTEM DEMONSTRATION
VISUAL: Switch to Terminal. Run './run-bot.sh --normal'.
========================================================================================

SPOKEN DIALOGUE:
"[Switch to Terminal]. To verify the prototype end-to-end, I will now run our automated simulation 
bot using the command './run-bot.sh'. The bot executes all 29 lifecycle steps with simulated 
typing and real-time visual output.

[Point to Terminal as Scenario 1 runs]:
First, notice the Member scenario. Member David Miller logs in and browses the available sessions. 
Notice step 3: the member deliberately attempts to access the Administrator's Global Maintenance Log. 
The system intercepts the call, and our security layer catches an UnauthorizedAccessException, 
verifying that our RBAC mechanism is working properly. The member then books session SES-03, 
attempts a duplicate booking—which is rejected with InvalidBookingException—and successfully cancels 
a reservation.

[Point to Terminal as Scenario 2 runs]:
Now, Instructor Alex Rivera logs in. The instructor schedules a new boxing session using our 
SessionBuilder. Next, the instructor attempts to schedule a class at 10:30 PM. Notice the error: 
our OperatingHoursStrategy rejects the session because the facility closes at 22:00. 
The instructor then attempts a studio double-booking in Studio B, which is blocked by our 
ResourceConflictStrategy. The instructor then schedules a 4-week recurring Pilates series and 
reports a mechanical breakdown on Treadmill TM-01, transitioning its status to Faulty.

[Point to Terminal as Scenario 3 runs]:
Finally, Administrator Sarah Connor logs in. She inspects the equipment inventory with real-time 
wear indices. She registers a new treadmill via EquipmentFactory, and tests a duplicate ID registration, 
which triggers DuplicateDataException. She logs 5 hours on TM-01. Notice the bright alert: 
the Preventative Maintenance Threshold has been exceeded! She assigns the repair ticket to herself 
and completes it with technician notes. Immediately, TM-01 is reset to Operational and its usage 
counter is zeroed. All events were cleanly logged by our Observer pattern."
```

```
========================================================================================
TIMECODE: [7:30 - 8:30] (60 Seconds)
SECTION: UNIT TESTING & INTENTIONAL FAILING TESTS
VISUAL: Slide 9, then switch to Terminal to execute 'mvn test'.
========================================================================================

SPOKEN DIALOGUE:
"[Switch to Terminal]. Beyond interactive simulation, the prototype is validated by a comprehensive 
JUnit 5 test harness. I will now run 'mvn test'. 

[Execute 'mvn test'].
Notice that all 26 automated unit and integration tests pass cleanly in just over one second.

Our test suite rigorously validates every core component:
SchedulingServiceTest verifies studio double-booking prevention, equipment conflict detection, 
and capacity bounds.
MaintenanceServiceTest confirms that ticket lifecycles correctly transition from Pending to 
Assigned to Completed, and that equipment operational status is restored.
EquipmentServiceTest validates polymorphic wear formulas and preventative alert thresholds.

In direct compliance with the PRAC 1 brief, our RobustnessExceptionTest suite implements intentional 
negative tests using JUnit's assertThrows method. These tests prove that custom exceptions are 
reliably thrown when invalid bookings, privilege escalations, or duplicate registrations occur."
```

```
========================================================================================
TIMECODE: [8:30 - 9:30] (60 Seconds)
SECTION: BUGS IDENTIFIED, DEBUGGING & REFLECTION
VISUAL: Slide 10 & Slide 11 (Bugs & Reflection).
========================================================================================

SPOKEN DIALOGUE:
"Software engineering is an iterative learning process, and we identified and resolved several 
notable challenges during development. [Show Slide 10].

First, when feeding automated inputs into our console scanner, the application threw 
NoSuchElementException upon stream exhaustion. We resolved this by engineering a safe readLine() 
wrapper with EOF boundary detection.

Second, during robustness testing, an assertThrows test failed because our exception check 
searched for the raw enum string 'ADMIN', whereas our domain model returned the localized 
display name 'Administrator'. We resolved this by aligning the test assertions with our domain 
model.

Third, our initial temporal overlap validation flagged adjacent sessions as collisions—for 
example, a class ending at 09:00 and another starting at 09:00. We corrected this by applying 
the strict interval formula: startA is before endB and startB is before endA.

Reflecting on the module [Show Slide 11], this project advanced our understanding of the Cardiff 
Met EDGE attributes: strengthening digital engineering through modern Java and design patterns, 
enforcing ethical user safety through equipment maintenance monitoring, and applying an 
entrepreneurial mindset by building a decoupled architecture ready for enterprise scaling."
```

```
========================================================================================
TIMECODE: [9:30 - 10:00] (30 Seconds)
SECTION: CONCLUSION & CLOSING
VISUAL: Slide 12 (Conclusion & Roadmap), Web Camera active.
========================================================================================

SPOKEN DIALOGUE:
"To conclude: the IWFC prototype demonstrates a robust, maintainable, and type-safe Java solution 
to gym operations. We have successfully implemented five Gang-of-Four design patterns, enforced 
Role-Based Access Control through custom checked exceptions, verified all functionality across 26 
automated unit tests, and showcased the running system through an automated simulation bot.

In future iterations, this architecture can transition smoothly to enterprise scale by swapping 
our generic in-memory repositories with Spring Data JPA and PostgreSQL.

Thank you very much for your time and attention. I am now happy to take any questions."
```

---

## Part 3: Video Recording & Presentation Checklist for the Student

Before recording your video and submitting to Cardiff Met Moodle and ICBT SIS, review this checklist:

1. **Recording Setup:**
   - [ ] Download and install **OBS Studio** (free, open source) or use Zoom / QuickTime Screen Recording.
   - [ ] Ensure microphone audio is crisp and background noise is minimal.
   - [ ] Set your display resolution to 1080p (1920x1080) for clear code readability.
2. **Rehearsal & Timing:**
   - [ ] Practice speaking through the script once with a timer. Target time: **9 minutes 30 seconds to 10 minutes**.
   - [ ] Keep your terminal window open with the command `./run-bot.sh` ready to run.
   - [ ] Keep your IDE (VS Code or IntelliJ) open with `Equipment.java`, `EquipmentFactory.java`, and `IWFCSystemFacade.java` tabbed for quick switching.
3. **Export & Upload:**
   - [ ] Record the video and check playback audio and video synchronization.
   - [ ] Upload the MP4 video to **YouTube** (set visibility to *Unlisted* so only tutors with the link can view) or upload to **student OneDrive** with link sharing set to *"Anyone with the link can view"*.
   - [ ] Copy the video link and paste it into **Slide 1** of your PowerPoint presentation.
4. **Final Submission:**
   - [ ] Save the PowerPoint presentation as a **PDF document** for Moodle Turnitin submission.
   - [ ] Upload the softcopy Word document to **ICBT SIS** (`www.icbtsis.lk`).
