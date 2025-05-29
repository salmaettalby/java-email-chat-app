package com.chat.service;

import com.chat.entity.ChatMessage;
import com.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final ChatMessageRepository messageRepository;

    public MessageService(ChatMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public boolean saveMessage(ChatMessage message) {
        try {
            messageRepository.save(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving message: " + e.getMessage());
            return false;
        }
    }

    public List<ChatMessage> getMessageHistory(String user1, String user2, int limit) {
        try {
            return messageRepository.findMessagesBetweenUsers(user1, user2, limit);
        } catch (Exception e) {
            System.err.println("Error retrieving message history: " + e.getMessage());
            return List.of();
        }
    }

    public List<ChatMessage> getAllMessages() {
        try {
            return messageRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error retrieving all messages: " + e.getMessage());
            return List.of();
        }
    }

    public List<ChatMessage> getUndeliveredMessages(String receiverEmail) {
        try {
            return messageRepository.findUndeliveredByReceiver(receiverEmail);
        } catch (Exception e) {
            System.err.println("Error retrieving undelivered messages: " + e.getMessage());
            return List.of();
        }
    }

    public void markAsDelivered(Long messageId) {
        try {
            ChatMessage message = messageRepository.findById(messageId);
            if (message != null) {
                message.setDelivered(true);
                messageRepository.save(message);
            }
        } catch (Exception e) {
            System.err.println("Error marking message as delivered: " + e.getMessage());
        }
    }
}