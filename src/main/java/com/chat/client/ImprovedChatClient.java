package com.chat.client;

import com.chat.common.Message;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ImprovedChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String userEmail;
    private Consumer<String> messageHandler;
    private Consumer<String> userListHandler;
    private boolean connected = false;

    public ImprovedChatClient(String userEmail, Consumer<String> messageHandler, Consumer<String> userListHandler) {
        this.userEmail = userEmail;
        this.messageHandler = messageHandler;
        this.userListHandler = userListHandler;
    }

    public boolean authenticate(String email, String password, String displayName, boolean isRegistration) {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send authentication data
            String authData = email + ":" + password;
            if (isRegistration) {
                authData += ":REGISTER:" + (displayName.isEmpty() ? email.split("@")[0] : displayName);
            }
            
            out.writeObject(authData);
            out.flush();

            // Read response
            String response = (String) in.readObject();
            
            if (response.startsWith("SUCCESS")) {
                connected = true;
                startMessageListener();
                requestUserList();
                return true;
            } else {
                if (messageHandler != null) {
                    messageHandler.accept("Authentication failed: " + response);
                }
                return false;
            }
            
        } catch (Exception e) {
            if (messageHandler != null) {
                messageHandler.accept("Connection error: " + e.getMessage());
            }
            return false;
        }
    }

    private void startMessageListener() {
        Thread receiverThread = new Thread(() -> {
            try {
                while (connected && socket != null && !socket.isClosed()) {
                    Object received = in.readObject();
                    
                    if (received instanceof Message) {
                        Message message = (Message) received;
                        if (messageHandler != null) {
                            String formattedMessage = String.format("%s → You: %s", 
                                                                   message.getSenderEmail(), 
                                                                   message.getContent());
                            messageHandler.accept(formattedMessage);
                        }
                    } else if (received instanceof String) {
                        String strMessage = (String) received;
                        if (strMessage.startsWith("USER_LIST:")) {
                            String userListData = strMessage.substring("USER_LIST:".length());
                            if (userListHandler != null) {
                                userListHandler.accept(userListData);
                            }
                        } else if (messageHandler != null) {
                            messageHandler.accept(strMessage);
                        }
                    }
                }
            } catch (EOFException e) {
                if (messageHandler != null) {
                    messageHandler.accept("Server disconnected");
                }
            } catch (Exception e) {
                if (connected && messageHandler != null) {
                    messageHandler.accept("Connection lost: " + e.getMessage());
                }
            } finally {
                connected = false;
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    public void sendMessage(String recipientEmail, String content) throws IOException {
        if (!connected || out == null) {
            throw new IOException("Not connected to server");
        }
        
        Message message = new Message(userEmail, recipientEmail, content);
        out.writeObject(message);
        out.flush();
    }

    public void requestUserList() {
        try {
            if (connected && out != null) {
                Message request = new Message(userEmail, "SERVER", "GET_USER_LIST");
                out.writeObject(request);
                out.flush();
            }
        } catch (IOException e) {
            if (messageHandler != null) {
                messageHandler.accept("Failed to request user list: " + e.getMessage());
            }
        }
    }

    public void requestConversationHistory(String contactEmail) {
        try {
            if (connected && out != null) {
                Message request = new Message(userEmail, "SERVER", "GET_HISTORY:" + contactEmail);
                out.writeObject(request);
                out.flush();
            }
        } catch (IOException e) {
            if (messageHandler != null) {
                messageHandler.accept("Failed to load conversation history: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}