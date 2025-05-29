# Application de Chat en Java – Messagerie par Email

Ce projet pédagogique développe une application de chat en Java basée sur une architecture client-serveur, utilisant les adresses email comme identifiant unique des utilisateurs. La communication s'effectue via les protocoles TCP et UDP avec des sockets Java. Ce README guide un développeur débutant dans la réalisation du projet en 2,5 jours, avec des instructions détaillées, des librairies recommandées, des astuces pratiques et une priorisation des tâches.

## Informations générales

- **Niveau** : Débutant (connaissance de base en Java : variables, boucles, classes)
- **Durée** : 2,5 jours (environ 20 heures de travail effectif)
- **IDE** : Visual Studio Code (simple, gratuit et adapté aux débutants)
- **Systèmes d'exploitation** : Windows, Linux
- **Langage** : Java (JDK 17 recommandé)
- **Backend** : Java avec Spring Boot et sockets
- **Objectif** : Implémenter une application fonctionnelle avec les fonctionnalités de base (phase 1) et, si possible, des fonctionnalités avancées (phase 2)

## Documentation du projet

Le cahier des charges est disponible ici (lecture publique, édition privée) :
[Document Google Docs](https://docs.google.com/document/d/1D-NnqkJ6DVCdzun1x0Qz5UJNW2g4SZVR8H76ArbCvEM/edit?tab=t.0)

## Objectif du projet

Développer une application de messagerie de type WhatsApp fonctionnant sur desktop, avec :

- Utilisation des emails comme identifiant unique
- Communication via sockets TCP et UDP
- Architecture client-serveur
- Phase 1 (base) : chat textuel via console
- Phase 2 (avancée, si temps disponible) : interface graphique, base de données, gestion des contacts et fichiers multimédias

Ce projet est conçu pour être réalisable en 2,5 jours par un développeur débutant, en priorisant les tâches essentielles et en utilisant des outils simples.

## Prérequis et installation

### 1. Installer Java

Le JDK 17 (Java Development Kit) doit être installé. Cette version est recommandée pour sa stabilité et sa compatibilité.

#### Windows

1. Télécharger le JDK 17 depuis : https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
2. Suivre l'installateur graphique
3. Ouvrir une invite de commande (cmd) et vérifier l'installation :

```bash
java -version
javac -version
```

Une version comme 17.x.x doit s'afficher. Si ce n'est pas le cas, vérifier l'installation.

#### Linux (Debian/Ubuntu)

Exécuter dans un terminal :

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

Vérifier l'installation :

```bash
java -version
javac -version
```

**Astuce** : Si une erreur `command not found` apparaît, configurer la variable d'environnement JAVA_HOME. Sur Windows, chercher "Variables d'environnement" dans les paramètres système. Sur Linux, ajouter `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` au fichier `~/.bashrc`.

### 2. Installer Visual Studio Code

1. Télécharger Visual Studio Code : https://code.visualstudio.com/
2. Suivre l'installateur graphique (Windows) ou installer via le gestionnaire de paquets (Linux)

**Pourquoi Visual Studio Code ?** C'est un éditeur léger, gratuit, avec des extensions qui simplifient le développement Java pour les débutants.

### 3. Extensions VS Code recommandées

Installer ces extensions dans VS Code pour faciliter le développement :

| Extension | Description | Pourquoi l'installer ? |
|-----------|-------------|------------------------|
| [Extension Pack for Java]([url](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)) | Pack officiel de Microsoft pour Java | Fournit tout ce qu'il faut : coloration syntaxique, autocomplétion, débogage |
| Java Debugger | Outil de débogage | Permet de repérer facilement les erreurs en exécutant le code pas à pas |
| Java Test Runner | Exécution de tests unitaires | Utile pour tester des morceaux de code (optionnel pour ce projet) |
| Language Support for Java (by Red Hat) | Support avancé pour Java | Améliore l'autocomplétion et détecte les erreurs de syntaxe |
| Code Runner | Exécution rapide de fichiers Java | Permet de lancer un fichier Java en un clic, idéal pour tester |

**Astuce** : Pour installer une extension, ouvrir VS Code, aller dans l'onglet Extensions (icône de carré avec une flèche), chercher le nom de l'extension et cliquer sur "Installer".

### 4. Cloner ou créer le projet

Si Git est utilisé (recommandé pour gérer les versions) :

```bash
git clone https://github.com/votre-utilisateur/java-email-chat-app.git
cd java-email-chat-app
code .
```

Si Git n'est pas connu, créer manuellement un dossier nommé `java-email-chat-app` et l'ouvrir dans VS Code.

**Astuce** : Si Git n'est pas installé, le télécharger depuis https://git-scm.com/. Pour les très débutants, ignorer Git et travailler directement dans un dossier local.

### 5. Structure du projet

Créer l'arborescence suivante dans le dossier `java-email-chat-app` :

```
java-email-chat-app/
├── README.md
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── chat/
│                   ├── ChatApplication.java
│                   ├── server/
│                   │   └── ChatServer.java
│                   ├── client/
│                   │   └── ChatClient.java
│                   └── common/
│                       └── Message.java
├── target/
├── .vscode/
│   └── settings.json
└── .gitignore
```

**Contenu de `pom.xml`** (configuration Maven pour Spring Boot) :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.chat</groupId>
    <artifactId>java-email-chat-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.42.0.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**Contenu de `.gitignore`** (pour ignorer les fichiers inutiles si Git est utilisé) :

```
target/
*.class
.DS_Store
*.log
```

**Contenu de `.vscode/settings.json`** (pour configurer VS Code pour Java avec Maven) :

```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.project.sourcePaths": ["src/main/java"],
    "java.project.outputPath": "target/classes",
    "maven.terminal.useJavaHome": true
}
```

**Astuce** : Créer ces fichiers manuellement dans VS Code (clic droit > Nouveau fichier). Le dossier `target` stockera les fichiers compilés (.class). Le dossier `.vscode` configure VS Code pour reconnaître le projet Java avec Maven.

## Librairies nécessaires

**Spring Boot** est nécessaire dès le début du projet pour gérer l'architecture client-serveur et les communications. Les sockets Java natifs (`java.net`) sont également utilisés.

### Librairies principales :

- **Spring Boot Starter** : Framework principal pour l'architecture du projet
  - Ajouter dans `pom.xml` : `spring-boot-starter` et `spring-boot-starter-web`
  
- **Spring Boot WebSocket** : Pour la gestion des communications temps réel
  - Ajouter dans `pom.xml` : `spring-boot-starter-websocket`

- **SQLite JDBC** : Pour gérer une base de données SQLite (simple pour les débutants)
  - Télécharger le pilote JDBC depuis : https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
  - Ajouter le fichier .jar dans un dossier `lib/` à la racine du projet

### Librairies avancées (phase 2) :

- **JavaFX (optionnel, phase avancée)** : Pour l'interface graphique
  - Télécharger JavaFX depuis : https://openjfx.io
  - Suivre les instructions pour configurer JavaFX avec VS Code (voir "Ressources utiles")

- **Gson (optionnel)** : Pour sérialiser/désérialiser les messages en JSON
  - Télécharger depuis : https://mvnrepository.com/artifact/com.google.code.gson/gson

**Astuce** : Pour ajouter une librairie avec Maven, l'ajouter dans le fichier `pom.xml` dans la section `<dependencies>`. VS Code rechargera automatiquement les dépendances.

## Plan de développement priorisé (2,5 jours)

Le développement est divisé en deux phases pour respecter le délai. Chaque tâche est priorisée avec une explication sur pourquoi la réaliser en premier. Le planning suppose 8 heures par jour, avec une répartition équilibrée entre la phase de base (essentielle) et la phase avancée (optionnelle).

## Jour 1 : Phase de base – Fondations (8 heures)

**Objectif** : Avoir un chat textuel fonctionnel via console avec Spring Boot et sockets TCP.

### 1. Mettre en place l'arborescence du projet (45 minutes)

**Pourquoi ?** Une structure claire avec Spring Boot évite la confusion et facilite l'organisation du code.

- Créer les dossiers et fichiers indiqués dans "Structure du projet"
- Créer le fichier `pom.xml` avec les dépendances Spring Boot
- Tester un programme simple Spring Boot pour vérifier que Java, Maven et VS Code fonctionnent :

Créer `src/main/java/com/chat/ChatApplication.java` :

```java
package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
        System.out.println("Test de configuration Spring Boot OK !");
    }
}
```

Compiler et exécuter avec Maven :

```bash
mvn clean compile
mvn spring-boot:run
```

### 2. Créer la classe Message (30 minutes)

**Pourquoi ?** Cette classe représente un message échangé entre client et serveur, ce qui est au cœur du projet.

Créer `src/main/java/com/chat/common/Message.java` :

```java
package com.chat.common;
import java.io.Serializable;

public class Message implements Serializable {
    private String senderEmail;
    private String receiverEmail;
    private String content;
    private long timestamp;

    public Message(String senderEmail, String receiverEmail, String content) {
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderEmail() { return senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
}
```

**Astuce** : La classe implémente `Serializable` pour permettre l'envoi d'objets via sockets.

### 3. Implémenter le serveur Spring Boot (ChatServer.java) (3 heures)

**Pourquoi ?** Le serveur Spring Boot est le cœur du système, gérant les connexions et les messages avec une architecture robuste. Le faire en premier permet de tester la communication rapidement.

Créer `src/main/java/com/chat/server/ChatServer.java` :

```java
package com.chat.server;
import com.chat.common.Message;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.*;
import java.util.*;

@Service
public class ChatServer {
    private static final int PORT = 12345;
    private static HashMap<String, ObjectOutputStream> clients = new HashMap<>();
    private ServerSocket serverSocket;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serveur Spring Boot démarré sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
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
                clientEmail = (String) in.readObject();
                clients.put(clientEmail, out);
                System.out.println("Client connecté : " + clientEmail);

                while (true) {
                    Message message = (Message) in.readObject();
                    broadcastMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                clients.remove(clientEmail);
                System.out.println("Client déconnecté : " + clientEmail);
            }
        }

        private void broadcastMessage(Message message) throws IOException {
            ObjectOutputStream receiverOut = clients.get(message.getReceiverEmail());
            if (receiverOut != null) {
                receiverOut.writeObject(message);
                receiverOut.flush();
            } else {
                System.out.println("Destinataire hors ligne : " + message.getReceiverEmail());
            }
        }
    }
}
```

Modifier `ChatApplication.java` pour démarrer le serveur :

```java
package com.chat;

import com.chat.server.ChatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ChatApplication.class, args);
        ChatServer chatServer = context.getBean(ChatServer.class);
        chatServer.startServer();
    }
}
```

**Astuce** : Utiliser TCP (pas UDP) pour la phase de base, car il est plus fiable pour les débutants. Tester le serveur en lançant `mvn spring-boot:run` et vérifier qu'il affiche "Serveur Spring Boot démarré".

### 4. Implémenter le client (ChatClient.java) (2,5 heures)

**Pourquoi ?** Le client permet de tester la communication avec le serveur Spring Boot immédiatement.

Créer `src/main/java/com/chat/client/ChatClient.java` :

```java
package com.chat.client;
import com.chat.common.Message;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre email : ");
        String email = scanner.nextLine();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(email);
            out.flush();

            Thread receiver = new Thread(() -> {
                try {
                    while (true) {
                        Message message = (Message) in.readObject();
                        System.out.println("[" + message.getSenderEmail() + "]: " + message.getContent());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Connexion au serveur perdue.");
                }
            });
            receiver.start();

            while (true) {
                System.out.print("Destinataire (email) : ");
                String receiverEmail = scanner.nextLine();
                System.out.print("Message : ");
                String content = scanner.nextLine();

                Message message = new Message(email, receiverEmail, content);
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**Astuce** : Lancer d'abord le serveur (`mvn spring-boot:run`), puis compiler et exécuter le client dans un terminal séparé. Tester l'envoi de messages entre deux clients.

### 5. Tester la communication (1 heure)

**Pourquoi ?** Tester tôt permet de détecter les erreurs avant d'ajouter des fonctionnalités complexes.

Compiler et exécuter avec Maven :

```bash
# Terminal 1 - Serveur Spring Boot
mvn spring-boot:run

# Terminal 2 - Client 1
mvn compile exec:java -Dexec.mainClass="com.chat.client.ChatClient"

# Terminal 3 - Client 2  
mvn compile exec:java -Dexec.mainClass="com.chat.client.ChatClient"
```

Entrer un email pour chaque client, envoyer des messages et vérifier qu'ils s'affichent dans la console.

**Astuce** : Si des erreurs de connexion apparaissent, vérifier que le port 12345 n'est pas utilisé par une autre application.

## Jour 2 : Phase de base – Finalisation et gestion hors ligne (8 heures)

### 1. Gérer les messages hors ligne (3 heures)

**Pourquoi ?** Cette fonctionnalité est requise dans le cahier des charges et permet de stocker les messages pour les utilisateurs déconnectés.

Modifier `ChatServer.java` pour stocker les messages hors ligne dans une liste :

```java
private static HashMap<String, List<Message>> offlineMessages = new HashMap<>();

private void broadcastMessage(Message message) throws IOException {
    ObjectOutputStream receiverOut = clients.get(message.getReceiverEmail());
    if (receiverOut != null) {
        receiverOut.writeObject(message);
        receiverOut.flush();
    } else {
        offlineMessages.computeIfAbsent(message.getReceiverEmail(), k -> new ArrayList<>()).add(message);
        System.out.println("Message stocké pour : " + message.getReceiverEmail());
    }
}
```

Lors de la connexion d'un client, envoyer les messages hors ligne :

```java
// Dans ClientHandler, après clients.put(clientEmail, out)
List<Message> pendingMessages = offlineMessages.getOrDefault(clientEmail, new ArrayList<>());
for (Message msg : pendingMessages) {
    out.writeObject(msg);
    out.flush();
}
offlineMessages.remove(clientEmail);
```

**Astuce** : Tester en déconnectant un client (Ctrl+C dans le terminal) et en envoyant un message depuis un autre client.

### 2. Valider les données (2 heures)

**Pourquoi ?** La validation évite les erreurs comme des emails invalides ou des messages vides.

Ajouter une méthode dans `ChatClient.java` pour valider l'email :

```java
private static boolean isValidEmail(String email) {
    return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
}
```

L'utiliser avant d'envoyer l'email au serveur :

```java
System.out.print("Entrez votre email : ");
String email = scanner.nextLine();
if (!isValidEmail(email)) {
    System.out.println("Email invalide !");
    return;
}
```

Vérifier que le contenu du message n'est pas vide avant envoi :

```java
if (content.trim().isEmpty()) {
    System.out.println("Le message ne peut pas être vide !");
    continue;
}
```

### 3. Améliorer la gestion des erreurs (2 heures)

**Pourquoi ?** Une bonne gestion des erreurs rend l'application robuste et évite les plantages.

Ajouter des blocs try-catch dans `ChatServer.java` et `ChatClient.java` pour gérer les déconnexions inattendues.

Exemple dans `ClientHandler` :

```java
catch (IOException e) {
    System.out.println("Erreur avec le client " + clientEmail + ": " + e.getMessage());
    clients.remove(clientEmail);
}
```

**Astuce** : Rediriger les erreurs vers un fichier log pour faciliter le débogage :

```java
PrintWriter log = new PrintWriter(new FileWriter("server.log", true));
log.println("Erreur : " + e.getMessage());
log.close();
```

### 4. Tests finaux de la phase de base (1 heure)

**Pourquoi ?** Valider que toutes les fonctionnalités de base fonctionnent avant de passer à la phase avancée.

Tester :
- Connexion/déconnexion de plusieurs clients
- Envoi de messages en temps réel
- Réception de messages hors ligne après reconnexion
- Gestion des erreurs (email invalide, déconnexion brutale)

## Jour 3 (4 heures) : Phase avancée – Interface graphique et base de données

**Note** : Cette phase est optionnelle si le temps manque. Prioriser la phase de base si du retard est pris.

### 1. Ajouter une interface graphique avec JavaFX (2 heures)

**Pourquoi ?** Une interface graphique rend l'application plus conviviale, mais elle est complexe pour un débutant. Utiliser JavaFX, car il est moderne et bien documenté.

Télécharger JavaFX et le configurer dans VS Code (voir https://openjfx.io).

Créer `src/client/ChatUI.java` pour une interface simple :

```java
package client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class ChatUI extends Application {
    @Override
    public void start(Stage stage) {
        TextField emailField = new TextField();
        emailField.setPromptText("Entrez votre email");
        
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        
        TextField receiverField = new TextField();
        receiverField.setPromptText("Email du destinataire");
        
        TextField messageField = new TextField();
        messageField.setPromptText("Votre message");
        
        Button sendButton = new Button("Envoyer");

        VBox layout = new VBox(10, emailField, chatArea, receiverField, messageField, sendButton);
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Chat par Email");
        stage.show();

        sendButton.setOnAction(e -> {
            String email = emailField.getText();
            String receiver = receiverField.getText();
            String message = messageField.getText();
            chatArea.appendText("Vous à " + receiver + ": " + message + "\n");
            // TODO : Intégrer avec ChatClient pour envoyer le message
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

**Astuce** : Compiler avec JavaFX (voir documentation OpenJFX). Intégrer le code de `ChatClient.java` dans `ChatUI.java` pour connecter l'interface au serveur.

### 2. Configurer une base de données SQLite (1,5 heures)

**Pourquoi ?** SQLite est léger et facile à configurer pour stocker les messages.

Télécharger le pilote JDBC SQLite et le placer dans `lib/`.

Créer une table pour les messages dans `ChatServer.java` :

```java
import java.sql.*;

public class Database {
    private Connection conn;

    public Database() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:chat.db");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (" +
                    "sender TEXT, receiver TEXT, content TEXT, timestamp LONG)");
    }

    public void saveMessage(Message message) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO messages (sender, receiver, content, timestamp) VALUES (?, ?, ?, ?)");
        pstmt.setString(1, message.getSenderEmail());
        pstmt.setString(2, message.getReceiverEmail());
        pstmt.setString(3, message.getContent());
        pstmt.setLong(4, message.getTimestamp());
        pstmt.executeUpdate();
    }
}
```

Modifier `broadcastMessage` pour sauvegarder les messages :

```java
Database db = new Database();
db.saveMessage(message);
```

**Astuce** : Tester la connexion à SQLite en exécutant un programme séparé qui insère et lit des données.

### 3. Tests finaux (0,5 heure)

**Pourquoi ?** Vérifier que l'interface et la base de données fonctionnent ensemble.

- Tester l'envoi de messages via l'interface graphique
- Vérifier que les messages sont stockés dans la base de données

## Astuces pour accélérer le développement

- **Utiliser des modèles de code** : Copier-coller les extraits fournis ci-dessus pour gagner du temps
- **Tester souvent** : Après chaque modification, exécuter le serveur et le client pour repérer les erreurs immédiatement
- **Déboguer avec VS Code** : Utiliser l'extension Java Debugger pour placer des points d'arrêt et inspecter les variables
- **Simplifier** : Si le temps manque, ignorer JavaFX et se concentrer sur la console. L'interface peut être ajoutée plus tard
- **Utiliser des bibliothèques** : Gson pour JSON ou SQLite pour la base de données réduit le code à écrire manuellement

## Contraintes et bonnes pratiques

- **Validation** : Toujours vérifier les entrées utilisateur (ex. : format d'email)
- **Modularité** : Séparer le code en classes (serveur, client, message) pour faciliter la maintenance
- **Gestion des erreurs** : Ajouter des messages clairs pour chaque erreur (ex. : "Destinataire introuvable")
- **Documentation** : Commenter le code pour expliquer chaque fonction (ex. : // Envoie le message au destinataire)

## Ressources utiles

- [Cours Java OpenClassrooms](https://openclassrooms.com/fr/courses/6173501-apprenez-a-programmer-en-java) : Bases de Java
- [Java Sockets Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/networking/sockets/) : Guide sur les sockets
- [JavaFX pour débutants](https://openjfx.io/openjfx-docs/) : Tutoriels pour créer des interfaces graphiques
- [SQLite JDBC Tutorial](https://www.sqlitetutorial.net/sqlite-java/) : Guide pour SQLite en Java

## Contribution

1. Forker le dépôt
2. Cloner : `git clone`
3. Créer une branche : `git checkout -b dev-fonctionnalite`
4. Commiter : `git commit -m "Ajout fonctionnalité X"`
5. Pousser : `git push origin dev-fonctionnalite`
6. Créer une Pull Request

## Support

Pour toute question, consulter le Google Docs ou ouvrir une issue sur GitHub.

## Licence

Ce projet est sous licence MIT.

## Réponses aux demandes spécifiques

### Création d'un .zip
Si un fichier .zip avec l'arborescence et les fichiers de base (Main.java, ChatServer.java, ChatClient.java, Message.java, .gitignore, settings.json) est souhaité, le contenu exact de chaque fichier peut être fourni. Ces fichiers peuvent alors être créés localement et zippés.

### GitHub
Si un dépôt GitHub existe, guidance peut être fournie pour initialiser et pousser les fichiers. Exemple :

```bash
git init
git add .
git commit -m "Initialisation du projet"
git remote add origin https://github.com/votre-utilisateur/java-email-chat-app.git
git push -u origin main
```
