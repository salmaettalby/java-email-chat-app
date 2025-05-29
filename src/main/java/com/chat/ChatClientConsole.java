package com.chat;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ChatClientConsole {
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String userEmail;
    private boolean isConnected = false;
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== CLIENT DE CHAT - VERSION CONSOLE ===");
        System.out.println("Tapez 'quit' pour quitter à tout moment");
        System.out.println("==========================================");

        while (true) {
            if (!isConnected) {
                if (!connectToServer()) {
                    System.out.println("Impossible de se connecter. Tapez 'quit' pour quitter ou Entrée pour réessayer.");
                    String input = scanner.nextLine();
                    if ("quit".equalsIgnoreCase(input)) {
                        break;
                    }
                    continue;
                }
            }

            System.out.print("Commande (msg/quit/help): ");
            String command = scanner.nextLine().trim();

            if ("quit".equalsIgnoreCase(command)) {
                break;
            } else if ("msg".equalsIgnoreCase(command)) {
                sendMessage();
            } else if ("help".equalsIgnoreCase(command)) {
                showHelp();
            } else {
                System.out.println("Commande inconnue. Tapez 'help' pour l'aide.");
            }
        }

        disconnect();
        System.out.println("Client fermé.");
    }

    private boolean connectToServer() {
        System.out.print("Entrez votre email: ");
        userEmail = scanner.nextLine().trim();

        if (!isValidEmail(userEmail)) {
            System.out.println("Format d'email invalide!");
            return false;
        }

        try {
            int port = readPortFromFile();
            System.out.println("Connexion au serveur sur le port " + port + "...");
            
            socket = new Socket("localhost", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Authentification
            out.println(userEmail);
            String response = in.readLine();

            if (response.startsWith("SUCCESS")) {
                isConnected = true;
                System.out.println("✅ Connecté en tant que: " + userEmail);
                
                // Thread pour recevoir les messages
                Thread receiver = new Thread(this::receiveMessages);
                receiver.setDaemon(true);
                receiver.start();
                
                return true;
            } else {
                System.out.println("❌ Erreur d'authentification: " + response);
                socket.close();
                return false;
            }

        } catch (IOException e) {
            System.out.println("❌ Impossible de se connecter au serveur: " + e.getMessage());
            return false;
        }
    }

    private int readPortFromFile() throws IOException {
        String portFile = "runtime/current-port.txt";
        if (Files.exists(Paths.get(portFile))) {
            String portStr = new String(Files.readAllBytes(Paths.get(portFile))).trim();
            return Integer.parseInt(portStr);
        } else {
            return 12345; // Port par défaut
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                handleReceivedMessage(message);
            }
        } catch (IOException e) {
            System.out.println("\n❌ Connexion perdue avec le serveur.");
            isConnected = false;
        }
    }

    private void handleReceivedMessage(String message) {
        if (message.startsWith("MESSAGE:")) {
            String[] parts = message.substring(8).split(":", 2);
            if (parts.length == 2) {
                String sender = parts[0];
                String content = parts[1];
                System.out.println("\n💬 Message de " + sender + ": " + content);
                System.out.print("Commande (msg/quit/help): ");
            }
        } else if (message.startsWith("INFO:")) {
            System.out.println("\nℹ️ " + message.substring(5));
            System.out.print("Commande (msg/quit/help): ");
        } else if (message.startsWith("ERROR:")) {
            System.out.println("\n❌ " + message.substring(6));
            System.out.print("Commande (msg/quit/help): ");
        }
    }

    private void sendMessage() {
        System.out.print("Email du destinataire: ");
        String receiver = scanner.nextLine().trim();

        if (!isValidEmail(receiver)) {
            System.out.println("Format d'email invalide!");
            return;
        }

        if (receiver.equals(userEmail)) {
            System.out.println("Vous ne pouvez pas vous envoyer un message à vous-même!");
            return;
        }

        System.out.print("Message: ");
        String content = scanner.nextLine().trim();

        if (content.isEmpty()) {
            System.out.println("Le message ne peut pas être vide!");
            return;
        }

        try {
            out.println("MSG:" + receiver + ":" + content);
            System.out.println("📤 Message envoyé à " + receiver);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("\n=== AIDE ===");
        System.out.println("msg  - Envoyer un message");
        System.out.println("quit - Quitter l'application");
        System.out.println("help - Afficher cette aide");
        System.out.println("============");
    }

    private void disconnect() {
        try {
            isConnected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Déconnecté du serveur.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static void main(String[] args) {
        ChatClientConsole client = new ChatClientConsole();
        client.start();
    }
}