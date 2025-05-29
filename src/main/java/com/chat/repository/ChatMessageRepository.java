package com.chat.repository;

import com.chat.entity.ChatMessage;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageRepository {
    private static final String DB_URL = "jdbc:sqlite:chat.db";

    public ChatMessageRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS chat_messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender_email TEXT NOT NULL,
                    receiver_email TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    delivered BOOLEAN DEFAULT FALSE,
                    read_by_receiver BOOLEAN DEFAULT FALSE
                )
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error initializing chat_messages database: " + e.getMessage());
        }
    }

    public void save(ChatMessage message) throws SQLException {
        String sql = "INSERT INTO chat_messages (sender_email, receiver_email, content, timestamp, delivered, read_by_receiver) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, message.getSenderEmail());
            pstmt.setString(2, message.getReceiverEmail());
            pstmt.setString(3, message.getContent());
            pstmt.setTimestamp(4, Timestamp.valueOf(message.getTimestamp()));
            pstmt.setBoolean(5, message.isDelivered());
            pstmt.setBoolean(6, message.isReadByReceiver());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public ChatMessage findById(Long id) throws SQLException {
        String sql = "SELECT * FROM chat_messages WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createMessageFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<ChatMessage> findAll() throws SQLException {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages ORDER BY timestamp DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                messages.add(createMessageFromResultSet(rs));
            }
        }
        return messages;
    }

    public List<ChatMessage> findMessagesBetweenUsers(String user1, String user2, int limit) throws SQLException {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE " +
                     "(sender_email = ? AND receiver_email = ?) OR " +
                     "(sender_email = ? AND receiver_email = ?) " +
                     "ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            pstmt.setInt(5, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(createMessageFromResultSet(rs));
                }
            }
        }
        return messages;
    }

    public List<ChatMessage> findUndeliveredByReceiver(String receiverEmail) throws SQLException {
        List<ChatMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM chat_messages WHERE receiver_email = ? AND delivered = FALSE ORDER BY timestamp";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, receiverEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(createMessageFromResultSet(rs));
                }
            }
        }
        return messages;
    }

    private ChatMessage createMessageFromResultSet(ResultSet rs) throws SQLException {
        ChatMessage message = new ChatMessage();
        message.setId(rs.getLong("id"));
        message.setSenderEmail(rs.getString("sender_email"));
        message.setReceiverEmail(rs.getString("receiver_email"));
        message.setContent(rs.getString("content"));
        message.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        message.setDelivered(rs.getBoolean("delivered"));
        message.setReadByReceiver(rs.getBoolean("read_by_receiver"));
        return message;
    }
}