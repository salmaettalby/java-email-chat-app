package com.chat.client;

import com.chat.common.Message;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static String userEmail;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Client de Chat par Email ===");
        System.out.print("📧 Votre email: ");
        userEmail = scanner.nextLine();

        if (!isValidEmail(userEmail)) {
            System.err.println("❌ Email invalide!");
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Envoi de l'email pour authentification
            out.writeObject(userEmail);
            out.flush();

            // Thread pour recevoir les messages
            Thread receiver = new Thread(() -> {
                try {
                    while (true) {
                        Object received = in.readObject();
                        if (received instanceof Message) {
                            Message message = (Message) received;
                            System.out.println("\n💬 " + message);
                            System.out.print("Commande (ou 'help'): ");
                        } else {
                            System.out.println("ℹ️  " + received);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\n❌ Connexion au serveur perdue.");
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            // Interface de commandes
            printHelp();
            while (true) {
                System.out.print("Commande (ou 'help'): ");
                String command = scanner.nextLine().trim();
                
                if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
                    break;
                } else if (command.equalsIgnoreCase("help")) {
                    printHelp();
                } else if (command.startsWith("msg ")) {
                    handleSendMessage(command, out, scanner);
                } else {
                    System.out.println("❌ Commande inconnue. Tapez 'help' pour l'aide.");
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur connexion: " + e.getMessage());
        }
    }

    private static void handleSendMessage(String command, ObjectOutputStream out, Scanner scanner) throws IOException {
        String[] parts = command.split(" ", 2);
        if (parts.length < 2) {
            System.out.println("❌ Usage: msg destinataire@email.com");
            return;
        }

        String receiverEmail = parts[1];
        if (!isValidEmail(receiverEmail)) {
            System.out.println("❌ Email destinataire invalide!");
            return;
        }

        System.out.print("📝 Message: ");
        String content = scanner.nextLine();
        
        if (content.trim().isEmpty()) {
            System.out.println("❌ Message vide!");
            return;
        }

        Message message = new Message(userEmail, receiverEmail, content);
        out.writeObject(message);
        out.flush();
        System.out.println("✅ Message envoyé!");
    }

    private static void printHelp() {
        System.out.println("\n=== Commandes disponibles ===");
        System.out.println("msg email@domain.com - Envoyer un message");
        System.out.println("help                 - Afficher cette aide");
        System.out.println("quit/exit           - Quitter l'application");
        System.out.println("===============================\n");
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
