package com.chat.service;

import com.chat.entity.User;
import com.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(String email) {
        try {
            if (!isValidEmail(email)) {
                return false;
            }
            userRepository.createUserIfNotExists(email);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public boolean setUserOnline(String email, boolean isOnline) {
        try {
            userRepository.setUserOnline(email, isOnline);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String email, String displayName, String password) {
        try {
            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            User user = new User();
            user.setEmail(email);
            user.setDisplayName(displayName);
            user.setPassword(password); // Assume password hashing in repository
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String email, String password) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return userRepository.verifyPassword(user, password);
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public List<User> getOnlineUsers() {
        return userRepository.getOnlineUsers();
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}