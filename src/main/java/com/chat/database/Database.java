package com.chat.database;

import com.chat.common.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:chat.db";
    private Connection connection;

    public Database() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        initializeTables();
        System.out.println("💾 Base de données SQLite initialisée");
    }

    private void initializeTables() throws SQLException {
        String createMessagesTable = """
            CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sender_email TEXT NOT NULL,
                receiver_email TEXT NOT NULL,
                content TEXT NOT NULL,
                timestamp LONG NOT NULL,
                message_id TEXT UNIQUE NOT NULL
            )
        """;
        
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                email TEXT PRIMARY KEY,
                last_seen LONG NOT NULL,
                status TEXT DEFAULT 'offline'
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMessagesTable);
            stmt.execute(createUsersTable);
        }
    }

    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT OR IGNORE INTO messages (sender_email, receiver_email, content, timestamp, message_id) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, message.getSenderEmail());
            pstmt.setString(2, message.getReceiverEmail());
            pstmt.setString(3, message.getContent());
            pstmt.setLong(4, message.getTimestamp());
            pstmt.setString(5, message.getMessageId());
            pstmt.executeUpdate();
        }
    }

    public List<Message> getMessageHistory(String user1, String user2, int limit) throws SQLException {
        String sql = """
            SELECT sender_email, receiver_email, content, timestamp 
            FROM messages 
            WHERE (sender_email = ? AND receiver_email = ?) 
               OR (sender_email = ? AND receiver_email = ?)
            ORDER BY timestamp DESC 
            LIMIT ?
        """;
        
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            pstmt.setInt(5, limit);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                    rs.getString("sender_email"),
                    rs.getString("receiver_email"),
                    rs.getString("content")
                );
                messages.add(0, msg); // Inverser l'ordre pour chronologique
            }
        }
        return messages;
    }

    public void updateUserStatus(String email, String status) throws SQLException {
        String sql = "INSERT OR REPLACE INTO users (email, last_seen, status) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setLong(2, System.currentTimeMillis());
            pstmt.setString(3, status);
            pstmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
