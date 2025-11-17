package view;

import java.awt.*;
import javax.swing.*;

public class AdminInterface extends JFrame {
    public AdminInterface() {
        setTitle("Admin Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BorderLayout());

        // Heading Label
        JLabel heading = new JLabel("Welcome to the Admin Portal", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        // Center Panel with buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        JButton loginButton = new JButton("Login to Admin Account");
        JButton signupButton = new JButton("Register New Admin");

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Footer/Note
        JLabel note = new JLabel("<html><center>Use your credentials to login,<br>or register to create a new admin account.</center></html>", SwingConstants.CENTER);
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        note.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        add(note, BorderLayout.SOUTH);

        // Button Actions
        loginButton.addActionListener(e -> {
            AdminLoginFrame loginFrame = new AdminLoginFrame();
            loginFrame.setVisible(true);
        });

        signupButton.addActionListener(e -> {
            AdminSignupFrame signupFrame = new AdminSignupFrame();
            signupFrame.setVisible(true);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminInterface::new);
    }
}
