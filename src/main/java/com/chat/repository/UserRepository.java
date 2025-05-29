package com.chat.repository;

import com.chat.entity.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String DB_URL = "jdbc:sqlite:chat.db";

    public UserRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    display_name TEXT NOT NULL,
                    password TEXT,
                    is_online BOOLEAN DEFAULT FALSE,
                    last_seen DATETIME DEFAULT CURRENT_TIMESTAMP,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error initializing users database: " + e.getMessage());
        }
    }

    public void createUserIfNotExists(String email) throws SQLException {
        String sql = "INSERT OR IGNORE INTO users (email, display_name) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String displayName = email.split("@")[0];
            pstmt.setString(1, email);
            pstmt.setString(2, displayName);
            pstmt.executeUpdate();
        }
    }

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (email, display_name, password) VALUES (?, ?, ?) " +
                     "ON CONFLICT(email) DO UPDATE SET display_name = ?, password = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String hashedPassword = user.getPassword() != null ? BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()) : null;
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getDisplayName());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getDisplayName());
            pstmt.setString(5, hashedPassword);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public boolean verifyPassword(User user, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE email = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getEmail());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword != null && BCrypt.checkpw(password, storedPassword);
                }
            }
        }
        return false;
    }

    public void setUserOnline(String email, boolean isOnline) throws SQLException {
        String sql = "UPDATE users SET is_online = ?, last_seen = CURRENT_TIMESTAMP WHERE email = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isOnline);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    public List<User> getOnlineUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_online = TRUE ORDER BY display_name";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving online users: " + e.getMessage());
        }
        return users;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY display_name";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setDisplayName(rs.getString("display_name"));
        user.setPassword(rs.getString("password"));
        user.setOnline(rs.getBoolean("is_online"));
        user.setLastSeen(rs.getTimestamp("last_seen").toLocalDateTime());
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}