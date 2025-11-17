package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.AdminDatabase;

public class AdminLoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AdminDatabase adminDatabase;

    public AdminLoginFrame() {
        adminDatabase = new AdminDatabase();

        setTitle("Admin Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        setResizable(false);

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        add(loginButton);
        add(cancelButton);

        JLabel forgotLabel = new JLabel("<HTML><U>Forgot Password?</U></HTML>");
        forgotLabel.setForeground(Color.BLUE);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(forgotLabel);

        loginButton.addActionListener(e -> performLogin());
        cancelButton.addActionListener(e -> dispose());
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openForgotPasswordDialog();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (adminDatabase.validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            new AdminDashboardFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openForgotPasswordDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Reset Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());

            if (adminDatabase.updatePassword(username, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Username not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
