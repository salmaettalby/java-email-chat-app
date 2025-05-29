package com.chat.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private long timestamp;
    private LocalDateTime dateTime;

    public Message() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.dateTime = LocalDateTime.now();
    }

    public Message(String senderEmail, String receiverEmail, String content) {
        this();
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderEmail() { return senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public LocalDateTime getDateTime() { return dateTime; }

    // Setters
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[%s] %s -> %s: %s", 
                           dateTime.format(formatter), 
                           senderEmail, 
                           receiverEmail, 
                           content);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageId != null ? messageId.equals(message.messageId) : message.messageId == null;
    }

    @Override
    public int hashCode() {
        return messageId != null ? messageId.hashCode() : 0;
    }
}