package com.iwfc.model.user;

/**
 * Represents an IWFC Instructor responsible for scheduling classes and reporting equipment faults.
 */
public class Instructor extends User {

    private String specialization;

    public Instructor(String id, String name, String email, String specialization) {
        super(id, name, email, UserRole.INSTRUCTOR);
        this.specialization = specialization != null ? specialization.trim() : "General Fitness";
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization != null ? specialization.trim() : "General Fitness";
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Instructor Profile: %s (%s) | Specialization: %s | Permissions: Class Scheduling & Maintenance Reporting",
                getName(), getId(), specialization);
    }
}
