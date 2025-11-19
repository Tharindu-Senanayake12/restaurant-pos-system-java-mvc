package view;

import controller.StatisticsController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {
        setTitle("🍽️ Restaurant Admin Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createNavigationPanel(), BorderLayout.WEST);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(220, 53, 69));
        navPanel.setPreferredSize(new Dimension(240, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("The Choice", JLabel.CENTER);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(logoLabel);

        navPanel.add(Box.createVerticalStrut(30));
        
        JButton addProductBtn = createNavButton("Add Product", "/images/add_product_icon.png", "Add a new product to the menu");
        addProductBtn.addActionListener(e -> new AddProductFrame().setVisible(true));
        navPanel.add(addProductBtn);
        navPanel.add(Box.createVerticalStrut(15));
        
        JButton manageProductsBtn = createNavButton("Manage Products", "/images/manage_products_icon.png", "Edit, delete, or search for products");
        manageProductsBtn.addActionListener(e -> new ManageProductsFrame().setVisible(true));
        navPanel.add(manageProductsBtn);
        navPanel.add(Box.createVerticalStrut(15));

        JButton viewAllProductsBtn = createNavButton("View All Products", "/images/view_products_icon.png", "View a catalog of all products");
        viewAllProductsBtn.addActionListener(e -> new ViewAllProductsFrame().setVisible(true));
        navPanel.add(viewAllProductsBtn);
        navPanel.add(Box.createVerticalStrut(15));

        // --- NEW BUTTON ADDED HERE ---
        JButton viewAllOrdersBtn = createNavButton("View All Orders", "/images/orders_icon.png", "View and manage all customer orders");
        viewAllOrdersBtn.addActionListener(e -> new ViewAllOrdersFrame().setVisible(true));
        navPanel.add(viewAllOrdersBtn);

        navPanel.add(Box.createVerticalGlue());
        
        JButton logoutBtn = createNavButton("Logout", "/images/logout_icon.png", "Logout from the system");
        logoutBtn.addActionListener(e -> {
            // A better implementation would be to close this frame and open a login frame
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if(response == JOptionPane.YES_OPTION) {
                this.dispose(); 
                // In a real app: new LoginFrame().setVisible(true);
            }
        });
        navPanel.add(logoutBtn);
        navPanel.add(Box.createVerticalStrut(20));

        return navPanel;
    }

    private JPanel createMainPanel() {
        // This method remains unchanged.
        StatisticsController stats = new StatisticsController();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Welcome Back, Admin!", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(220, 53, 69));
        headerPanel.add(title, BorderLayout.WEST);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridx = 0;
        statsPanel.add(createStatCard("Menu Items", String.valueOf(stats.getTotalProducts()), "🍽️", "Total number of menu items"), gbc);
        gbc.gridx = 1;
        statsPanel.add(createStatCard("Today's Sales", "LKR " + stats.getTodaysRevenue(), "💵", "Total revenue earned today"), gbc);
        gbc.gridx = 2;
        statsPanel.add(createStatCard("Orders", String.valueOf(stats.getTotalOrders()), "🛎️", "Total orders received today"), gbc);

        mainPanel.add(statsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel popularPanel = new JPanel(new BorderLayout());
        popularPanel.setBackground(Color.WHITE);
        popularPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 53, 69), 1, true),
                "🌟 Top 5 Best-Selling Dishes", 0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(220, 53, 69)));

        JTextArea productList = new JTextArea();
        productList.setEditable(false);
        productList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        productList.setMargin(new Insets(15, 20, 15, 20));

        List<String> popular = stats.getPopularProducts();
        for (int i = 0; i < popular.size(); i++) {
            productList.append((i + 1) + ". " + popular.get(i) + "\n");
        }
        popularPanel.add(new JScrollPane(productList), BorderLayout.CENTER);

        mainPanel.add(popularPanel);
        return mainPanel;
    }

    private JButton createNavButton(String text, String iconPath, String tooltip) {
        // This method remains unchanged.
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(192, 40, 54));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setToolTipText(tooltip);
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch(Exception e) { /* Icon not found, button will just have text */ }
        
        return button;
    }

    private JPanel createStatCard(String title, String value, String emoji, String tooltip) {
        // This method remains unchanged.
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(255, 243, 243));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 199, 199), 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setToolTipText(tooltip);
        
        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(new Color(220, 53, 69));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(titleLabel);

        card.add(icon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    public static void main(String[] args) {
        // This requires the other frame classes (AddProductFrame etc.) to exist
        // For standalone testing, you can comment out their action listeners.
        SwingUtilities.invokeLater(() -> new AdminDashboardFrame().setVisible(true));
    }
}