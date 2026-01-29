package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.AdminDatabase;
import model.AdminModel;

public class AdminSignupFrame extends JFrame {
    private JTextField usernameField, nameField, emailField, mobileField;
    private JPasswordField passwordField;
    private AdminDatabase adminDatabase;

    public AdminSignupFrame() {
        adminDatabase = new AdminDatabase();
        createUI();
    }

    private void createUI() {
        setTitle("📝 Admin Signup");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // 🔹 Title
        JLabel titleLabel = new JLabel("Create Admin Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 🔹 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 250));
        formPanel.setBorder(BorderFactory.createTitledBorder("Admin Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        mobileField = new JTextField(20);

        String[] labels = {"👤 Username:", "🔒 Password:", "🧑 Full Name:", "✉️ Email:", "📱 Mobile:"};
        JTextField[] fields = {usernameField, passwordField, nameField, emailField, mobileField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // 🔹 Button Panel
        JButton signupButton = new JButton("✅ Signup");
        JButton cancelButton = new JButton("❌ Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);

        // Style buttons
        JButton[] buttons = {signupButton, cancelButton};
        for (JButton btn : buttons) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(33, 150, 243));
            btn.setForeground(Color.WHITE);
        }

        // Add panels to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        signupButton.addActionListener(e -> performSignup());
        cancelButton.addActionListener(e -> dispose());
    }

    private void performSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String mobile = mobileField.getText().trim();

        if (username.length() > 50 || !username.matches("[a-zA-Z0-9]+")) {
            showError("❌ Invalid username. Use only alphanumeric characters (max 50).");
            return;
        }

        if (password.length() > 20) {
            showError("❌ Password too long. Max 20 characters allowed.");
            return;
        }

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
            showError("❌ Please fill in all fields.");
            return;
        }

        if (!adminDatabase.isEmailUnique(email)) {
            showError("❌ This email is already registered.");
            return;
        }

        String encryptedPassword = AdminDatabase.encrypt(password);
        AdminModel newAdmin = new AdminModel(username, encryptedPassword, name, email, mobile);
        adminDatabase.addAdmin(newAdmin);

        JOptionPane.showMessageDialog(this,
                "🎉 Signup successful! You can now login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
