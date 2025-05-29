package com.chat;

import com.chat.entity.User;
import com.chat.service.MessageService;
import com.chat.service.UserService;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static final Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private int port;
    private UserService userService;
    private MessageService messageService;

    public ChatServer() {
        this.userService = new UserService();
        this.messageService = new MessageService(new com.chat.repository.ChatMessageRepository());
        this.port = findAvailablePort();
    }

    private int findAvailablePort() {
        // Recherche d'un port disponible à partir de 12345
        for (int testPort = 12345; testPort <= 65535; testPort++) {
            try (ServerSocket testSocket = new ServerSocket(testPort)) {
                System.out.println("Port " + testPort + " est disponible");
                savePortToFile(testPort);
                return testPort;
            } catch (IOException e) {
                // Port occupé, essayer le suivant
            }
        }
        throw new RuntimeException("Aucun port disponible trouvé");
    }

    private void savePortToFile(int port) {
        try {
            Files.createDirectories(Paths.get("runtime"));
            Files.write(Paths.get("runtime/current-port.txt"), String.valueOf(port).getBytes());
            System.out.println("Port " + port + " sauvegardé dans runtime/current-port.txt");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du port: " + e.getMessage());
        }
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("=== SERVEUR DE CHAT DÉMARRÉ ===");
            System.out.println("Port: " + port);
            System.out.println("Adresse: localhost:" + port);
            System.out.println("En attente de connexions...");
            System.out.println("=====================================");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            connectedClients.values().forEach(ClientHandler::disconnect);
            connectedClients.clear();
            System.out.println("Serveur arrêté avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientEmail;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // Authentification
                if (!authenticateUser()) {
                    return;
                }
                
                connectedClients.put(clientEmail, this);
                userService.setUserOnline(clientEmail, true);
                System.out.println("Utilisateur connecté: " + clientEmail);
                
                // Écouter les messages
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    handleMessage(inputLine);
                }
                
            } catch (IOException e) {
                System.out.println("Utilisateur déconnecté: " + clientEmail);
            } finally {
                cleanup();
            }
        }

        private boolean authenticateUser() throws IOException {
            String email = in.readLine();
            
            if (!isValidEmail(email)) {
                out.println("ERROR: Format d'email invalide");
                return false;
            }
            
            try {
                userService.createUser(email);
                clientEmail = email;
                out.println("SUCCESS: Authentification réussie");
                return true;
            } catch (Exception e) {
                out.println("ERROR: " + e.getMessage());
                return false;
            }
        }

        private void handleMessage(String input) {
            if (input.startsWith("MSG:")) {
                String[] parts = input.substring(4).split(":", 2);
                if (parts.length == 2) {
                    String receiver = parts[0];
                    String content = parts[1];
                    
                    // Vérifier que le destinataire existe
                    if (!isValidEmail(receiver)) {
                        out.println("ERROR: Email destinataire invalide");
                        return;
                    }
                    
                    // Sauvegarder le message
                    com.chat.entity.ChatMessage chatMessage = new com.chat.entity.ChatMessage();
                    chatMessage.setSenderEmail(clientEmail);
                    chatMessage.setReceiverEmail(receiver);
                    chatMessage.setContent(content);
                    chatMessage.setTimestamp(java.time.LocalDateTime.now());
                    messageService.saveMessage(chatMessage);
                    
                    // Transférer le message au destinataire
                    ClientHandler receiverHandler = connectedClients.get(receiver);
                    if (receiverHandler != null) {
                        receiverHandler.out.println("MESSAGE:" + clientEmail + ":" + content);
                        System.out.println("Message transmis: " + clientEmail + " -> " + receiver);
                    } else {
                        System.out.println("Destinataire hors ligne: " + receiver);
                        out.println("INFO: Destinataire hors ligne, message sauvegardé");
                    }
                }
            }
        }

        public void disconnect() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
            }
        }

        private void cleanup() {
            connectedClients.remove(clientEmail);
            if (clientEmail != null) {
                userService.setUserOnline(clientEmail, false);
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Erreur fermeture socket: " + e.getMessage());
            }
        }

        private boolean isValidEmail(String email) {
            return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // Gestionnaire d'arrêt propre
        Runtime.getRuntime().addShutdownHook(new Thread(server::stopServer));
        
        server.startServer();
    }
}