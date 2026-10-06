import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class ChatApplication {

    // Store all registered users
    private ArrayList<User> users = new ArrayList<>();

    // Store all messages
    private ArrayList<Message> messages = new ArrayList<>();

    // Current logged-in user
    private User currentUser;

    // GUI components
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    // --------------------------------------------------
    // START APPLICATION
    // --------------------------------------------------

    public void start() {
        loadUsers();
        loadMessages();
        showLoginScreen();
    }

    // --------------------------------------------------
    // LOGIN SCREEN
    // --------------------------------------------------

    private void showLoginScreen() {

        frame = new JFrame("Chat Application");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = new JLabel("CHAT APPLICATION");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> login());

        registerButton.addActionListener(e -> register());

        frame.add(panel);
        frame.setVisible(true);
    }

    // --------------------------------------------------
    // REGISTER
    // --------------------------------------------------

    private void register() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter username and password."
            );

            return;
        }

        // Check duplicate username
        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username)) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Username already exists!"
                );

                return;
            }
        }

        // Create new user
        User newUser = new User(username, password);

        users.add(newUser);

        saveUsers(); 

        JOptionPane.showMessageDialog(
                frame,
                "Registration successful!\nYou can now login."
        );

        usernameField.setText("");
        passwordField.setText("");
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        for (User user : users) {

            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {

                currentUser = user;

                showChatScreen();

                return;
            }
        }

        JOptionPane.showMessageDialog(
                frame,
                "Invalid username or password!"
        );
    }

    // --------------------------------------------------
    // CHAT SCREEN
    // --------------------------------------------------

    private void showChatScreen() {

        frame.getContentPane().removeAll();

        frame.setTitle(
                "Chat Application - " + currentUser.getUsername()
        );

        frame.setSize(800, 550);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // ------------------------------------------
        // LEFT SIDE - USER LIST
        // ------------------------------------------

        JPanel leftPanel = new JPanel(new BorderLayout());

        JLabel usersLabel = new JLabel("  USERS");
        usersLabel.setFont(new Font("Arial", Font.BOLD, 18));

        DefaultListModel<String> userListModel =
                new DefaultListModel<>();

        for (User user : users) {

            if (!user.getUsername()
                    .equals(currentUser.getUsername())) {

                userListModel.addElement(user.getUsername());
            }
        }

        JList<String> userList =
                new JList<>(userListModel);

        userList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        leftPanel.add(usersLabel, BorderLayout.NORTH);
        leftPanel.add(
                new JScrollPane(userList),
                BorderLayout.CENTER
        );

        // ------------------------------------------
        // CENTER - CHAT AREA
        // ------------------------------------------

        JPanel chatPanel = new JPanel(new BorderLayout());

        JLabel chatTitle = new JLabel(
                "  Select a user to start chatting"
        );

        chatTitle.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JTextArea chatArea = new JTextArea();

        chatArea.setEditable(false);

        chatArea.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        JScrollPane chatScroll =
                new JScrollPane(chatArea);

        // ------------------------------------------
        // MESSAGE AREA
        // ------------------------------------------

        JPanel messagePanel = new JPanel(new BorderLayout());

        JTextField messageField =
                new JTextField();

        JButton sendButton =
                new JButton("SEND");

        messagePanel.add(
                messageField,
                BorderLayout.CENTER
        );

        messagePanel.add(
                sendButton,
                BorderLayout.EAST
        );

        chatPanel.add(
                chatTitle,
                BorderLayout.NORTH
        );

        chatPanel.add(
                chatScroll,
                BorderLayout.CENTER
        );

        chatPanel.add(
                messagePanel,
                BorderLayout.SOUTH
        );

        // ------------------------------------------
        // LOGOUT BUTTON
        // ------------------------------------------

        JButton logoutButton =
                new JButton("Logout");

        JPanel bottomPanel =
                new JPanel(new BorderLayout());

        bottomPanel.add(
                logoutButton,
                BorderLayout.EAST
        );

        // ------------------------------------------
        // SELECT USER
        // ------------------------------------------

        userList.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && userList.getSelectedValue() != null) {

                String selectedUser =
                        userList.getSelectedValue();

                chatTitle.setText(
                        "  Chat with " + selectedUser
                );

                displayMessages(
                        chatArea,
                        currentUser.getUsername(),
                        selectedUser
                );
            }
        });

        // ------------------------------------------
        // SEND MESSAGE
        // ------------------------------------------

        sendButton.addActionListener(e -> {

            String selectedUser =
                    userList.getSelectedValue();

            if (selectedUser == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a user first."
                );

                return;
            }

            String text =
                    messageField.getText().trim();

            if (text.isEmpty()) {
                return;
            }

            Message message =
                    new Message(
                            currentUser.getUsername(),
                            selectedUser,
                            text
                    );

            messages.add(message);

            saveMessages();

            messageField.setText("");

            displayMessages(
                    chatArea,
                    currentUser.getUsername(),
                    selectedUser
            );
        });

        // Allow Enter key to send message
        messageField.addActionListener(e ->
                sendButton.doClick()
        );

        // ------------------------------------------
        // LOGOUT
        // ------------------------------------------

        logoutButton.addActionListener(e -> {

            currentUser = null;

            frame.getContentPane().removeAll();

            showLoginScreen();
        });

        // ------------------------------------------
        // ADD PANELS
        // ------------------------------------------

        mainPanel.add(
                leftPanel,
                BorderLayout.WEST
        );

        mainPanel.add(
                chatPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        frame.add(mainPanel);

        frame.revalidate();
        frame.repaint();
        frame.setLocationRelativeTo(null);
    }

    // --------------------------------------------------
    // DISPLAY MESSAGES
    // --------------------------------------------------

    private void displayMessages(
            JTextArea chatArea,
            String user1,
            String user2) {

        chatArea.setText("");

        for (Message message : messages) {

            boolean sameChat =
                    (message.getSender().equals(user1)
                            && message.getReceiver().equals(user2))
                    ||
                    (message.getSender().equals(user2)
                            && message.getReceiver().equals(user1));

            if (sameChat) {

                chatArea.append(
                        message.getSender()
                                + ": "
                                + message.getText()
                                + "\n\n"
                );
                 }
        }
    }
                // --------------------------------------------------
