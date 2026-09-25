package com.iwfc.service;

import com.iwfc.exception.EntityNotFoundException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.user.User;
import com.iwfc.model.user.UserRole;
import com.iwfc.repository.Repository;

import java.util.Optional;

/**
 * Service managing user authentication context and Role-Based Access Control (RBAC) verification.
 */
public class AuthService {

    private final Repository<User, String> userRepository;
    private User currentUser;

    public AuthService(Repository<User, String> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user by their unique ID.
     *
     * @param userId user identifier
     * @return authenticated User
     * @throws EntityNotFoundException if user is not found
     */
    public User login(String userId) throws EntityNotFoundException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException(String.format("Authentication failed: User with ID '%s' not found.", userId));
        }
        this.currentUser = userOpt.get();
        return this.currentUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Enforces RBAC permissions. Throws UnauthorizedAccessException if requirement is unmet.
     *
     * @param requiredRole role required to perform operation
     * @throws UnauthorizedAccessException if no user is logged in or user lacks required role
     */
    public void requireRole(UserRole requiredRole) throws UnauthorizedAccessException {
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Access denied: No active user session. Please log in.");
        }
        if (currentUser.getRole() != requiredRole) {
            throw new UnauthorizedAccessException(String.format(
                    "Access denied: Operation requires [%s] role, but current user '%s' holds [%s] role.",
                    requiredRole.getDisplayName(), currentUser.getName(), currentUser.getRole().getDisplayName()
            ));
        }
    }
}
