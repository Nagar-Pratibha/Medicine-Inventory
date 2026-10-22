package com.example.Meds.security;

import com.example.Meds.entity.User;
import com.example.Meds.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Guards user-scoped endpoints.
 * <p>
 * The nested routes (/api/users/{userId}/...) take the owning user from the URL,
 * which a client fully controls. Every such endpoint must therefore confirm that
 * the {userId} in the path is the authenticated caller before touching any data.
 * Admins are allowed through so the admin screens keep working.
 */
@Component
public class OwnershipValidator {

    /**
     * The User entity behind the current request. JwtRequestFilter puts our own
     * User entity in as the principal, so this is safe to cast.
     */
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new ForbiddenException("No authenticated user found for this request");
        }
        return user;
    }

    public boolean isAdmin() {
        return currentUser().getRole() == User.Role.ADMIN;
    }

    /**
     * Fails the request unless the caller is the user named in the path, or an admin.
     */
    public void assertCanAccessUser(Integer userId) {
        User current = currentUser();
        if (current.getRole() == User.Role.ADMIN) {
            return;
        }
        if (userId == null || !userId.equals(current.getId())) {
            throw new ForbiddenException("You are not allowed to access data for user " + userId);
        }
    }
}
