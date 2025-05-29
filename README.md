# Application de Chat Java - Client/Serveur

Une application de messagerie instantanée complète développée en Java avec interface graphique Swing et version console.

## 🚀 Fonctionnalités

### 💬 Messagerie en temps réel
- Communication client-serveur instantanée
- Messages persistants en base de données SQLite
- Gestion des utilisateurs hors ligne
- Validation des formats d'email

### 🖥️ Interfaces utilisateur multiples
- **Interface console** : Version terminal pour tests rapides
- **Interface GUI simple** : Chat basique avec Swing
- **Interface GUI complète** : Chat + gestion de contacts

### 👥 Gestion des utilisateurs
- Connexion automatique par email
- Liste des utilisateurs connectés en temps réel
- Statut en ligne/hors ligne
- Authentification sécurisée

### 📇 Gestion des contacts (GUI complète)
- Ajout/modification/suppression de contacts
- Stockage local des informations de contact
- Démarrage de conversation depuis les contacts
- Interface CRUD complète

### 🔀 Port dynamique
- Sélection automatique du port disponible
- Sauvegarde du port pour les clients
- Pas de conflit de port

## 📋 Prérequis

### Système d'exploitation
- **Linux** (Ubuntu, Debian, CentOS, etc.)
- **macOS** (10.14+)
- **Windows** (10/11)

### Logiciels requis
- **Java 17+** (OpenJDK ou Oracle JDK)
- **Maven 3.6+**
- **Git** (pour cloner le projet)

### Installation des prérequis

#### Linux (Ubuntu/Debian)
```bash
# Mise à jour du système
sudo apt update

# Installation de Java 17
sudo apt install openjdk-17-jdk

# Installation de Maven
sudo apt install maven

# Installation de Git
sudo apt install git

# Vérification des installations
java -version
mvn -version
git --version
```

#### Linux (CentOS/RHEL/Fedora)
```bash
# Pour CentOS/RHEL
sudo yum install java-17-openjdk-devel maven git

# Pour Fedora
sudo dnf install java-17-openjdk-devel maven git

# Vérification
java -version
mvn -version
git --version
```

#### macOS
```bash
# Avec Homebrew (recommandé)
brew install openjdk@17 maven git

# Configuration du JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# Vérification
java -version
mvn -version
git --version
```