// SAVE USERS TO FILE
// --------------------------------------------------

private void saveUsers() {

    try {

        BufferedWriter writer =
                new BufferedWriter(
                        new FileWriter("users.txt")
                );

        for (User user : users) {

            writer.write(
                    user.getUsername()
                            + "|"
                            + user.getPassword()
            );

            writer.newLine();
        }

        writer.close();

    } catch (IOException e) {

        System.out.println("Error saving users!");
    }
}
// --------------------------------------------------
// LOAD USERS FROM FILE
// --------------------------------------------------

private void loadUsers() {

    File file = new File("users.txt");

    if (!file.exists()) {
        return;
    }

    try {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(file)
                );

        String line;

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split("\\|", 2);

            if (parts.length == 2) {

                User user =
                        new User(parts[0], parts[1]);

                users.add(user);
            }
        }

        reader.close();

    } catch (IOException e) {

        System.out.println("Error loading users!");
    }
}
// --------------------------------------------------
// SAVE MESSAGES TO FILE
// --------------------------------------------------

private void saveMessages() {

    try {

        BufferedWriter writer =
                new BufferedWriter(
                        new FileWriter("messages.txt")
                );

        for (Message message : messages) {

            writer.write(
                    message.getSender()
                            + "|"
                            + message.getReceiver()
                            + "|"
                            + message.getText()
            );

            writer.newLine();
        }

        writer.close();

    } catch (IOException e) {

        System.out.println("Error saving messages!");
    }
}
// --------------------------------------------------
// LOAD MESSAGES FROM FILE
// --------------------------------------------------

private void loadMessages() {

    File file = new File("messages.txt");

    if (!file.exists()) {
        return;
    }

    try {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(file)
                );

        String line;

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split("\\|", 3);

            if (parts.length == 3) {

                Message message =
                        new Message(
                                parts[0],
                                parts[1],
                                parts[2]
                        );

                messages.add(message);
            }
        }

        reader.close();

    } catch (IOException e) {

        System.out.println("Error loading messages!");
    }
}
           
}