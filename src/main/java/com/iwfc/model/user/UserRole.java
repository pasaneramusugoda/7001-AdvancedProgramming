package com.iwfc.model.user;

/**
 * Enumeration of system roles supporting Role-Based Access Control (RBAC).
 */
public enum UserRole {
    ADMIN("Administrator"),
    INSTRUCTOR("Instructor"),
    MEMBER("Member");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
