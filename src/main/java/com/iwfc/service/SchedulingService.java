package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.scheduling.Booking;
import com.iwfc.model.scheduling.BookingStatus;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.user.UserRole;
import com.iwfc.pattern.behavioural.observer.NotificationEvent;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.pattern.behavioural.strategy.BookingValidationStrategy;
import com.iwfc.pattern.behavioural.strategy.OperatingHoursValidationStrategy;
import com.iwfc.pattern.behavioural.strategy.ResourceConflictValidationStrategy;
import com.iwfc.pattern.creational.SessionBuilder;
import com.iwfc.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service orchestrating fitness session scheduling, recurring class management,
 * member reservations, and strict collision validation using the Strategy pattern.
 */
public class SchedulingService {

    private final Repository<FitnessSession, String> sessionRepository;
    private final Repository<Booking, String> bookingRepository;
    private final Repository<Equipment, String> equipmentRepository;
    private final NotificationSubject notificationSubject;
    private final List<BookingValidationStrategy> validationStrategies = new ArrayList<>();

    public SchedulingService(Repository<FitnessSession, String> sessionRepository,
                             Repository<Booking, String> bookingRepository,
                             Repository<Equipment, String> equipmentRepository,
                             NotificationSubject notificationSubject) {
        this.sessionRepository = sessionRepository;
        this.bookingRepository = bookingRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationSubject = notificationSubject;

        // Register default validation strategies
        this.validationStrategies.add(new OperatingHoursValidationStrategy());
        this.validationStrategies.add(new ResourceConflictValidationStrategy());
    }

    public void addValidationStrategy(BookingValidationStrategy strategy) {
        if (strategy != null) {
            this.validationStrategies.add(strategy);
        }
    }

    /**
     * Schedules a new fitness session, enforcing operating hours and resource conflict validations.
     *
     * @param session session to schedule
     * @return persisted FitnessSession
     * @throws InvalidBookingException if scheduling constraints are violated
     * @throws DuplicateDataException  if session ID already exists
     */
    public FitnessSession scheduleSession(FitnessSession session)
            throws InvalidBookingException, DuplicateDataException {
        // Execute all registered validation strategies
        for (BookingValidationStrategy strategy : validationStrategies) {
            strategy.validate(session, sessionRepository, equipmentRepository);
        }
        return sessionRepository.save(session);
    }

    /**
     * Enhancement: Schedules recurring weekly sessions for a specified number of consecutive weeks.
     *
     * @param baseSession the template session
     * @param weeksAhead  number of weeks to generate
     * @return list of successfully scheduled recurring sessions
     * @throws InvalidBookingException if any scheduled occurrence conflicts
     * @throws DuplicateDataException  if an ID collision occurs
     */
    public List<FitnessSession> scheduleRecurringWeeklySession(FitnessSession baseSession, int weeksAhead)
            throws InvalidBookingException, DuplicateDataException {
        List<FitnessSession> scheduled = new ArrayList<>();
        baseSession.setRecurring(true);
        baseSession.setRecurringDay(baseSession.getStartTime().getDayOfWeek());

        // First schedule week 0
        FitnessSession initial = scheduleSession(baseSession);
        scheduled.add(initial);

        // Schedule subsequent weeks
        for (int i = 1; i <= weeksAhead; i++) {
            LocalDateTime nextStart = baseSession.getStartTime().plusWeeks(i);
            LocalDateTime nextEnd = baseSession.getEndTime().plusWeeks(i);
            String recurringId = String.format("%s-W%d", baseSession.getId(), i);

            FitnessSession weeklySession = new SessionBuilder()
                    .withId(recurringId)
                    .withTitle(baseSession.getTitle())
                    .withSessionType(baseSession.getSessionType())
                    .withStudioLocation(baseSession.getStudioLocation())
                    .withInstructorId(baseSession.getInstructorId())
                    .withTimes(nextStart, nextEnd)
                    .withMaxCapacity(baseSession.getMaxCapacity())
                    .withRequiredEquipment(baseSession.getRequiredEquipmentIds())
                    .asRecurring(baseSession.getRecurringDay())
                    .build();

            FitnessSession saved = scheduleSession(weeklySession);
            scheduled.add(saved);
        }

        return scheduled;
    }

    /**
     * Books a slot for a member in a specific session.
     *
     * @param sessionId session ID
     * @param memberId  member ID
     * @return confirmed Booking
     * @throws EntityNotFoundException if session is not found
     * @throws InvalidBookingException if session is full, member is already booked, or has time overlap
     */
    public Booking bookSession(String sessionId, String memberId)
            throws EntityNotFoundException, InvalidBookingException {
        FitnessSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Session with ID '%s' does not exist.", sessionId)));

        // Retrieve existing active bookings for this session
        List<Booking> sessionBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getSessionId().equals(sessionId) && b.isActive())
                .collect(Collectors.toList());

        // Check 1: Capacity Limit
        if (sessionBookings.size() >= session.getMaxCapacity()) {
            throw new InvalidBookingException(String.format(
                    "Booking failed: Session '%s' has reached maximum capacity (%d/%d slots filled).",
                    session.getTitle(), sessionBookings.size(), session.getMaxCapacity()
            ));
        }

        // Check 2: Already booked for this session
        boolean alreadyBooked = sessionBookings.stream()
                .anyMatch(b -> b.getMemberId().equalsIgnoreCase(memberId));
        if (alreadyBooked) {
            throw new InvalidBookingException(String.format(
                    "Booking failed: Member '%s' is already booked for session '%s'.",
                    memberId, session.getTitle()
            ));
        }

        // Check 3: Member schedule collision with another active session
        List<Booking> memberBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getMemberId().equalsIgnoreCase(memberId) && b.isActive())
                .collect(Collectors.toList());

        for (Booking existingBooking : memberBookings) {
            FitnessSession otherSession = sessionRepository.findById(existingBooking.getSessionId()).orElse(null);
            if (otherSession != null && otherSession.overlapsWith(session.getStartTime(), session.getEndTime())) {
                throw new InvalidBookingException(String.format(
                        "Member schedule conflict: Member '%s' already has a confirmed booking for '%s' during %s to %s.",
                        memberId, otherSession.getTitle(), otherSession.getStartTime(), otherSession.getEndTime()
                ));
            }
        }

        String bookingId = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking(bookingId, sessionId, memberId);

        try {
            bookingRepository.save(booking);
        } catch (DuplicateDataException e) {
            throw new InvalidBookingException("Failed to generate unique booking reservation: " + e.getMessage(), e);
        }

        // Notify member and staff
        NotificationEvent event = new NotificationEvent(
                NotificationEvent.EventType.SESSION_BOOKING_CONFIRMED,
                "Booking Confirmed",
                String.format("Booking %s confirmed for Member '%s' in session '%s' at %s.",
                        bookingId, memberId, session.getTitle(), session.getStudioLocation()),
                UserRole.MEMBER
        );
        notificationSubject.notifyObservers(event);

        return booking;
    }

    /**
     * Cancels an existing booking.
     *
     * @param bookingId booking ID
     * @return updated Booking
     * @throws EntityNotFoundException if booking does not exist
     */
    public Booking cancelBooking(String bookingId) throws EntityNotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Booking with ID '%s' does not exist.", bookingId)));

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.update(booking);
    }

    public List<FitnessSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public List<Booking> getBookingsForMember(String memberId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getMemberId().equalsIgnoreCase(memberId))
                .collect(Collectors.toList());
    }

    public List<Booking> getActiveBookingsForSession(String sessionId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getSessionId().equals(sessionId) && b.isActive())
                .collect(Collectors.toList());
    }
}
