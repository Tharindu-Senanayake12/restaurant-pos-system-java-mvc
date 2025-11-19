package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URL;

public class AddProductFrame extends JFrame {

    // --- UI Styling Constants ---
    private static final Color COLOR_THEME_RED = new Color(220, 53, 69);
    private static final Color COLOR_BACKGROUND_SOFT = new Color(253, 250, 250);
    private static final Color COLOR_TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color COLOR_TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color COLOR_BORDER = new Color(206, 212, 218);
    
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 16);

    // --- Form Components ---
    private final ModernTextField nameField, priceField, stockField;
    private final JTextArea descriptionArea;
    private final JComboBox<String> categoryCombo, statusCombo;
    private final JLabel imagePreviewLabel;
    private final JLabel imageNameLabel;
    
    private File selectedImageFile;
    private static final int VERTICAL_GAP = 25; // Consistent spacing between form rows

    public AddProductFrame() {
        setTitle("Admin: Add New Product");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- Left Panel for the Image ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.BLACK);
        
        URL imageUrl = getClass().getResource("/images/b.jfif");
        if (imageUrl != null) {
            ImageIcon rawIcon = new ImageIcon(imageUrl);
            JLabel imageHolder = new JLabel(new ImageIcon(rawIcon.getImage().getScaledInstance(450, 1000, Image.SCALE_SMOOTH)));
            leftPanel.add(imageHolder, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Error: Image '/images/b.jfif' not found.", SwingConstants.CENTER);
            errorLabel.setForeground(Color.WHITE);
            leftPanel.add(errorLabel, BorderLayout.CENTER);
            System.err.println("CRITICAL ERROR: Could not find the background image at '/images/b.jfif'");
        }

        // --- Right Panel for the Form ---
        JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
        rightPanel.setBackground(COLOR_BACKGROUND_SOFT);
        rightPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel titleLabel = new JLabel("Add New Menu Item");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_THEME_RED);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        // --- NEW: Single-column form panel using BoxLayout ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // --- Fields Initialization ---
        nameField = new ModernTextField("e.g., Spicy Chicken Kottu");
        categoryCombo = createStyledComboBox(new String[]{"Appetizers", "Main Course", "Beverages", "Desserts"});
        priceField = new ModernTextField("e.g., 1200.00");
        stockField = new ModernTextField("e.g., 50");
        statusCombo = createStyledComboBox(new String[]{"Active", "Inactive"});
        descriptionArea = new JTextArea(5, 30); // Adjusted rows for single column
        imagePreviewLabel = new JLabel("<html><center>Click to<br>Upload Image</center></html>", SwingConstants.CENTER);
        imageNameLabel = new JLabel("No file selected.");

        // Add components vertically with consistent spacing
        formPanel.add(createFormRow("Product Name", nameField));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
        formPanel.add(createFormRow("Category", categoryCombo));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));

        // Description Area Setup
        descriptionArea.setFont(FONT_INPUT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            new CustomLineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        styleScrollPane(descScroll);
        formPanel.add(createFormRow("Description", descScroll));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
        
        formPanel.add(createFormRow("Price (LKR)", priceField));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
        
        formPanel.add(createFormRow("Stock Quantity", stockField));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));

        formPanel.add(createFormRow("Status", statusCombo));
        formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
        
        formPanel.add(createFormRow("Product Image", createImageUploadPanel()));
        
        // Add a filler to push all content to the top
        formPanel.add(Box.createVerticalGlue());

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        styleScrollPane(formScrollPane);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(formScrollPane, BorderLayout.CENTER);
        
        // --- Bottom Panel for Action Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        bottomPanel.setOpaque(false);
        RoundedButton addButton = new RoundedButton("Add Product", 12);
        addButton.setBackground(COLOR_THEME_RED);
        addButton.setForeground(Color.WHITE);
        addButton.setFont(FONT_BUTTON);
        addButton.setPreferredSize(new Dimension(200, 50));
        addButton.addActionListener(e -> addProduct());
        bottomPanel.add(addButton);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- JSplitPane to combine both panels ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);

        setContentPane(splitPane);
        pack();
        setSize(new Dimension(1200, 900));
        setLocationRelativeTo(null);
    }

    private JPanel createFormRow(String labelText, Component field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ensure the field aligns to the left and doesn't stretch wider than necessary
        if (field instanceof JComponent) {
            ((JComponent) field).setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        return panel;
    }

    private JPanel createImageUploadPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT); // Important for BoxLayout

        RoundedButton uploadButton = new RoundedButton("Choose File...", 8);
        uploadButton.setFont(FONT_INPUT);
        uploadButton.setBackground(new Color(233, 236, 239));
        uploadButton.setForeground(COLOR_TEXT_PRIMARY);
        uploadButton.setPreferredSize(new Dimension(140, 40));
        uploadButton.addActionListener(e -> chooseImage());

        imagePreviewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imagePreviewLabel.setForeground(COLOR_TEXT_SECONDARY);
        imagePreviewLabel.setPreferredSize(new Dimension(120, 120));
        imagePreviewLabel.setBorder(new DottedBorder(COLOR_BORDER, 2));
        imagePreviewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        imageNameLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        imageNameLabel.setForeground(COLOR_TEXT_SECONDARY);

        JPanel textAndButtonPanel = new JPanel();
        textAndButtonPanel.setOpaque(false);
        textAndButtonPanel.setLayout(new BoxLayout(textAndButtonPanel, BoxLayout.Y_AXIS));
        textAndButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textAndButtonPanel.add(uploadButton);
        textAndButtonPanel.add(Box.createVerticalStrut(10));
        textAndButtonPanel.add(imageNameLabel);

        panel.add(imagePreviewLabel, BorderLayout.WEST);
        panel.add(textAndButtonPanel, BorderLayout.CENTER);
        
        // Constrain the max size to prevent it from stretching vertically
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
        return panel;
    }

    // --- Unchanged Methods ---

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files (JPG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            long fileSizeMB = selectedImageFile.length() / (1024 * 1024);
            
            if (fileSizeMB > 5) {
                showError("Image file is too large. Maximum size is 5MB.");
                clearImageSelection();
            } else {
                ImageIcon icon = new ImageIcon(selectedImageFile.getPath());
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaled));
                imagePreviewLabel.setText(null);
                imageNameLabel.setText(selectedImageFile.getName());
            }
        }
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();

        if (name.isEmpty() || name.equals(nameField.getPlaceholder())) {
             showError("Product Name is required."); return;
        }
        if (!name.matches("[a-zA-Z0-9\\s]{1,100}")) {
            showError("Product Name must be 1-100 alphanumeric characters."); return;
        }
        if (description.length() > 500) {
            showError("Description cannot exceed 500 characters."); return;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0.01 || price > 99999.99) {
                showError("Price must be between 0.01 and 99999.99."); return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid price format. Please enter a valid number."); return;
        }

        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                showError("Stock Quantity must be a positive integer (0 or more)."); return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid stock format. Please enter a whole number."); return;
        }
        
        if (selectedImageFile == null) {
            showError("Please upload a product image."); return;
        }
        
        JOptionPane.showMessageDialog(this, "Product '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }
    
    private void clearImageSelection() {
        selectedImageFile = null;
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("<html><center>Click to<br>Upload Image</center></html>");
        imageNameLabel.setText("No file selected.");
    }
    
    private void clearForm() {
        nameField.reset();
        priceField.reset();
        stockField.reset();
        descriptionArea.setText("");
        categoryCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        clearImageSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(FONT_INPUT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setUI(new ModernComboBoxUI());
        comboBox.setBorder(new EmptyBorder(5, 5, 5, 5));
        // Constrain size for BoxLayout
        comboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, comboBox.getPreferredSize().height));
        return comboBox;
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND_SOFT);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddProductFrame().setVisible(true));
    }

    // --- CUSTOM UI COMPONENT CLASSES ---

    static class ModernTextField extends JTextField {
        private final String placeholder;
        private boolean hasFocus;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            this.hasFocus = false;
            setText(placeholder);
            setForeground(COLOR_TEXT_SECONDARY);
            setFont(FONT_INPUT);
            setBorder(new EmptyBorder(10, 10, 10, 10));
            // Constrain size for BoxLayout
            setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(COLOR_TEXT_PRIMARY);
                    }
                    hasFocus = true;
                    repaint();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(COLOR_TEXT_SECONDARY);
                    }
                    hasFocus = false;
                    repaint();
                }
            });
        }
        
        public String getPlaceholder() {
            return this.placeholder;
        }

        public void reset() {
            setText(placeholder);
            setForeground(COLOR_TEXT_SECONDARY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hasFocus) {
                g2.setColor(COLOR_THEME_RED);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
            } else {
                g2.setColor(COLOR_BORDER);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
            g2.dispose();
        }
    }

    static class CustomLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final boolean rounded;

        public CustomLineBorder(Color color, int thickness, boolean rounded) {
            this.color = color;
            this.thickness = thickness;
            this.rounded = rounded;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.color);
            g2.setStroke(new BasicStroke(this.thickness));
            if (rounded) {
                g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, 8, 8));
            } else {
                g2.drawRect(x, y, width - 1, height - 1);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    static class DottedBorder extends AbstractBorder {
        private final Color color;
        private final float thickness;

        public DottedBorder(Color color, float thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            Stroke dashed = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
            g2d.setStroke(dashed);
            g2d.draw(new RoundRectangle2D.Double(x + 1, y + 1, width - 3, height - 3, 10, 10));
            g2d.dispose();
        }
    }
    
    static class ModernComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setBorder(new EmptyBorder(0, 0, 0, 10));
            button.setForeground(COLOR_TEXT_SECONDARY);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(Color.WHITE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        protected Insets getInsets() {
            return new Insets(8, 10, 8, 10);
        }
    }

    static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = new Color(245, 245, 245);
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    static class RoundedButton extends JButton {
        private final int radius;
        private Color hoverColor;

        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    hoverColor = getBackground().brighter();
                    repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    hoverColor = null;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (hoverColor != null && getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}