#### Windows
1. **Java 17** :
   - Télécharger depuis [OpenJDK](https://jdk.java.net/17/) ou [Oracle](https://www.oracle.com/java/technologies/downloads/#java17)
   - Installer et configurer JAVA_HOME dans les variables d'environnement
   - Ajouter `%JAVA_HOME%\bin` au PATH

2. **Maven** :
   - Télécharger depuis [Apache Maven](https://maven.apache.org/download.cgi)
   - Extraire et configurer MAVEN_HOME
   - Ajouter `%MAVEN_HOME%\bin` au PATH

3. **Git** :
   - Télécharger depuis [Git for Windows](https://git-scm.com/download/win)
   - Installer avec les options par défaut

4. **Vérification** (dans Command Prompt) :
```cmd
java -version
mvn -version
git --version
```

## 🛠️ Installation et Configuration

### 1. Clonage du projet
```bash
git clone <URL_DU_REPOSITORY>
cd java-email-chat-app
```

### 2. Compilation du projet
```bash
# Compilation avec Maven
mvn clean compile

# Compilation avec tests (optionnel)
mvn clean test compile
```

### 3. Initialisation de la base de données
La base de données SQLite est créée automatiquement au premier lancement du serveur.

## 🚀 Utilisation

### Démarrage rapide avec scripts de test

#### Linux et macOS
```bash
# Rendre le script exécutable
chmod +x run-test-linux.sh

# Lancer le script de test
./run-test-linux.sh
```

#### Windows
```cmd
# Lancer le script de test
run-test-windows.bat
```

Les scripts de test offrent un menu interactif pour :
1. Tester le client console
2. Tester l'interface GUI simple
3. Tester l'interface GUI complète
4. Arrêter le serveur

### Démarrage manuel

#### 1. Lancement du serveur
```bash
# Terminal 1 - Serveur
mvn exec:java -Dexec.mainClass="com.chat.ChatServer"
```

Le serveur :
- Trouve automatiquement un port disponible (à partir de 12345)
- Sauvegarde le port dans `runtime/current-port.txt`
- Affiche les informations de connexion

#### 2. Lancement des clients

##### Client Console (Terminal)
```bash
# Terminal 2 - Client console
mvn exec:java -Dexec.mainClass="com.chat.ChatClientConsole"
```

**Utilisation du client console :**
- Entrez votre email pour vous connecter
- Commandes disponibles :
  - `msg` : Envoyer un message
  - `help` : Afficher l'aide
  - `quit` : Quitter

##### Client GUI Simple
```bash
# Terminal 3 - Client GUI simple
mvn exec:java -Dexec.mainClass="com.chat.ui.ChatUI"
```

**Fonctionnalités GUI simple :**
- Interface Swing basique
- Connexion par email
- Envoi/réception de messages
- Zone de chat scrollable

##### Client GUI Complet
```bash
# Terminal 4 - Client GUI complet
mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI"
```

**Fonctionnalités GUI complète :**
- Interface Swing avancée avec onglets
- Gestion complète des contacts (CRUD)
- Liste des utilisateurs connectés
- Chat intégré avec les contacts

## 📱 Guide d'utilisation

### Interface Console

1. **Connexion** :
   ```
   Entrez votre email: utilisateur@example.com
   ✅ Connecté en tant que: utilisateur@example.com
   ```

2. **Envoi de message** :
   ```
   Commande (msg/quit/help): msg
   Email du destinataire: ami@example.com
   Message: Salut, comment ça va ?
   📤 Message envoyé à ami@example.com
   ```

3. **Réception de message** :
   ```
   💬 Message de ami@example.com: Ça va bien, merci !
   ```

### Interface GUI Simple

1. **Connexion** :
   - Entrez votre email dans le champ "Email"
   - Cliquez sur "Connect"
   - L'interface s'active une fois connecté

2. **Envoi de message** :
   - Entrez l'email du destinataire dans "To:"
   - Tapez votre message
   - Cliquez "Send" ou appuyez sur Entrée

### Interface GUI Complète

1. **Onglet Chat** :
   - Même fonctionnement que l'interface simple
   - Liste des utilisateurs connectés à droite
   - Clic sur un utilisateur pour le sélectionner comme destinataire

2. **Onglet Contacts** :
   - **Ajouter** : Bouton "➕ Add" pour créer un nouveau contact
   - **Modifier** : Sélectionner un contact et cliquer "✏️ Edit"
   - **Supprimer** : Sélectionner un contact et cliquer "🗑️ Delete"
   - **Chat** : Sélectionner un contact et cliquer "💬 Start Chat"

## 🗄️ Structure de la base de données

### Table `users`
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    display_name TEXT NOT NULL,
    password TEXT,
    is_online BOOLEAN DEFAULT FALSE,
    last_seen DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Table `chat_messages`
```sql
CREATE TABLE chat_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender_email TEXT NOT NULL,
    receiver_email TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    delivered BOOLEAN DEFAULT FALSE,
    read_by_receiver BOOLEAN DEFAULT FALSE
);
```

### Table `contacts`
```sql
CREATE TABLE contacts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    phone TEXT,
    address TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Configuration

### Fichiers de configuration

#### `src/main/resources/application.yml`
```yaml
chat:
  database:
    type: sqlite
    
spring:
  datasource:
    url: jdbc:sqlite:chat.db
    driver-class-name: org.sqlite.JDBC
```

#### Port dynamique
Le serveur sauvegarde le port utilisé dans `runtime/current-port.txt`. Les clients lisent ce fichier pour se connecter automatiquement au bon port.

## 🧪 Tests et Développement

### Compilation et tests
```bash
# Compilation simple
mvn compile

# Compilation avec tests
mvn test

# Nettoyage et compilation complète
mvn clean compile

# Création du JAR
mvn package
```

### Tests manuels

1. **Test de charge** :
   - Lancez plusieurs clients simultanément
   - Testez la communication entre plusieurs utilisateurs

2. **Test de persistence** :
   - Arrêtez et relancez le serveur
   - Vérifiez que les données sont conservées

3. **Test de reconnexion** :
   - Coupez la connexion réseau
   - Testez la reconnexion automatique

## 🐛 Dépannage

### Problèmes fréquents

#### Erreur de compilation Maven
```bash
# Solution 1: Nettoyer le cache Maven
mvn clean

# Solution 2: Forcer le téléchargement des dépendances
mvn dependency:resolve

# Solution 3: Vérifier la version Java
java -version
```

#### Port déjà utilisé
```bash
# Vérifier les ports utilisés (Linux/macOS)
netstat -tlnp | grep :12345

# Tuer le processus (Linux/macOS)
kill -9 <PID>

# Windows
netstat -ano | findstr :12345
taskkill /PID <PID> /F
```

#### Base de données corrompue
```bash
# Supprimer la base de données pour la recréer
rm chat.db
```

#### Problèmes d'affichage GUI
```bash
# Vérifier l'affichage X11 (Linux)
echo $DISPLAY

# Pour WSL Windows
export DISPLAY=:0
```

### Logs et débuggage

Les logs sont affichés dans la console du serveur et des clients. Pour plus de détails :

```bash
# Lancement avec logs détaillés
mvn exec:java -Dexec.mainClass="com.chat.ChatServer" -X
```

## 🤝 Contribution

### Structure du projet
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── chat/
│   │           ├── ChatServer.java          # Serveur principal
│   │           ├── ChatClientConsole.java   # Client console
│   │           ├── entity/                  # Entités JPA
│   │           ├── repository/              # Accès aux données
│   │           ├── service/                 # Logique métier
│   │           ├── ui/                      # Interfaces graphiques
│   │           └── config/                  # Configuration
│   └── resources/
│       └── application.yml                  # Configuration Spring
├── run-test-linux.sh                       # Script de test Linux/macOS
├── run-test-windows.bat                     # Script de test Windows
├── pom.xml                                  # Configuration Maven
└── README.md                                # Documentation
```

### Guidelines de développement

1. **Code Style** :
   - Utiliser les conventions Java standard
   - Commenter les méthodes publiques
   - Noms de variables explicites

2. **Tests** :
   - Tester chaque nouvelle fonctionnalité
   - Tests unitaires avec JUnit
   - Tests d'intégration

3. **Documentation** :
   - Mettre à jour le README pour les nouvelles fonctionnalités
   - Documenter les API publiques

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Support

Pour toute question ou problème :

1. Vérifiez la section [Dépannage](#-dépannage)
2. Consultez les logs pour plus d'informations
3. Ouvrez une issue sur le repository Git

---

**Version** : 1.0.0  
**Dernière mise à jour** : $(date +"%Y-%m-%d")  
**Compatibilité** : Java 17+, Maven 3.6+