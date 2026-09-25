package com.iwfc.model.user;

import com.iwfc.common.Identifiable;

import java.util.Objects;

/**
 * Abstract base class representing a registered user in the IWFC ecosystem.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class User implements Identifiable<String> {

    private final String id;
    private String name;
    private String email;
    private final UserRole role;

    protected User(String id, String name, String email, UserRole role) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or empty.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.role = Objects.requireNonNull(role, "User role cannot be null.");
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }
        this.email = email.trim();
    }

    public UserRole getRole() {
        return role;
    }

    /**
     * Polymorphic method implemented by concrete user subtypes to return a contextual summary.
     *
     * @return Summary string of user profile and role-specific details.
     */
    public abstract String getDashboardSummary();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Email: %s", role, id, name, email);
    }
}
