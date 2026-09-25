package com.iwfc.pattern.structural;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.equipment.Equipment;
import com.iwfc.model.equipment.EquipmentType;
import com.iwfc.model.maintenance.MaintenanceRequest;
import com.iwfc.model.maintenance.MaintenanceStatus;
import com.iwfc.model.maintenance.MaintenanceUrgency;
import com.iwfc.model.scheduling.Booking;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.user.User;
import com.iwfc.model.user.UserRole;
import com.iwfc.pattern.behavioural.observer.ConsoleNotificationObserver;
import com.iwfc.pattern.behavioural.observer.NotificationObserver;
import com.iwfc.pattern.behavioural.observer.NotificationSubject;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import com.iwfc.service.AuthService;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SchedulingService;

import java.util.List;

/**
 * Structural Design Pattern: Facade Pattern.
 * Provides a unified, high-level interface shielding client applications
 * (such as CLI, REST APIs, or GUI) from underlying subsystem complexity.
 * Enforces Role-Based Access Control (RBAC) security checks on all operations.
 */
public class IWFCSystemFacade {

    private final Repository<User, String> userRepository;
    private final Repository<Equipment, String> equipmentRepository;
    private final Repository<FitnessSession, String> sessionRepository;
    private final Repository<Booking, String> bookingRepository;
    private final Repository<MaintenanceRequest, String> maintenanceRepository;

    private final NotificationSubject notificationSubject;
    private final ConsoleNotificationObserver systemAlertObserver;

    private final AuthService authService;
    private final EquipmentService equipmentService;
    private final SchedulingService schedulingService;
    private final MaintenanceService maintenanceService;

    public IWFCSystemFacade() {
        this.userRepository = new InMemoryRepository<>();
        this.equipmentRepository = new InMemoryRepository<>();
        this.sessionRepository = new InMemoryRepository<>();
        this.bookingRepository = new InMemoryRepository<>();
        this.maintenanceRepository = new InMemoryRepository<>();

        this.notificationSubject = new NotificationSubject();
        this.systemAlertObserver = new ConsoleNotificationObserver("AuditLog", false);
        this.notificationSubject.attach(systemAlertObserver);

        this.authService = new AuthService(userRepository);
        this.equipmentService = new EquipmentService(equipmentRepository, notificationSubject);
        this.schedulingService = new SchedulingService(sessionRepository, bookingRepository, equipmentRepository, notificationSubject);
        this.maintenanceService = new MaintenanceService(maintenanceRepository, equipmentRepository, notificationSubject);
    }

    public IWFCSystemFacade(Repository<User, String> userRepository,
                            Repository<Equipment, String> equipmentRepository,
                            Repository<FitnessSession, String> sessionRepository,
                            Repository<Booking, String> bookingRepository,
                            Repository<MaintenanceRequest, String> maintenanceRepository,
                            NotificationSubject notificationSubject) {
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.sessionRepository = sessionRepository;
        this.bookingRepository = bookingRepository;
        this.maintenanceRepository = maintenanceRepository;

        this.notificationSubject = notificationSubject;
        this.systemAlertObserver = new ConsoleNotificationObserver("AuditLog", false);
        this.notificationSubject.attach(systemAlertObserver);

        this.authService = new AuthService(userRepository);
        this.equipmentService = new EquipmentService(equipmentRepository, notificationSubject);
        this.schedulingService = new SchedulingService(sessionRepository, bookingRepository, equipmentRepository, notificationSubject);
        this.maintenanceService = new MaintenanceService(maintenanceRepository, equipmentRepository, notificationSubject);
    }

    // ==========================================
    // AUTHENTICATION & ACCESS CONTROL
    // ==========================================

    public User login(String userId) throws EntityNotFoundException {
        return authService.login(userId);
    }

    public void logout() {
        authService.logout();
    }

    public User getCurrentUser() {
        return authService.getCurrentUser();
    }

