package com.chat;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String userEmail;
    private Scanner scanner;
    private boolean isConnected = false;

    public ChatClient() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== CLIENT DE CHAT EN LIGNE DE COMMANDE ===");
        System.out.println("Tapez 'quit' pour quitter à tout moment");
        System.out.println("==========================================");

        while (true) {
            if (!isConnected) {
                if (!connectToServer()) {
                    System.out.println("Voulez-vous réessayer? (o/n)");
                    String retry = scanner.nextLine().trim().toLowerCase();
                    if (!retry.equals("o") && !retry.equals("oui")) {
                        break;
                    }
                    continue;
                }
            }

            System.out.println("\nCommandes disponibles:");
            System.out.println("1. msg <email> <message> - Envoyer un message");
            System.out.println("2. quit - Quitter");
            System.out.print("Tapez votre commande: ");

            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit")) {
                break;
            }
            
            handleUserInput(input);
        }

        disconnect();
        System.out.println("Au revoir!");
    }

    private boolean connectToServer() {
        System.out.print("Entrez votre adresse email: ");
        userEmail = scanner.nextLine().trim();
        
        if (!isValidEmail(userEmail)) {
            System.out.println("Format d'email invalide!");
            return false;
        }

        try {
            int port = readPortFromFile();
            socket = new Socket("localhost", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Authentification
            out.println(userEmail);
            String response = in.readLine();
            
            if (response.startsWith("SUCCESS")) {
                isConnected = true;
                System.out.println("Connecté en tant que: " + userEmail);
                
                // Thread pour recevoir les messages
                Thread receiver = new Thread(this::receiveMessages);
                receiver.setDaemon(true);
                receiver.start();
                
                return true;
            } else {
                System.out.println("Erreur d'authentification: " + response);
                socket.close();
                return false;
            }
            
        } catch (IOException e) {
            System.out.println("Impossible de se connecter au serveur: " + e.getMessage());
            System.out.println("Assurez-vous que le serveur est démarré.");
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
            System.out.println("\nConnexion perdue avec le serveur.");
            isConnected = false;
        }
    }

    private void handleReceivedMessage(String message) {
        if (message.startsWith("MESSAGE:")) {
            String[] parts = message.substring(8).split(":", 2);
            if (parts.length == 2) {
                String sender = parts[0];
                String content = parts[1];
                System.out.println("\n>>> " + sender + ": " + content);
                System.out.print("Tapez votre commande: ");
            }
        } else if (message.startsWith("INFO:")) {
            System.out.println("\n[INFO] " + message.substring(5));
            System.out.print("Tapez votre commande: ");
        }
    }

    private void handleUserInput(String input) {
        if (input.startsWith("msg ")) {
            String[] parts = input.split(" ", 3);
            if (parts.length < 3) {
                System.out.println("Usage: msg <email> <message>");
                return;
            }
            
            String receiver = parts[1];
            String message = parts[2];
            
            if (!isValidEmail(receiver)) {
                System.out.println("Email du destinataire invalide!");
                return;
            }
            
            if (receiver.equals(userEmail)) {
                System.out.println("Vous ne pouvez pas vous envoyer un message à vous-même!");
                return;
            }
            
            sendMessage(receiver, message);
        } else {
            System.out.println("Commande non reconnue. Tapez 'msg <email> <message>' ou 'quit'");
        }
    }

    private void sendMessage(String receiver, String content) {
        if (!isConnected) {
            System.out.println("Non connecté au serveur!");
            return;
        }
        
        try {
            out.println("MSG:" + receiver + ":" + content);
            System.out.println("Message envoyé à " + receiver);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    private void disconnect() {
        try {
            isConnected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.start();
    }
}