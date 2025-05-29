package com.chat.entity;

import java.time.LocalDateTime;

public class ChatMessage {
    private Long id;
    private String messageId;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private LocalDateTime timestamp;
    private MessageType messageType;
    private boolean isDelivered;
    private boolean isRead;
    private String filePath;
    private boolean isReadByReceiver;
    
    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }

    public ChatMessage() {}

    public ChatMessage(String messageId, String senderEmail, String receiverEmail, String content) {
        this.messageId = messageId;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageType = MessageType.TEXT;
        this.isDelivered = false;
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public boolean isDelivered() { return isDelivered; }
    public void setDelivered(boolean delivered) { this.isDelivered = delivered; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public boolean isReadByReceiver() {
    return isReadByReceiver;
    }

    public void setReadByReceiver(boolean readByReceiver) {
        this.isReadByReceiver = readByReceiver;
    }

    @Override
    public String toString() {
        return String.format("Message{from='%s', to='%s', content='%s', time=%s}",
                senderEmail, receiverEmail, content, timestamp);
    }
}