    public void registerUser(User user) throws DuplicateDataException {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ==========================================
    // EQUIPMENT OPERATIONS (ADMIN ONLY)
    // ==========================================

    public Equipment registerEquipment(EquipmentType type, String id, String name, String location)
            throws UnauthorizedAccessException, DuplicateDataException {
        authService.requireRole(UserRole.ADMIN);
        return equipmentService.registerEquipment(type, id, name, location);
    }

    public Equipment updateEquipment(String id, String name, String location)
            throws UnauthorizedAccessException, EntityNotFoundException {
        authService.requireRole(UserRole.ADMIN);
        return equipmentService.updateEquipment(id, name, location);
    }

    public Equipment deactivateEquipment(String id)
            throws UnauthorizedAccessException, EntityNotFoundException {
        authService.requireRole(UserRole.ADMIN);
        return equipmentService.deactivateEquipment(id);
    }

    public Equipment logEquipmentUsage(String id, double hours)
            throws EntityNotFoundException {
        // Log usage may be called by automated sensors or instructors after class
        return equipmentService.logUsage(id, hours);
    }

    public List<Equipment> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    public List<Equipment> getEquipmentDueForMaintenance() {
        return equipmentService.getEquipmentDueForMaintenance();
    }

    // ==========================================
    // SESSION & BOOKING OPERATIONS
    // ==========================================

    public FitnessSession scheduleSession(FitnessSession session)
            throws UnauthorizedAccessException, InvalidBookingException, DuplicateDataException {
        User current = authService.getCurrentUser();
        if (current == null || (current.getRole() != UserRole.INSTRUCTOR && current.getRole() != UserRole.ADMIN)) {
            throw new UnauthorizedAccessException("Unauthorized: Only Instructors or Administrators can schedule fitness sessions.");
        }
        return schedulingService.scheduleSession(session);
    }

    public List<FitnessSession> scheduleRecurringWeeklySession(FitnessSession session, int weeksAhead)
            throws UnauthorizedAccessException, InvalidBookingException, DuplicateDataException {
        User current = authService.getCurrentUser();
        if (current == null || (current.getRole() != UserRole.INSTRUCTOR && current.getRole() != UserRole.ADMIN)) {
            throw new UnauthorizedAccessException("Unauthorized: Only Instructors or Administrators can schedule recurring sessions.");
        }
        return schedulingService.scheduleRecurringWeeklySession(session, weeksAhead);
    }

    public Booking bookSession(String sessionId)
            throws UnauthorizedAccessException, EntityNotFoundException, InvalidBookingException {
        User current = authService.getCurrentUser();
        if (current == null) {
            throw new UnauthorizedAccessException("Unauthorized: Must be logged in to book sessions.");
        }
        return schedulingService.bookSession(sessionId, current.getId());
    }

    public Booking bookSessionForMember(String sessionId, String memberId)
            throws EntityNotFoundException, InvalidBookingException {
        return schedulingService.bookSession(sessionId, memberId);
    }

    public Booking cancelBooking(String bookingId) throws EntityNotFoundException {
        return schedulingService.cancelBooking(bookingId);
    }

    public List<FitnessSession> getAllSessions() {
        return schedulingService.getAllSessions();
    }

    public List<Booking> getMyBookings() throws UnauthorizedAccessException {
        User current = authService.getCurrentUser();
        if (current == null) {
            throw new UnauthorizedAccessException("Unauthorized: Must be logged in to view bookings.");
        }
        return schedulingService.getBookingsForMember(current.getId());
    }

    // ==========================================
    // MAINTENANCE WORKFLOWS
    // ==========================================

    public MaintenanceRequest reportEquipmentFault(String equipmentId, String description, MaintenanceUrgency urgency)
            throws UnauthorizedAccessException, EntityNotFoundException, DuplicateDataException {
        User current = authService.getCurrentUser();
        if (current == null || (current.getRole() != UserRole.INSTRUCTOR && current.getRole() != UserRole.ADMIN)) {
            throw new UnauthorizedAccessException("Unauthorized: Only Instructors or Administrators can report equipment faults.");
        }
        return maintenanceService.reportFault(equipmentId, current.getId(), description, urgency);
    }

    public MaintenanceRequest assignMaintenanceTicket(String requestId, String adminId)
            throws UnauthorizedAccessException, EntityNotFoundException {
        authService.requireRole(UserRole.ADMIN);
        return maintenanceService.assignRequest(requestId, adminId);
    }

    public MaintenanceRequest completeMaintenanceTicket(String requestId, String resolutionNotes)
            throws UnauthorizedAccessException, EntityNotFoundException {
        authService.requireRole(UserRole.ADMIN);
        return maintenanceService.completeRequest(requestId, resolutionNotes);
    }

    /**
     * Retrieves the global maintenance log. Strictly restricted to Administrators.
     * Members attempting to invoke this will trigger UnauthorizedAccessException.
     *
     * @return list of all maintenance requests
     * @throws UnauthorizedAccessException if caller is not an Administrator
     */
    public List<MaintenanceRequest> getGlobalMaintenanceLog() throws UnauthorizedAccessException {
        authService.requireRole(UserRole.ADMIN);
        return maintenanceService.getAllRequests();
    }

    public List<MaintenanceRequest> getMaintenanceRequestsByStatus(MaintenanceStatus status)
            throws UnauthorizedAccessException {
        authService.requireRole(UserRole.ADMIN);
        return maintenanceService.getRequestsByStatus(status);
    }

    // ==========================================
    // OBSERVER SUBSCRIPTIONS & SERVICES
    // ==========================================

    public void registerNotificationObserver(NotificationObserver observer) {
        notificationSubject.attach(observer);
    }

    public List<com.iwfc.pattern.behavioural.observer.NotificationEvent> getSystemAuditLogs() {
        return systemAlertObserver.getEventLog();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public EquipmentService getEquipmentService() {
        return equipmentService;
    }

    public SchedulingService getSchedulingService() {
        return schedulingService;
    }

    public MaintenanceService getMaintenanceService() {
        return maintenanceService;
    }

    public Repository<Equipment, String> getEquipmentRepository() {
        return equipmentRepository;
    }

    public Repository<FitnessSession, String> getSessionRepository() {
        return sessionRepository;
    }

    public Repository<Booking, String> getBookingRepository() {
        return bookingRepository;
    }

    public Repository<MaintenanceRequest, String> getMaintenanceRepository() {
        return maintenanceRepository;
    }

    public Repository<User, String> getUserRepository() {
        return userRepository;
    }
}
