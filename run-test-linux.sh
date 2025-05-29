#!/bin/bash

# Script de test pour Linux/macOS - Application de Chat Java
# Ce script lance le serveur et teste les différents clients

echo "=========================================="
echo "  TEST APPLICATION DE CHAT JAVA"
echo "  Compatible: Linux & macOS"
echo "=========================================="

# Vérification Java
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé ou pas dans le PATH"
    echo "   Installez Java 17+ pour continuer"
    exit 1
fi

# Vérification Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé ou pas dans le PATH"
    echo "   Installez Maven pour continuer"
    exit 1
fi

echo "✅ Java et Maven détectés"

# Compilation du projet
echo ""
echo "📦 Compilation du projet..."
mvn clean compile -q

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✅ Compilation réussie"

# Fonction pour lancer le serveur
start_server() {
    echo ""
    echo "🚀 Lancement du serveur de chat..."
    mvn exec:java -Dexec.mainClass="com.chat.ChatServer" -q &
    SERVER_PID=$!
    echo "   Serveur lancé avec PID: $SERVER_PID"
    sleep 3
    
    # Vérification que le serveur a démarré
    if ! ps -p $SERVER_PID > /dev/null; then
        echo "❌ Le serveur n'a pas pu démarrer"
        return 1
    fi
    
    # Lecture du port utilisé
    if [ -f "runtime/current-port.txt" ]; then
        PORT=$(cat runtime/current-port.txt)
        echo "   Serveur actif sur le port: $PORT"
    else
        echo "   Port par défaut: 12345"
    fi
    
    return 0
}

# Fonction pour arrêter le serveur
stop_server() {
    if [ ! -z "$SERVER_PID" ]; then
        echo ""
        echo "🛑 Arrêt du serveur..."
        kill $SERVER_PID 2>/dev/null
        wait $SERVER_PID 2>/dev/null
        echo "   Serveur arrêté"
    fi
}

# Gestionnaire de signal pour arrêt propre
trap 'stop_server; exit 0' SIGINT SIGTERM

# Lancement du serveur
start_server
if [ $? -ne 0 ]; then
    exit 1
fi

echo ""
echo "=========================================="
echo "  CHOISISSEZ UN MODE DE TEST"
echo "=========================================="
echo "1) Client Console (Terminal)"
echo "2) Client GUI Simple (Swing)"
echo "3) Client GUI Complet (Swing + Contacts)"
echo "4) Arrêter le serveur"
echo "5) Test Multi-Clients (3 instances GUI)"
echo ""

while true; do
    read -p "Votre choix (1-5): " choice
    
    case $choice in
        1)
            echo ""
            echo "🖥️  Lancement du client console..."
            echo "   (Tapez 'quit' pour revenir au menu)"
            mvn exec:java -Dexec.mainClass="com.chat.ChatClientConsole" -q
            echo ""
            echo "Retour au menu principal..."
            ;;
        2)
            echo ""
            echo "🖱️  Lancement du client GUI simple..."
            mvn exec:java -Dexec.mainClass="com.chat.ui.ChatUI" -q &
            GUI_PID=$!
            echo "   Client GUI lancé avec PID: $GUI_PID"
            echo "   Fermez la fenêtre pour revenir au menu"
            wait $GUI_PID 2>/dev/null
            echo "   Client GUI fermé"
            ;;
        3)
            echo ""
            echo "🖱️  Lancement du client GUI complet..."
            mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI" -q &
            GUI_PID=$!
            echo "   Client GUI complet lancé avec PID: $GUI_PID"
            echo "   Fermez la fenêtre pour revenir au menu"
            wait $GUI_PID 2>/dev/null
            echo "   Client GUI fermé"
            ;;
        5)
            echo ""
            echo "🖱️  Test multi-clients (3 instances GUI)..."
            if [ -f "runtime/current-port.txt" ]; then
                SERVER_ADDR="localhost"
                PORT=$(cat runtime/current-port.txt)
                echo "   Lancement de 3 clients sur $SERVER_ADDR:$PORT"
                # Lancer les clients GUI en leur passant l'adresse et le port en arguments
                mvn exec:java -Dexec.mainClass='com.chat.ui.ImprovedChatUI' -Dexec.args="$SERVER_ADDR $PORT" -q &
                sleep 2
                mvn exec:java -Dexec.mainClass='com.chat.ui.ImprovedChatUI' -Dexec.args="$SERVER_ADDR $PORT" -q &
                sleep 2
                mvn exec:java -Dexec.mainClass='com.chat.ui.ImprovedChatUI' -Dexec.args="$SERVER_ADDR $PORT" -q &
                echo "   3 clients GUI lancés. Fermez toutes les fenêtres pour revenir au menu."
                echo "   Utilisez différents emails pour tester la communication multi-clients."
                wait
            else
                echo "   Erreur: Fichier de port introuvable"
            fi
            ;;
        4)
            stop_server
            echo ""
            echo "✅ Test terminé"
            exit 0
            ;;
        *)
            echo "❌ Choix invalide, veuillez entrer 1, 2, 3, 4 ou 5"
            ;;
    esac
    
    echo ""
    echo "=========================================="
    echo "  CHOISISSEZ UN MODE DE TEST"
    echo "=========================================="
    echo "1) Client Console (Terminal)"
    echo "2) Client GUI Simple (Swing)"
    echo "3) Client GUI Complet (Swing + Contacts)"
    echo "4) Arrêter le serveur"
    echo "5) Test Multi-Clients (3 instances GUI)"
    echo ""
done