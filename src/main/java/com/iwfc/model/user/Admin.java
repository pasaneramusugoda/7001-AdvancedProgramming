package com.iwfc.model.user;

/**
 * Represents an IWFC Administrator responsible for inventory, staff, and maintenance management.
 */
public class Admin extends User {

    private String department;

    public Admin(String id, String name, String email, String department) {
        super(id, name, email, UserRole.ADMIN);
        this.department = department != null ? department.trim() : "Operations";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "Operations";
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Admin Profile: %s (%s) | Dept: %s | Permissions: Full System Control",
                getName(), getId(), department);
    }
}
