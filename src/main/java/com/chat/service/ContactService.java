package com.chat.service;

import com.chat.entity.Contact;
import com.chat.repository.ContactRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService() {
        this.contactRepository = new ContactRepository();
    }

    public boolean addContact(Contact contact) {
        try {
            if (contact.getNom() == null || contact.getNom().trim().isEmpty()) {
                return false;
            }
            if (contact.getPrenom() == null || contact.getPrenom().trim().isEmpty()) {
                return false;
            }
            if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
                return false;
            }
            if (!isValidEmail(contact.getEmail())) {
                return false;
            }

            contactRepository.create(contact);
            return true;
        } catch (SQLException e) {
            
            System.err.println("Erreur lors de l'ajout du contact: " + e.getMessage());




            return false;
        }
    }

    public boolean updateContact(Contact contact) {
        try {
            Contact existingContact = contactRepository.findById(contact.getId());
            if (existingContact == null) {
                return false;
            }

            if (contact.getNom() == null || contact.getNom().trim().isEmpty()) {
                return false;
            }
            if (contact.getPrenom() == null || contact.getPrenom().trim().isEmpty()) {
                return false;
            }
            if (contact.getEmail() == null || contact.getEmail().trim().isEmpty()) {
                return false;
            }
            if (!isValidEmail(contact.getEmail())) {
                return false;
            }

            contactRepository.update(contact);
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du contact: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteContact(int id) {
        try {
            Contact contact = contactRepository.findById(id);
            if (contact == null) {
                return false;
            }
            contactRepository.delete(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du contact: " + e.getMessage());
            return false;
        }
    }

    public List<Contact> getAllContacts() {
        try {
            return contactRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des contacts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Contact getContactById(int id) {
        try {
            return contactRepository.findById(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du contact: " + e.getMessage());
            return null;
        }
    }

    public List<Contact> searchContacts(String terme) {
        try {
            List<Contact> tousLesContacts = contactRepository.findAll();
            return tousLesContacts.stream()
                    .filter(contact -> 
                        contact.getNom().toLowerCase().contains(terme.toLowerCase()) ||
                        contact.getPrenom().toLowerCase().contains(terme.toLowerCase()) ||
                        contact.getEmail().toLowerCase().contains(terme.toLowerCase())
                    )
                    .toList();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de contacts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}