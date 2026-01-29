package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.User;
import model.UserManager;

public class CustomerLogin extends JFrame {
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    private JTextField regNameField, regEmailField, regMobileField, regAddressField, regUsernameField;
    private JPasswordField regPasswordField;

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private UserManager userManager = UserManager.getInstance();

    public CustomerLogin() {
        setTitle("🍽️ Customer Login & Registration");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createRegistrationPanel(), "register");

        add(mainPanel);
        showLogin();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Customer Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);

        loginUsernameField = new JTextField(20);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(loginUsernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);

        loginPasswordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(loginPasswordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(220, 53, 69));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(loginButton, gbc);

        JButton newUserButton = new JButton("New User? Register Here");
        newUserButton.setBorderPainted(false);
        newUserButton.setContentAreaFilled(false);
        newUserButton.setForeground(new Color(220, 53, 69));
        newUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newUserButton.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        gbc.gridy = 4;
        panel.add(newUserButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        newUserButton.addActionListener(e -> showRegister());

        return panel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all login fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (username.length() > 50) {
            JOptionPane.showMessageDialog(this, "Username must be max 50 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPwd = hashPassword(password);

        if (!userManager.validateCredentials(username, hashedPwd)) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userManager.setLoggedInUser(userManager.getUser(username));
        JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Open dashboard and close login frame
        SwingUtilities.invokeLater(() -> {
            CustomerDashboard dashboard = new CustomerDashboard();
            dashboard.setVisible(true);
        });
        this.dispose();
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("New User Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 53, 69));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.EAST;

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        regNameField = new JTextField(20);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(regNameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(emailLabel, gbc);
        regEmailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(regEmailField, gbc);

        JLabel mobileLabel = new JLabel("Mobile Number:");
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(mobileLabel, gbc);
        regMobileField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(regMobileField, gbc);

        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(addressLabel, gbc);
        regAddressField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(regAddressField, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(usernameLabel, gbc);
        regUsernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(regUsernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(passwordLabel, gbc);
        regPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(regPasswordField, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(220, 53, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(registerButton, gbc);

        JButton backToLoginBtn = new JButton("Back to Login");
        backToLoginBtn.setBorderPainted(false);
        backToLoginBtn.setContentAreaFilled(false);
        backToLoginBtn.setForeground(new Color(220, 53, 69));
        backToLoginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToLoginBtn.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        gbc.gridy = 8;
        panel.add(backToLoginBtn, gbc);

        registerButton.addActionListener(e -> handleRegistration());
        backToLoginBtn.addActionListener(e -> showLogin());

        return panel;
    }

    private void handleRegistration() {
        String name = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String mobile = regMobileField.getText().trim();
        String address = regAddressField.getText().trim();
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all registration fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userManager.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!mobile.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Mobile number must be numeric and 10-15 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be min 8 chars, include uppercase, lowercase, digit, and special char.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pwdHash = hashPassword(password);

        User newUser = new User(0, name, email, mobile, address, username, pwdHash, "customer");
        userManager.addUser(newUser);

        JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        showLogin();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-\\.]+\\.[a-zA-Z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        String specials = "!@#$%^&*()_+[]{};:'\"\\|,.<>/?-=";
        boolean hasSpecial = password.matches(".*[" + Pattern.quote(specials) + "].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showLogin() {
        clearLoginFields();
        cardLayout.show(mainPanel, "login");
    }

    private void showRegister() {
        clearRegistrationFields();
        cardLayout.show(mainPanel, "register");
    }

    private void clearLoginFields() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
    }

    private void clearRegistrationFields() {
        regNameField.setText("");
        regEmailField.setText("");
        regMobileField.setText("");
        regAddressField.setText("");
        regUsernameField.setText("");
        regPasswordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerLogin login = new CustomerLogin();
            login.setVisible(true);
        });
    }
}
