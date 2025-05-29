package com.chat.ui;

import com.chat.entity.Contact;

import com.chat.service.ContactService;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ImprovedChatUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField receiverField;
    private JTextField emailField;
    private JButton connectButton;
    private JButton sendButton;
    private JButton refreshUsersButton;
    private JList<String> usersList;
    private DefaultListModel<String> usersListModel;
    private JTable contactsTable;
    private DefaultTableModel contactsTableModel;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String userEmail;
    private ContactService contactService;
    private boolean isConnected = false;

    public ImprovedChatUI() {
        super("Java Chat Application - Complete GUI");
        this.contactService = new ContactService();
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Chat tab
        JPanel chatPanel = createChatPanel();
        tabbedPane.addTab("💬 Chat", chatPanel);

        // Contacts tab
        JPanel contactsPanel = createContactsPanel();
        tabbedPane.addTab("📇 Contacts", contactsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        JLabel statusBar = new JLabel("Disconnected - Enter your email to connect");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusBar, BorderLayout.SOUTH);

        setSize(900, 700);
        setLocationRelativeTo(null);

        // Load contacts on startup
        loadContacts();
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Connection panel
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBorder(new TitledBorder("Connection"));

        emailField = new JTextField(20);
        emailField.setToolTipText("Enter your email address (e.g., user@domain.com)");
        connectButton = new JButton("Connect");

        connectionPanel.add(new JLabel("Email:"));
        connectionPanel.add(emailField);
        connectionPanel.add(connectButton);

        panel.add(connectionPanel, BorderLayout.NORTH);

        // Center panel with chat and users
        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));

        // Chat area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(new TitledBorder("Messages"));

        chatArea = new JTextArea(15, 40);
        chatArea.setEditable(false);
        chatArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        chatPanel.add(chatScroll, BorderLayout.CENTER);
        centerPanel.add(chatPanel, BorderLayout.CENTER);

        // Online users list
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBorder(new TitledBorder("Online Users"));
        usersPanel.setPreferredSize(new Dimension(200, 0));

        usersListModel = new DefaultListModel<>();
        usersList = new JList<>(usersListModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersList.getSelectedValue() != null) {
                String selectedUser = usersList.getSelectedValue();
                if (selectedUser.contains(" (")) {
                    String email = selectedUser.substring(0, selectedUser.indexOf(" ("));
                    receiverField.setText(email);
                }
            }
        });

        JScrollPane usersScroll = new JScrollPane(usersList);
        refreshUsersButton = new JButton("🔄 Refresh");
        refreshUsersButton.setEnabled(false);

        usersPanel.add(usersScroll, BorderLayout.CENTER);
        usersPanel.add(refreshUsersButton, BorderLayout.SOUTH);
        centerPanel.add(usersPanel, BorderLayout.EAST);

        panel.add(centerPanel, BorderLayout.CENTER);

        // Message sending panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messagePanel.setBorder(new TitledBorder("Send Message"));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        receiverField = new JTextField(15);
        receiverField.setToolTipText("Recipient's email");
        receiverField.setEnabled(false);

        messageField = new JTextField();
        messageField.setToolTipText("Type your message here...");
        messageField.setEnabled(false);

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        JPanel receiverPanel = new JPanel(new BorderLayout());
        receiverPanel.add(new JLabel("To: "), BorderLayout.WEST);
        receiverPanel.add(receiverField, BorderLayout.CENTER);

        inputPanel.add(receiverPanel, BorderLayout.NORTH);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        messagePanel.add(inputPanel, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createContactsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Contacts table
        String[] columnNames = {"ID", "Last Name", "First Name", "Email", "Phone", "Address"};
        contactsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };

        contactsTable = new JTable(contactsTableModel);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        contactsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        contactsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        contactsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        contactsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        contactsTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane tableScroll = new JScrollPane(contactsTable);
        panel.add(tableScroll, BorderLayout.CENTER);

        // CRUD buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("➕ Add");
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        JButton chatButton = new JButton("💬 Start Chat");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(chatButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers for CRUD buttons
        addButton.addActionListener(e -> showContactDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = contactsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int contactId = (Integer) contactsTableModel.getValueAt(selectedRow, 0);
                Contact contact = contactService.getContactById(contactId);
                showContactDialog(contact);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a contact to edit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = contactsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int contactId = (Integer) contactsTableModel.getValueAt(selectedRow, 0);
                String contactName = contactsTableModel.getValueAt(selectedRow, 1) + " " +
                        contactsTableModel.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete the contact: " + contactName + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (contactService.deleteContact(contactId)) {
                        loadContacts();
                        JOptionPane.showMessageDialog(this, "Contact deleted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting contact.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
            }
        });

        chatButton.addActionListener(e -> {
            int selectedRow = contactsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String email = (String) contactsTableModel.getValueAt(selectedRow, 3);
                receiverField.setText(email);
                // Switch to chat tab
                ((JTabbedPane) getContentPane().getComponent(0)).setSelectedIndex(0);
                messageField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a contact to start a chat.");
            }
        });

        return panel;
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

        refreshUsersButton.addActionListener(e -> requestUserList());

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
                chatArea.append("✅ Connected as: " + userEmail + "\n");

                // Thread to receive messages
                Thread receiver = new Thread(this::receiveMessages);
                receiver.setDaemon(true);
                receiver.start();

                // Request user list
                requestUserList();

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
                chatArea.append("❌ Connection lost with the server.\n");
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
                chatArea.append("💬 " + sender + ": " + content + "\n");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        } else if (message.startsWith("USER_LIST:")) {
            String userListData = message.substring(10);
            updateUsersList(userListData);
        }
    }

    private void updateUsersList(String userListData) {
        usersListModel.clear();
        if (!userListData.isEmpty()) {
            String[] users = userListData.split(";");
            for (String userData : users) {
                if (!userData.trim().isEmpty()) {
                    String[] userParts = userData.split(",");
                    if (userParts.length >= 2) {
                        String email = userParts[0];
                        String name = userParts[1];
                        if (!email.equals(userEmail)) { // Do not display current user
                            usersListModel.addElement(email + " (" + name + ")");
                        }
                    }
                }
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
            chatArea.append("📤 You -> " + receiver + ": " + content + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            messageField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to send message: " + e.getMessage(), "Send Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void requestUserList() {
        if (isConnected && out != null) {
            out.println("REQUEST_USERS");
        }
    }

    private void disconnectFromServer() {
        try {
            isConnected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            updateUIDisconnected();
            chatArea.append("🔌 Disconnected from server.\n");
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
        refreshUsersButton.setEnabled(true);
        messageField.requestFocus();
    }

    private void updateUIDisconnected() {
        isConnected = false;
        connectButton.setText("Connect");
        emailField.setEnabled(true);
        receiverField.setEnabled(false);
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        refreshUsersButton.setEnabled(false);
        usersListModel.clear();
        receiverField.setText("");
        messageField.setText("");
    }

    private void showContactDialog(Contact contact) {
        JDialog dialog = new JDialog(this, contact == null ? "Add Contact" : "Edit Contact", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField lastNameField = new JTextField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField emailFieldDialog = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        if (contact != null) {
            lastNameField.setText(contact.getNom());
            firstNameField.setText(contact.getPrenom());
            emailFieldDialog.setText(contact.getEmail());
            phoneField.setText(contact.getTelephone());
            addressField.setText(contact.getAdresse());
        }

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailFieldDialog, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String lastName = lastNameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String email = emailFieldDialog.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Last Name, First Name, and Email are required.");
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Invalid email format.");
                return;
            }

            Contact newContact = new Contact(lastName, firstName, email, phone, address);
            boolean success;

            if (contact == null) {
                success = contactService.addContact(newContact);
            } else {
                newContact.setId(contact.getId());
                success = contactService.updateContact(newContact);
            }

            if (success) {
                loadContacts();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Contact " + (contact == null ? "added" : "updated") + " successfully.");
            } else {
                JOptionPane.showMessageDialog(dialog, "Error " + (contact == null ? "adding" : "updating") + " contact.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadContacts() {
        contactsTableModel.setRowCount(0);
        List<Contact> contacts = contactService.getAllContacts();
        for (Contact contact : contacts) {
            Object[] row = {
                    contact.getId(),
                    contact.getNom(),
                    contact.getPrenom(),
                    contact.getEmail(),
                    contact.getTelephone(),
                    contact.getAdresse()
            };
            contactsTableModel.addRow(row);
        }
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
            new ImprovedChatUI().setVisible(true);
        });
    }
}