package com.iwfc.model.maintenance;

import com.iwfc.common.Identifiable;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity capturing maintenance tickets logged by Instructors and actioned by Administrators.
 */
public class MaintenanceRequest implements Identifiable<String> {

    private final String id;
    private final String equipmentId;
    private final String reportedByInstructorId;
    private String assignedToAdminId;
    private final String issueDescription;
    private MaintenanceUrgency urgency;
    private MaintenanceStatus status;
    private final LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private String resolutionNotes;

    public MaintenanceRequest(String id,
                              String equipmentId,
                              String reportedByInstructorId,
                              String issueDescription,
                              MaintenanceUrgency urgency) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Request ID cannot be empty.");
        if (equipmentId == null || equipmentId.isBlank()) throw new IllegalArgumentException("Equipment ID cannot be empty.");
        if (reportedByInstructorId == null || reportedByInstructorId.isBlank()) {
            throw new IllegalArgumentException("Reporter Instructor ID cannot be empty.");
        }
        if (issueDescription == null || issueDescription.isBlank()) {
            throw new IllegalArgumentException("Issue description cannot be empty.");
        }

        this.id = id.trim();
        this.equipmentId = equipmentId.trim();
        this.reportedByInstructorId = reportedByInstructorId.trim();
        this.issueDescription = issueDescription.trim();
        this.urgency = Objects.requireNonNull(urgency, "Urgency cannot be null.");
        this.status = MaintenanceStatus.PENDING;
        this.reportedAt = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getReportedByInstructorId() {
        return reportedByInstructorId;
    }

    public String getAssignedToAdminId() {
        return assignedToAdminId;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public MaintenanceUrgency getUrgency() {
        return urgency;
    }

    public void setUrgency(MaintenanceUrgency urgency) {
        this.urgency = urgency;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    /**
     * Transitions ticket state to ASSIGNED.
     *
     * @param adminId administrator assigned to oversee the repair
     */
    public void assignTo(String adminId) {
        if (adminId == null || adminId.isBlank()) {
            throw new IllegalArgumentException("Assigned Admin ID cannot be empty.");
        }
        this.assignedToAdminId = adminId.trim();
        this.status = MaintenanceStatus.ASSIGNED;
    }

    /**
     * Transitions ticket state to COMPLETED.
     *
     * @param notes resolution details provided by technician/admin
     */
    public void complete(String notes) {
        this.status = MaintenanceStatus.COMPLETED;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = notes != null ? notes.trim() : "Service completed successfully.";
    }

    /**
     * Cancels the maintenance ticket.
     */
    public void cancel() {
        this.status = MaintenanceStatus.CANCELLED;
        this.resolvedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceRequest that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] Equip: %s | ReportedBy: %s | Status: %s | Urgency: %s | Desc: %s",
                id, equipmentId, reportedByInstructorId, status, urgency, issueDescription);
    }
}
