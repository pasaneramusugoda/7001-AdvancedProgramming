package com.iwfc.pattern.creational;

import com.iwfc.common.Constants;
import com.iwfc.model.scheduling.FitnessSession;
import com.iwfc.model.scheduling.SessionType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Creational Design Pattern: Builder Pattern.
 * Provides a flexible, readable, step-by-step mechanism to construct complex {@link FitnessSession} instances.
 */
public class SessionBuilder {

    private String id;
    private String title;
    private SessionType sessionType = SessionType.HIIT;
    private String studioLocation = "Studio A";
    private String instructorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxCapacity = Constants.DEFAULT_STUDIO_CAPACITY;
    private final List<String> requiredEquipmentIds = new ArrayList<>();
    private boolean recurring = false;
    private DayOfWeek recurringDay = null;

    public SessionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public SessionBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public SessionBuilder withSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public SessionBuilder withStudioLocation(String studioLocation) {
        this.studioLocation = studioLocation;
        return this;
    }

    public SessionBuilder withInstructorId(String instructorId) {
        this.instructorId = instructorId;
        return this;
    }

    public SessionBuilder withTimes(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public SessionBuilder withMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public SessionBuilder addRequiredEquipment(String equipmentId) {
        if (equipmentId != null && !equipmentId.isBlank()) {
            this.requiredEquipmentIds.add(equipmentId.trim());
        }
        return this;
    }

    public SessionBuilder withRequiredEquipment(List<String> equipmentIds) {
        if (equipmentIds != null) {
            this.requiredEquipmentIds.clear();
            this.requiredEquipmentIds.addAll(equipmentIds);
        }
        return this;
    }

    public SessionBuilder asRecurring(DayOfWeek dayOfWeek) {
        this.recurring = true;
        this.recurringDay = dayOfWeek;
        return this;
    }

    /**
     * Builds and returns the validated FitnessSession instance.
     *
     * @return constructed FitnessSession
     */
    public FitnessSession build() {
        return new FitnessSession(
                id,
                title,
                sessionType,
                studioLocation,
                instructorId,
                startTime,
                endTime,
                maxCapacity,
                requiredEquipmentIds,
                recurring,
                recurringDay
        );
    }
}
