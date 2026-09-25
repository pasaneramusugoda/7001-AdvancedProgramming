package com.iwfc.model.scheduling;

import com.iwfc.common.Constants;
import com.iwfc.common.Identifiable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a scheduled fitness class or training session.
 * Managed through the SessionBuilder creational design pattern.
 */
public class FitnessSession implements Identifiable<String> {

    private final String id;
    private String title;
    private SessionType sessionType;
    private String studioLocation;
    private String instructorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxCapacity;
    private final List<String> requiredEquipmentIds;
    private boolean recurring;
    private DayOfWeek recurringDay;

    public FitnessSession(String id,
                          String title,
                          SessionType sessionType,
                          String studioLocation,
                          String instructorId,
                          LocalDateTime startTime,
                          LocalDateTime endTime,
                          int maxCapacity,
                          List<String> requiredEquipmentIds,
                          boolean recurring,
                          DayOfWeek recurringDay) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Session ID cannot be empty.");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Session title cannot be empty.");
        if (studioLocation == null || studioLocation.isBlank()) throw new IllegalArgumentException("Studio location cannot be empty.");
        if (instructorId == null || instructorId.isBlank()) throw new IllegalArgumentException("Instructor ID cannot be empty.");
        if (startTime == null || endTime == null) throw new IllegalArgumentException("Start and End times are required.");
        if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("Session end time must be after start time.");
        if (maxCapacity <= 0) throw new IllegalArgumentException("Capacity must be greater than zero.");

        this.id = id.trim();
        this.title = title.trim();
        this.sessionType = Objects.requireNonNull(sessionType, "Session type cannot be null.");
        this.studioLocation = studioLocation.trim();
        this.instructorId = instructorId.trim();
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity > 0 ? maxCapacity : Constants.DEFAULT_STUDIO_CAPACITY;
        this.requiredEquipmentIds = new ArrayList<>(requiredEquipmentIds != null ? requiredEquipmentIds : Collections.emptyList());
        this.recurring = recurring;
        this.recurringDay = recurringDay;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getStudioLocation() {
        return studioLocation;
    }

    public void setStudioLocation(String studioLocation) {
        this.studioLocation = studioLocation;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<String> getRequiredEquipmentIds() {
        return Collections.unmodifiableList(requiredEquipmentIds);
    }

    public void addRequiredEquipmentId(String equipmentId) {
        if (equipmentId != null && !equipmentId.isBlank() && !requiredEquipmentIds.contains(equipmentId.trim())) {
            requiredEquipmentIds.add(equipmentId.trim());
        }
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public DayOfWeek getRecurringDay() {
        return recurringDay;
    }

    public void setRecurringDay(DayOfWeek recurringDay) {
        this.recurringDay = recurringDay;
    }

    /**
     * Checks if this session overlaps in time with another time window.
     *
     * @param otherStart other window start
     * @param otherEnd   other window end
     * @return true if overlapping
     */
    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return this.startTime.isBefore(otherEnd) && otherStart.isBefore(this.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FitnessSession that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Type: %s | Loc: %s | Time: %s to %s | Cap: %d | Recurring: %s",
                id, title, sessionType, studioLocation, startTime, endTime, maxCapacity, recurring ? recurringDay : "No");
    }
}
