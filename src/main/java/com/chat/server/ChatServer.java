package com.chat.server;

import com.chat.common.Message;
import com.chat.entity.ChatMessage;
import com.chat.entity.User;
import com.chat.service.MessageService;
import com.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatServer {
    @Value("${chat.server.port:12345}")
    private int port;

    private static final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("🚀 Chat Server started on port " + port);
            System.out.println("📧 Using email addresses as user identifiers");
            System.out.println("💾 Database connection established");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            connectedClients.values().forEach(ClientHandler::disconnect);
            connectedClients.clear();
            System.out.println("Server stopped successfully");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    public void broadcastUserList() {
        List<User> onlineUsers = userService.getOnlineUsers();
        Message userListMessage = new Message("SERVER", "", "USER_LIST:" + formatUserList(onlineUsers));

        connectedClients.values().forEach(client -> {
            try {
                client.sendMessage(userListMessage);
            } catch (IOException e) {
                System.err.println("Error broadcasting user list: " + e.getMessage());
            }
        });
    }

    private String formatUserList(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.getEmail()).append(",").append(user.getDisplayName()).append(";");
        }
        return sb.toString();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String clientEmail;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                // Handle authentication
                if (!authenticateUser()) {
                    return;
                }

                connectedClients.put(clientEmail, this);
                userService.setUserOnline(clientEmail, true);
                System.out.println("✅ User connected: " + clientEmail);

                // Send offline messages
                sendOfflineMessages();

                // Broadcast updated user list
                broadcastUserList();

                // Listen for messages
                while (true) {
                    Message message = (Message) in.readObject();
                    handleMessage(message);
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("❌ User disconnected: " + clientEmail);
            } finally {
                cleanup();
            }
        }

        private boolean authenticateUser() throws IOException, ClassNotFoundException {
            // Read authentication data
            String authData = (String) in.readObject();
            String[] parts = authData.split(":");

            if (parts.length < 2) {
                out.writeObject("ERROR: Invalid authentication format");
                return false;
            }

            String email = parts[0];
            String password = parts[1];
            boolean isRegistration = parts.length > 2 && "REGISTER".equals(parts[2]);

            if (!isValidEmail(email)) {
                out.writeObject("ERROR: Invalid email format");
                return false;
            }

            try {
                if (isRegistration) {
                    String displayName = parts.length > 3 ? parts[3] : email.split("@")[0];
                    userService.registerUser(email, displayName, password);
                    out.writeObject("SUCCESS: User registered successfully");
                } else {
                    userService.authenticateUser(email, password);
                    out.writeObject("SUCCESS: Authentication successful");
                }

                clientEmail = email;
                return true;

            } catch (Exception e) {
                out.writeObject("ERROR: " + e.getMessage());
                return false;
            }
        }

        private void sendOfflineMessages() throws IOException {
            List<ChatMessage> undelivered = messageService.getUndeliveredMessages(clientEmail);
            for (ChatMessage chatMsg : undelivered) {
                Message msg = new Message(chatMsg.getSenderEmail(), chatMsg.getReceiverEmail(), chatMsg.getContent());
                out.writeObject(msg);
                out.flush();
                messageService.markAsDelivered(chatMsg.getId());
            }
            if (!undelivered.isEmpty()) {
                System.out.println("📬 " + undelivered.size() + " offline messages sent to " + clientEmail);
            }
        }

        private void handleMessage(Message message) {
            try {
                // Create ChatMessage object
                ChatMessage chatMessage = new ChatMessage(
                    UUID.randomUUID().toString(),
                    message.getSenderEmail(),
                    message.getReceiverEmail(),
                    message.getContent()
                );

                // Save to database
                messageService.saveMessage(chatMessage);

                // Forward message to recipient
                ClientHandler receiverHandler = connectedClients.get(message.getReceiverEmail());
                if (receiverHandler != null) {
                    receiverHandler.sendMessage(message);
                    messageService.markAsDelivered(chatMessage.getId());
                    System.out.println("📤 Message delivered: " + message.getSenderEmail() + " -> " + message.getReceiverEmail());
                } else {
                    System.out.println("📥 Message stored offline for: " + message.getReceiverEmail());
                }
            } catch (Exception e) {
                System.err.println("Error handling message: " + e.getMessage());
            }
        }

        public void sendMessage(Message message) throws IOException {
            out.writeObject(message);
            out.flush();
        }

        public void disconnect() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error disconnecting client: " + e.getMessage());
            }
        }

        private void cleanup() {
            connectedClients.remove(clientEmail);
            if (clientEmail != null) {
                userService.setUserOnline(clientEmail, false);
                broadcastUserList();
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }

        private boolean isValidEmail(String email) {
            return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        }
    }
}