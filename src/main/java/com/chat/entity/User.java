package com.chat.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private Long id;
    private String email;
    private String displayName;
    private String password;
    private boolean isOnline;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;

    public User() {}

    public User(String email) {
        this.email = email;
        this.displayName = email.split("@")[0];
        this.isOnline = true;
        this.lastSeen = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public User(Long id, String email, String displayName, boolean isOnline, LocalDateTime lastSeen, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPassword() { return password; }
    public boolean isOnline() { return isOnline; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setPassword(String password) { this.password = password; }
    public void setOnline(boolean online) { this.isOnline = online; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{email='" + email + "', displayName='" + displayName + "', isOnline=" + isOnline + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}