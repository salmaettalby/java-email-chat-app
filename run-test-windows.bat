@echo off
setlocal EnableDelayedExpansion

REM Script de test pour Windows - Application de Chat Java
REM Ce script lance le serveur et teste les différents clients

echo ==========================================
echo   TEST APPLICATION DE CHAT JAVA
echo   Compatible: Windows
echo ==========================================

REM Vérification Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java n'est pas installé ou pas dans le PATH
    echo    Installez Java 17+ pour continuer
    pause
    exit /b 1
)

REM Vérification Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven n'est pas installé ou pas dans le PATH
    echo    Installez Maven pour continuer
    pause
    exit /b 1
)

echo ✅ Java et Maven détectés

REM Compilation du projet
echo.
echo 📦 Compilation du projet...
mvn clean compile -q
if errorlevel 1 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

echo ✅ Compilation réussie

REM Lancement du serveur
echo.
echo 🚀 Lancement du serveur de chat...
start /B mvn exec:java -Dexec.mainClass="com.chat.ChatServer" -q
timeout /t 3 /nobreak >nul

REM Vérification du port
if exist "runtime\current-port.txt" (
    set /p PORT=<runtime\current-port.txt
    echo    Serveur actif sur le port: !PORT!
) else (
    echo    Port par défaut: 12345
)

echo.
echo ==========================================
echo   CHOISISSEZ UN MODE DE TEST
echo ==========================================
echo 1^) Client Console ^(Terminal^)
echo 2^) Client GUI Simple ^(Swing^)
echo 3^) Client GUI Complet ^(Swing + Contacts^)
echo 4^) Arrêter le serveur
echo.

:menu
set /p choice="Votre choix (1-5): "

if "%choice%"=="1" (
    echo.
    echo 🖥️  Lancement du client console...
    echo    ^(Tapez 'quit' pour revenir au menu^)
    mvn exec:java -Dexec.mainClass="com.chat.ChatClientConsole" -q
    echo.
    echo Retour au menu principal...
    goto menu_display
)

if "%choice%"=="2" (
    echo.
    echo 🖱️  Lancement du client GUI simple...
    start /WAIT mvn exec:java -Dexec.mainClass="com.chat.ui.ChatUI" -q
    echo    Client GUI fermé
    goto menu_display
)

if "%choice%"=="3" (
    echo.
    echo 🖱️  Lancement du client GUI complet...
    start /WAIT mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI" -q
    echo    Client GUI fermé
    goto menu_display
)

if "%choice%"=="4" (
    echo.
    echo 🛑 Arrêt du serveur...
    taskkill /F /IM java.exe >nul 2>&1
    echo    Serveur arrêté
    echo.
    echo ✅ Test terminé
    pause
    exit /b 0
)

if "%choice%"=="5" (
    echo.
    echo 🖱️  Test multi-clients ^(3 instances GUI^)...
    if exist "runtime\current-port.txt" (
        set /p PORT=<runtime\current-port.txt
        echo    Lancement de 3 clients sur localhost:!PORT!
        start /MIN mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI" -Dexec.args="localhost !PORT!" -q
        timeout /t 2 /nobreak >nul
        start /MIN mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI" -Dexec.args="localhost !PORT!" -q
        timeout /t 2 /nobreak >nul
        start /MIN mvn exec:java -Dexec.mainClass="com.chat.ui.ImprovedChatUI" -Dexec.args="localhost !PORT!" -q
        echo    3 clients GUI lancés. Fermez toutes les fenêtres pour revenir au menu.
        echo    Utilisez différents emails pour tester la communication multi-clients.
        pause
    ) else (
        echo    Erreur: Fichier de port introuvable
    )
    goto menu_display
)

echo ❌ Choix invalide, veuillez entrer 1, 2, 3, 4 ou 5
goto menu

:menu_display
echo.
echo ==========================================
echo   CHOISISSEZ UN MODE DE TEST
echo ==========================================
echo 1^) Client Console ^(Terminal^)
echo 2^) Client GUI Simple ^(Swing^)
echo 3^) Client GUI Complet ^(Swing + Contacts^)
echo 4^) Arrêter le serveur
echo 5^) Test Multi-Clients ^(3 instances GUI^)
echo.
goto menu