package com.chat.ui;

import com.chat.service.ContactService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChatUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField receiverField;
    private JTextField emailField;
    private JButton connectButton;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String userEmail;

    private boolean isConnected = false;

    public ChatUI() {
        super("Email Chat - Simple GUI");
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connection panel
        JPanel connectionPanel = new JPanel(new FlowLayout());
        connectionPanel.setBorder(new TitledBorder("Connection"));

        emailField = new JTextField(20);
        emailField.setToolTipText("Enter your email address");
        connectButton = new JButton("Connect");

        connectionPanel.add(new JLabel("Email:"));
        connectionPanel.add(emailField);
        connectionPanel.add(connectButton);

        add(connectionPanel, BorderLayout.NORTH);

        // Chat area
        chatArea = new JTextArea(15, 40);
        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(chatScroll, BorderLayout.CENTER);

        // Message sending panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new TitledBorder("Send Message"));

        receiverField = new JTextField(15);
        receiverField.setToolTipText("Recipient's email");
        receiverField.setEnabled(false);

        messageField = new JTextField();
        messageField.setToolTipText("Type your message here");
        messageField.setEnabled(false);

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("To: "), BorderLayout.WEST);
        inputPanel.add(receiverField, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(messageField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);

        messagePanel.add(inputPanel, BorderLayout.NORTH);
        messagePanel.add(sendPanel, BorderLayout.CENTER);
        add(messagePanel, BorderLayout.SOUTH);

        setSize(600, 500);
        setLocationRelativeTo(null);
    }

    private void setupEventHandlers() {
        connectButton.addActionListener(e -> {
            if (!isConnected) {
                connectToServer();
            } else {
                disconnectFromServer();
            }
        });

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectFromServer();
                System.exit(0);
            }
        });
    }

    private void connectToServer() {
        userEmail = emailField.getText().trim();
        if (!isValidEmail(userEmail)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int port = readPortFromFile();
            socket = new Socket("localhost", port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Authentication
            out.println(userEmail);
            String response = in.readLine();

            if (response.startsWith("SUCCESS")) {
                isConnected = true;
                updateUIConnected();
                chatArea.append("Connected as: " + userEmail + "\n");

                // Thread to receive messages
                Thread receiver = new Thread(this::receiveMessages);
                receiver.setDaemon(true);
                receiver.start();

            } else {
                JOptionPane.showMessageDialog(this, "Authentication error: " + response, "Connection Failed", JOptionPane.ERROR_MESSAGE);
                socket.close();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to the server.\nEnsure the server is running.\nError: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int readPortFromFile() throws IOException {
        String portFile = "runtime/current-port.txt";
        if (Files.exists(Paths.get(portFile))) {
            String portStr = new String(Files.readAllBytes(Paths.get(portFile))).trim();
            return Integer.parseInt(portStr);
        } else {
            return 12345; // Default port
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> handleReceivedMessage(finalMessage));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                chatArea.append("Connection lost with the server.\n");
                updateUIDisconnected();
            });
        }
    }

    private void handleReceivedMessage(String message) {
        if (message.startsWith("MESSAGE:")) {
            String[] parts = message.substring(8).split(":", 2);
            if (parts.length == 2) {
                String sender = parts[0];
                String content = parts[1];
                chatArea.append(sender + ": " + content + "\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        }
    }

    private void sendMessage() {
        String receiver = receiverField.getText().trim();
        String content = messageField.getText().trim();

        if (!isValidEmail(receiver)) {
            JOptionPane.showMessageDialog(this, "Invalid recipient email.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a message.", "Empty Message", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (receiver.equals(userEmail)) {
            JOptionPane.showMessageDialog(this, "You cannot send a message to yourself.", "Invalid Recipient", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            out.println("MSG:" + receiver + ":" + content);
            chatArea.append("You -> " + receiver + ": " + content + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            messageField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to send message: " + e.getMessage(), "Send Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromServer() {
        try {
            isConnected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            updateUIDisconnected();
            chatArea.append("Disconnected from server.\n");
        } catch (IOException e) {
            System.err.println("Error during disconnection: " + e.getMessage());
        }
    }

    private void updateUIConnected() {
        connectButton.setText("Disconnect");
        emailField.setEnabled(false);
        receiverField.setEnabled(true);
        messageField.setEnabled(true);
        sendButton.setEnabled(true);
        messageField.requestFocus();
    }

    private void updateUIDisconnected() {
        isConnected = false;
        connectButton.setText("Connect");
        emailField.setEnabled(true);
        receiverField.setEnabled(false);
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        receiverField.setText("");
        messageField.setText("");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            new ChatUI().setVisible(true);
        });
    }
}