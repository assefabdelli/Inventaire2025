package com.example.demo.service;

import com.example.demo.entite.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to manage the current user context
 * In a real application, this would integrate with Spring Security's SecurityContext
 */
@Service
public class UserContextService {

    @Autowired
    private UserRepository userRepository;

    // In a real app, this would come from SecurityContext
    private ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public void setCurrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public Long getCurrentUserId() {
        return currentUserId.get();
    }

    public User getCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("No user logged in");
        }
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isAdmin() {
        try {
            User user = getCurrentUser();
            return user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isSuperAdmin() {
        try {
            User user = getCurrentUser();
            return user.getRole() == Role.SUPER_ADMIN;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public Long getCurrentUserDepartmentId() {
        try {
            User user = getCurrentUser();
            if (user.getDepartment() != null) {
                return user.getDepartment().getId();
            }
            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    public void clear() {
        currentUserId.remove();
    }
}

