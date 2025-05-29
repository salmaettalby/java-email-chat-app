package com.chat.repository;

import com.chat.entity.Contact;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {
    private static final String DB_URL = "jdbc:sqlite:chat.db";

    public ContactRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    telephone TEXT,
                    adresse TEXT
                )
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
        }
    }

    public void create(Contact contact) throws SQLException {
        String sql = "INSERT INTO contacts (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, contact.getNom());
            pstmt.setString(2, contact.getPrenom());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getTelephone());
            pstmt.setString(5, contact.getAdresse());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contact.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Contact findById(int id) throws SQLException {
        String sql = "SELECT * FROM contacts WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Contact(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                    );
                }
            }
        }
        return null;
    }

    public List<Contact> findAll() throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts ORDER BY nom, prenom";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                contacts.add(new Contact(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("adresse")
                ));
            }
        }
        return contacts;
    }

    public void update(Contact contact) throws SQLException {
        String sql = "UPDATE contacts SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, contact.getNom());
            pstmt.setString(2, contact.getPrenom());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getTelephone());
            pstmt.setString(5, contact.getAdresse());
            pstmt.setInt(6, contact.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}