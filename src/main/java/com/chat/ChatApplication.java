package com.chat;

import com.chat.ui.ImprovedChatUI;

public class ChatApplication {
    public static void main(String[] args) {
        System.out.println("=== Application de Chat Java ===");
        System.out.println("Choisissez votre mode:");
        System.out.println("1. Démarrer le serveur: java -cp target/classes com.chat.ChatServer");
        System.out.println("2. Client graphique: java -cp target/classes com.chat.ui.ImprovedChatUI");
        System.out.println("3. Client console: java -cp target/classes com.chat.ChatClient");
        
        // Par défaut, lancer l'interface graphique
        if (args.length == 0 || "gui".equals(args[0])) {
            ImprovedChatUI.main(args);
        } else if ("server".equals(args[0])) {
            ChatServer.main(args);
        } else if ("console".equals(args[0])) {
            ChatClient.main(args);
        }
    }
}
