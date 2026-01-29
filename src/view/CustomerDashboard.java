package view; // Ensure this matches your folder structure

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class CustomerDashboard extends JFrame {

    // --- UI Styling Constants ---
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_SIDEBAR = new Color(0xF5F5F7);
    private static final Color COLOR_PRIMARY_TEXT = new Color(0x333333);
    private static final Color COLOR_SECONDARY_TEXT = new Color(0x8A8A8E);
    private static final Color COLOR_ACCENT_PINK = new Color(0xE83D67);
    private static final Color COLOR_ACCENT_BLUE = new Color(0x3B82F6);
    private static final Color COLOR_CARD_DEFAULT_BG = new Color(0xFDFDFD);
    private static final Color COLOR_CARD_BORDER = new Color(0xEAEAEB);
    private static final Color COLOR_CARD_DISABLED_BG = new Color(0xF8F9FA);
    private static final Color COLOR_STOCK_IN = new Color(0x28A745);
    private static final Color COLOR_STOCK_OUT = new Color(0xDC3545);
    private static final Color COLOR_PLACEHOLDER_TEXT = Color.LIGHT_GRAY;

    private static final Font FONT_LOGO = new Font("Serif", Font.BOLD, 28);
    private static final Font FONT_MENU_ITEM = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_HEADER_TITLE = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_PRODUCT_NAME = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_PRODUCT_DETAILS = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_SUMMARY_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_SUMMARY_ITEM = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_STOCK_STATUS = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_ADD_TO_CART = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_FORM_LABEL = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_QUANTITY = new Font("SansSerif", Font.BOLD, 18);

    // --- CardLayout View Names ---
    private static final String MENU_VIEW = "MENU_VIEW";
    private static final String CHECKOUT_VIEW = "CHECKOUT_VIEW";
    private static final String PROFILE_VIEW = "PROFILE_VIEW";

    // --- Data and State ---
    private final List<Product> menuItems = new ArrayList<>();
    private final Map<Product, Integer> currentOrder = new LinkedHashMap<>();
    private final List<Order> pastOrders = new ArrayList<>();
    private static final AtomicInteger orderCounter = new AtomicInteger(1001);

    // --- UI Component References for Dynamic Updates ---
    private final Map<Product, ProductCardUI> productCardUIs = new HashMap<>();

    // --- UI Components ---
    private JPanel cartItemsPanel, mainPanelContainer, checkoutItemsPanel;
    private CardLayout mainPanelLayout;
    private JLabel totalItemsLabel, priceValueLabel, taxesValueLabel, discountValueLabel, totalValueLabel;
    private JLabel checkoutPriceLabel, checkoutTaxesLabel, checkoutTotalLabel;
    private JTextArea orderHistoryArea;
    private JTextField nameField, addressField, phoneField;
    private ButtonGroup paymentGroup;
    private JButton placeOrderButton;

    public CustomerDashboard() {
        initData();
        initUI();
    }

    private void initData() {
        menuItems.add(new Product("Rice and Curry", "Crispy wontons with savory filling.", 450, "/images/1.jpg", 25));
        menuItems.add(new Product("Sausages Pizza", "Grilled premium sausages.", 2300, "/images/b.jfif", 0));
        menuItems.add(new Product("Fried Rice with Pork", "A hearty and flavorful classic.", 1350, "/images/3.jpg", 15));
        menuItems.add(new Product("Chicken Kottu", "A Sri Lankan classic.", 1300, "/images/4.jpg", 30));
        menuItems.add(new Product("Spicy Chicken Biriyani", "Tender chicken in a spicy glaze.", 1500, "/images/5.jpg", 5));
        menuItems.add(new Product("Devilled Chicken Fried Rice", "Spicy fried rice with chicken.", 1200, "/images/6.png", 18));
    }

    private void initUI() {
        setTitle("The Choice - Restaurant POS");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanelLayout = new CardLayout();
        mainPanelContainer = new JPanel(mainPanelLayout);
        mainPanelContainer.add(createMenuAndCartPanel(), MENU_VIEW);
        mainPanelContainer.add(createCheckoutPanel(), CHECKOUT_VIEW);
        mainPanelContainer.add(createProfilePanel(), PROFILE_VIEW);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSidebarPanel(), mainPanelContainer);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);

        add(splitPane);
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        panel.add(new JLabel("Checkout") {{ setFont(FONT_HEADER_TITLE); }}, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createCustomerInfoSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createVerticalStrut(20));

        contentPanel.add(createOrderSummarySection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(new JSeparator());
        contentPanel.add(Box.createVerticalStrut(20));
        
        contentPanel.add(createPaymentMethodSection());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(createTotalsAndPayButtonSection(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerInfoSection() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(new JLabel("Full Name:") {{ setFont(FONT_FORM_LABEL); }}, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = createPlaceholderTextField("Enter your full name");
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Address:") {{ setFont(FONT_FORM_LABEL); }}, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        addressField = createPlaceholderTextField("Enter delivery address");
        formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Phone Number:") {{ setFont(FONT_FORM_LABEL); }}, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        phoneField = createPlaceholderTextField("Enter your phone number");
        formPanel.add(phoneField, gbc);
        
        return formPanel;
    }

    private JPanel createOrderSummarySection() {
        JPanel summarySection = new JPanel(new BorderLayout());
        summarySection.setOpaque(false);
        summarySection.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        summarySection.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        checkoutItemsPanel = new JPanel();
        checkoutItemsPanel.setLayout(new BoxLayout(checkoutItemsPanel, BoxLayout.Y_AXIS));
        checkoutItemsPanel.setBackground(Color.WHITE);
        
        summarySection.add(checkoutItemsPanel, BorderLayout.CENTER);
        return summarySection;
    }

    private JPanel createPaymentMethodSection() {
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setOpaque(false);
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Method"));
        paymentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton cashRadio = new JRadioButton("Cash Payment");
        cashRadio.setOpaque(false);
        cashRadio.setActionCommand("Cash");
        cashRadio.setSelected(true);

        JRadioButton cardRadio = new JRadioButton("Card Payment");
        cardRadio.setOpaque(false);
        cardRadio.setActionCommand("Card");

        JRadioButton walletRadio = new JRadioButton("Digital Wallet");
        walletRadio.setOpaque(false);
        walletRadio.setActionCommand("Digital Wallet");

        paymentGroup = new ButtonGroup();
        paymentGroup.add(cashRadio);
        paymentGroup.add(cardRadio);
        paymentGroup.add(walletRadio);
        
        paymentPanel.add(cashRadio);
        paymentPanel.add(cardRadio);
        paymentPanel.add(walletRadio);
        
        return paymentPanel;
    }

    private JPanel createTotalsAndPayButtonSection() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0; totalsPanel.add(new JLabel("Subtotal"){{setFont(FONT_SUMMARY_ITEM); setForeground(COLOR_SECONDARY_TEXT);}}, gbc);
        gbc.gridy = 1; totalsPanel.add(new JLabel("Taxes (10%)"){{setFont(FONT_SUMMARY_ITEM); setForeground(COLOR_SECONDARY_TEXT);}}, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(10, 0, 10, 0); totalsPanel.add(new JSeparator(), gbc);
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 5, 0); totalsPanel.add(new JLabel("Total"){{setFont(FONT_SUMMARY_TITLE);}}, gbc);

        checkoutPriceLabel = new JLabel("Rs 0.00");
        checkoutTaxesLabel = new JLabel("Rs 0.00");
        checkoutTotalLabel = new JLabel("Rs 0.00");

        gbc.gridx = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 0; gbc.insets = new Insets(5, 0, 5, 0); totalsPanel.add(checkoutPriceLabel, gbc);
        gbc.gridy = 1; totalsPanel.add(checkoutTaxesLabel, gbc);
        gbc.gridy = 2; gbc.insets = new Insets(10, 0, 10, 0); totalsPanel.add(new JSeparator(), gbc);
        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 5, 0); totalsPanel.add(checkoutTotalLabel, gbc);
        
        Font valueFont = FONT_SUMMARY_ITEM.deriveFont(Font.BOLD);
        checkoutPriceLabel.setFont(valueFont); checkoutTaxesLabel.setFont(valueFont);
        checkoutTotalLabel.setFont(FONT_SUMMARY_TITLE); checkoutTotalLabel.setForeground(COLOR_ACCENT_PINK);
        
        panel.add(totalsPanel, BorderLayout.CENTER);

        JButton payButton = new RoundedButton("Pay Now", 12);
        payButton.setBackground(COLOR_STOCK_IN);
        payButton.setForeground(Color.WHITE);
        payButton.setFont(FONT_MENU_ITEM);
        payButton.setPreferredSize(new Dimension(200, 55));
        payButton.addActionListener(e -> processPayment());
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(payButton);
        
        panel.add(buttonWrapper, BorderLayout.SOUTH);
        return panel;
    }

    private void processPayment() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || name.equals("Enter your full name")) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer details before proceeding.", "Incomplete Information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentOrder.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Cannot Pay", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String paymentMethod = paymentGroup.getSelection().getActionCommand();
        double[] totals = calculateTotals();
        
        Order newOrder = new Order(String.valueOf(orderCounter.getAndIncrement()), new HashMap<>(currentOrder), name, address, phone, paymentMethod, totals[2]);
        newOrder.setStatus("Paid");
        pastOrders.add(newOrder);
        
        JOptionPane.showMessageDialog(this, "Payment record created for Order #" + newOrder.orderNumber + " via " + paymentMethod, "Payment Confirmed", JOptionPane.INFORMATION_MESSAGE);

        String receipt = newOrder.getFormattedString();
        JTextArea receiptArea = new JTextArea(receipt);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(receiptArea) {{ setPreferredSize(new Dimension(400, 300)); }};
        JOptionPane.showMessageDialog(this, scrollPane, "Order Receipt", JOptionPane.INFORMATION_MESSAGE);
        
        currentOrder.clear();
        refreshCartView();
        for (Product p : menuItems) {
            refreshProductCard(p);
        }
        nameField.setText(""); addPlaceholder(nameField, "Enter your full name");
        addressField.setText(""); addPlaceholder(addressField, "Enter delivery address");
        phoneField.setText(""); addPlaceholder(phoneField, "Enter your phone number");
        
        mainPanelLayout.show(mainPanelContainer, MENU_VIEW);
    }
    
    private JScrollPane createMenuGridPanel() {
        JPanel menuGridPanel = new JPanel(new GridLayout(0, 3, 25, 25));
        menuGridPanel.setBackground(COLOR_BACKGROUND);
        menuGridPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        for (Product item : menuItems) {
            menuGridPanel.add(createProductCard(item));
        }
        JScrollPane scrollPane = new JScrollPane(menuGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createProductCard(Product product) {
        boolean isAvailable = product.stock > 0;
        Color cardBg = isAvailable ? COLOR_CARD_DEFAULT_BG : COLOR_CARD_DISABLED_BG;
        Color primaryFg = isAvailable ? COLOR_PRIMARY_TEXT : COLOR_SECONDARY_TEXT;

        RoundedPanel card = new RoundedPanel(15, cardBg);
        card.setBorder(BorderFactory.createLineBorder(COLOR_CARD_BORDER));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0; gbc.insets.top = 20; gbc.insets.bottom = 10;
        card.add(new JLabel(createCircularImage(product.imagePath, 120)), gbc);
        gbc.gridy++; gbc.insets.top = 10; gbc.insets.bottom = 2;
        card.add(new JLabel(product.name, SwingConstants.CENTER) {{ setFont(FONT_PRODUCT_NAME); setForeground(primaryFg); }}, gbc);
        gbc.gridy++; gbc.insets.top = 0; gbc.insets.bottom = 15;
        card.add(new JLabel(product.description, SwingConstants.CENTER) {{ setFont(FONT_PRODUCT_DETAILS); setForeground(COLOR_SECONDARY_TEXT); }}, gbc);

        gbc.gridy++; gbc.insets.top = 5; gbc.insets.bottom = 15;
        JPanel priceStockPanel = new JPanel(new BorderLayout());
        priceStockPanel.setOpaque(false);
        priceStockPanel.add(new JLabel(String.format("Rs %.2f", product.price)) {{ setFont(FONT_PRODUCT_NAME); setForeground(primaryFg); }}, BorderLayout.WEST);
        JLabel stockLabel = new JLabel();
        stockLabel.setFont(FONT_STOCK_STATUS);
        if (isAvailable) { stockLabel.setText("In Stock (" + product.stock + ")"); stockLabel.setForeground(COLOR_STOCK_IN); }
        else { stockLabel.setText("Out of Stock"); stockLabel.setForeground(COLOR_STOCK_OUT); }
        priceStockPanel.add(stockLabel, BorderLayout.EAST);
        card.add(priceStockPanel, gbc);

        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets.top = 0; gbc.insets.bottom = 20;
        
        CardLayout actionLayout = new CardLayout();
        JPanel actionPanel = new JPanel(actionLayout);
        actionPanel.setOpaque(false);
        
        RoundedButton addToCartButton = new RoundedButton("Add to Cart", 12);
        addToCartButton.setFont(FONT_ADD_TO_CART);
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setBackground(isAvailable ? COLOR_ACCENT_PINK : Color.LIGHT_GRAY);
        addToCartButton.setPreferredSize(new Dimension(0, 40));
        if (!isAvailable) addToCartButton.setText("Unavailable");
        addToCartButton.addActionListener(e -> updateOrder(product, 1));
        
        JPanel stepperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        stepperPanel.setOpaque(false);
        
        JLabel quantityLabel = new JLabel("1");
        quantityLabel.setFont(FONT_QUANTITY);
        quantityLabel.setForeground(COLOR_PRIMARY_TEXT);
        
        CircularButton minusButton = new CircularButton(CircularButton.IconType.MINUS);
        minusButton.addActionListener(e -> {
            int currentQty = currentOrder.getOrDefault(product, 1);
            updateOrder(product, currentQty - 1);
        });
        
        CircularButton plusButton = new CircularButton(CircularButton.IconType.PLUS);
        plusButton.addActionListener(e -> {
            int currentQty = currentOrder.getOrDefault(product, 1);
            if (currentQty < product.stock) {
                updateOrder(product, currentQty + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Stock limit reached for " + product.name, "Stock Alert", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        stepperPanel.add(minusButton);
        stepperPanel.add(quantityLabel);
        stepperPanel.add(plusButton);
        
        actionPanel.add(addToCartButton, "ADD_BUTTON");
        actionPanel.add(stepperPanel, "STEPPER");
        
        card.add(actionPanel, gbc);

        productCardUIs.put(product, new ProductCardUI(actionPanel, actionLayout, quantityLabel));
        
        refreshProductCard(product);

        return card;
    }

    private JPanel createCheckoutItemPanel(Product product, int quantity) {
        JPanel itemPanel = new JPanel(new BorderLayout(15, 0));
        itemPanel.setBackground(COLOR_SIDEBAR);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_CARD_BORDER),
            new EmptyBorder(10, 15, 10, 15)
        ));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        itemPanel.add(new JLabel(createIcon(product.imagePath, 50, 50, 10)), BorderLayout.WEST);

        JPanel details = new JPanel(new GridLayout(2, 1));
        details.setOpaque(false);
        details.add(new JLabel(product.name) {{setFont(FONT_PRODUCT_NAME);}});
        details.add(new JLabel(String.format("Rs %.2f per item", product.price)) {{setFont(FONT_PRODUCT_DETAILS); setForeground(COLOR_SECONDARY_TEXT);}});
        itemPanel.add(details, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);
        
        JLabel quantityLabel = new JLabel(String.valueOf(quantity));
        quantityLabel.setFont(FONT_PRODUCT_NAME);
        
        CircularButton minusBtn = new CircularButton(CircularButton.IconType.MINUS, 28);
        minusBtn.addActionListener(e -> {
            int currentQty = currentOrder.get(product);
            if (currentQty > 1) updateOrder(product, currentQty - 1);
        });

        CircularButton plusBtn = new CircularButton(CircularButton.IconType.PLUS, 28);
        plusBtn.addActionListener(e -> {
            int currentQty = currentOrder.get(product);
            if (currentQty < product.stock) updateOrder(product, currentQty + 1);
            else JOptionPane.showMessageDialog(this, "Stock limit reached.", "Alert", JOptionPane.WARNING_MESSAGE);
        });
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setForeground(COLOR_STOCK_OUT);
        removeBtn.setOpaque(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setBorder(null);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> updateOrder(product, 0));
        
        controls.add(minusBtn);
        controls.add(quantityLabel);
        controls.add(plusBtn);
        controls.add(Box.createHorizontalStrut(20));
        controls.add(removeBtn);

        itemPanel.add(controls, BorderLayout.EAST);

        return itemPanel;
    }
    
    private void updateOrder(Product product, int quantity) {
        if (quantity > 0) {
            currentOrder.put(product, quantity);
        } else {
            currentOrder.remove(product);
        }
        refreshCartView();
        refreshCheckoutView();
        refreshProductCard(product);
    }

    private void refreshProductCard(Product product) {
        ProductCardUI ui = productCardUIs.get(product);
        if (ui == null) return;

        if (currentOrder.containsKey(product)) {
            int quantity = currentOrder.get(product);
            ui.quantityLabel.setText(String.valueOf(quantity));
            ui.actionLayout.show(ui.actionPanel, "STEPPER");
        } else {
            ui.actionLayout.show(ui.actionPanel, "ADD_BUTTON");
        }
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel logoLabel = new JLabel("The Choice");
        logoLabel.setFont(FONT_LOGO);
        logoLabel.setForeground(COLOR_ACCENT_PINK);
        gbc.insets = new Insets(0, 5, 60, 0);
        sidebar.add(logoLabel, gbc);

        gbc.insets = new Insets(0, 0, 20, 0);
        sidebar.add(createSidebarButton("Menu", "/images/menu_icon.png", e -> mainPanelLayout.show(mainPanelContainer, MENU_VIEW)), gbc);
        sidebar.add(createSidebarButton("My Orders", "/images/orders_icon.png", e -> {
            refreshCheckoutView();
            mainPanelLayout.show(mainPanelContainer, CHECKOUT_VIEW);
        }), gbc);
        sidebar.add(createSidebarButton("Profile", "/images/profile_icon.png", e -> {
            updateOrderHistoryView();
            mainPanelLayout.show(mainPanelContainer, PROFILE_VIEW);
        }), gbc);

        gbc.weighty = 1;
        sidebar.add(new JLabel(), gbc);
        gbc.weighty = 0;

        sidebar.add(createSidebarButton("Logout", "/images/logout_icon.png", e -> System.exit(0)), gbc);

        return sidebar;
    }

    private JPanel createMenuAndCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        panel.add(createMenuPanel(), BorderLayout.CENTER);
        panel.add(createOrderSummaryPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout(20, 20));
        profilePanel.setBackground(COLOR_BACKGROUND);
        profilePanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        profilePanel.add(new JLabel("My Order History") {{ setFont(FONT_HEADER_TITLE); }}, BorderLayout.NORTH);

        orderHistoryArea = new JTextArea("You haven't placed any orders yet.");
        orderHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        orderHistoryArea.setForeground(COLOR_PRIMARY_TEXT);
        orderHistoryArea.setEditable(false);
        orderHistoryArea.setMargin(new Insets(20, 20, 20, 20));
        orderHistoryArea.setLineWrap(true);
        orderHistoryArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(orderHistoryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_CARD_BORDER));
        profilePanel.add(scrollPane, BorderLayout.CENTER);
        return profilePanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout(20, 20));
        menuPanel.setOpaque(false);
        menuPanel.add(new JLabel("Menu") {{ setFont(FONT_HEADER_TITLE); }}, BorderLayout.NORTH);
        menuPanel.add(createMenuGridPanel(), BorderLayout.CENTER);
        return menuPanel;
    }

    private JPanel createOrderSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(COLOR_SIDEBAR);
        summaryPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        summaryPanel.setPreferredSize(new Dimension(380, 0));

        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(new JLabel("Order's Summary") {{ setFont(FONT_HEADER_TITLE); }}, BorderLayout.WEST);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        summaryPanel.add(header);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        totalItemsLabel = new JLabel("Total Items (0)");
        totalItemsLabel.setFont(FONT_SUMMARY_TITLE);
        totalItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(totalItemsLabel);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setOpaque(false);
        cartItemsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(scrollPane);

        summaryPanel.add(Box.createVerticalGlue());
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        summaryPanel.add(createPaymentSummarySection());
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        placeOrderButton = new RoundedButton("Place Order", 12);
        placeOrderButton.setBackground(COLOR_ACCENT_PINK);
        placeOrderButton.setForeground(Color.WHITE);
        placeOrderButton.setFont(FONT_MENU_ITEM);
        placeOrderButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        placeOrderButton.setMinimumSize(new Dimension(0, 55));
        placeOrderButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        placeOrderButton.setEnabled(false);
        placeOrderButton.addActionListener(e -> {
            refreshCheckoutView();
            mainPanelLayout.show(mainPanelContainer, CHECKOUT_VIEW);
        });
        summaryPanel.add(placeOrderButton);

        return summaryPanel;
    }

    private JPanel createCartItemPanel(Product product, int quantity) {
        RoundedPanel itemPanel = new RoundedPanel(12, Color.WHITE);
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        itemPanel.setOpaque(false);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(product.name);
        nameLabel.setFont(FONT_SUMMARY_ITEM);
        detailsPanel.add(nameLabel);

        detailsPanel.add(Box.createVerticalStrut(5));

        JLabel priceLabel = new JLabel(String.format("(Rs %.2f) x %d", product.price, quantity));
        priceLabel.setFont(FONT_PRODUCT_DETAILS);
        priceLabel.setForeground(COLOR_SECONDARY_TEXT);
        detailsPanel.add(priceLabel);

        itemPanel.add(detailsPanel, BorderLayout.CENTER);
        return itemPanel;
    }

    private JPanel createPaymentSummarySection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 0, 8, 0);

        priceValueLabel = new JLabel("Rs 0.00"); taxesValueLabel = new JLabel("Rs 0.00");
        discountValueLabel = new JLabel("Rs 0.00"); totalValueLabel = new JLabel("Rs 0.00");

        gbc.gridx=0; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy=0; panel.add(new JLabel("Price"){{setFont(FONT_SUMMARY_ITEM); setForeground(COLOR_SECONDARY_TEXT);}}, gbc);
        gbc.gridy=1; panel.add(new JLabel("Taxes"){{setFont(FONT_SUMMARY_ITEM); setForeground(COLOR_SECONDARY_TEXT);}}, gbc);
        gbc.gridy=2; panel.add(new JLabel("Discount"){{setFont(FONT_SUMMARY_ITEM); setForeground(COLOR_SECONDARY_TEXT);}}, gbc);
        
        gbc.gridy=3; gbc.insets = new Insets(12, 0, 12, 0); panel.add(new JSeparator(), gbc);
        gbc.gridy=4; gbc.insets = new Insets(8, 0, 8, 0); panel.add(new JLabel("Total"){{setFont(FONT_SUMMARY_TITLE);}}, gbc);

        gbc.gridx=1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy=0; panel.add(priceValueLabel, gbc);
        gbc.gridy=1; panel.add(taxesValueLabel, gbc);
        gbc.gridy=2; panel.add(discountValueLabel, gbc);
        gbc.gridy=3; gbc.insets = new Insets(12, 0, 12, 0); panel.add(new JSeparator(), gbc);
        gbc.gridy=4; gbc.insets = new Insets(8, 0, 8, 0); panel.add(totalValueLabel, gbc);
        
        Font valueFont = FONT_SUMMARY_ITEM;
        priceValueLabel.setFont(valueFont); taxesValueLabel.setFont(valueFont);
        discountValueLabel.setFont(valueFont); totalValueLabel.setFont(FONT_SUMMARY_TITLE);
        totalValueLabel.setForeground(COLOR_ACCENT_PINK);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private void refreshCartView() {
        cartItemsPanel.removeAll();
        int totalItemCount = 0;
        double[] totals = calculateTotals();

        for (Map.Entry<Product, Integer> entry : currentOrder.entrySet()) {
            totalItemCount += entry.getValue();
            cartItemsPanel.add(createCartItemPanel(entry.getKey(), entry.getValue()));
            cartItemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        totalItemsLabel.setText(String.format("Total Items (%d)", totalItemCount));
        updatePaymentSummary(totals);
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private void refreshCheckoutView() {
        checkoutItemsPanel.removeAll();
        if (currentOrder.isEmpty()) {
            checkoutItemsPanel.setLayout(new GridBagLayout());
            JLabel emptyLabel = new JLabel("Your cart is empty. Add items from the menu.", SwingConstants.CENTER);
            emptyLabel.setFont(FONT_HEADER_TITLE);
            emptyLabel.setForeground(COLOR_SECONDARY_TEXT);
            checkoutItemsPanel.add(emptyLabel);
        } else {
            checkoutItemsPanel.setLayout(new BoxLayout(checkoutItemsPanel, BoxLayout.Y_AXIS));
            for (Map.Entry<Product, Integer> entry : currentOrder.entrySet()) {
                checkoutItemsPanel.add(createCheckoutItemPanel(entry.getKey(), entry.getValue()));
                checkoutItemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        double[] totals = calculateTotals();
        checkoutPriceLabel.setText(String.format("Rs %.2f", totals[0]));
        checkoutTaxesLabel.setText(String.format("Rs %.2f", totals[1]));
        checkoutTotalLabel.setText(String.format("Rs %.2f", totals[2]));

        checkoutItemsPanel.revalidate();
        checkoutItemsPanel.repaint();
    }

    private void updateOrderHistoryView() {
        if (pastOrders.isEmpty()) {
            orderHistoryArea.setText("You haven't placed any orders yet.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = pastOrders.size() - 1; i >= 0; i--) {
            Order order = pastOrders.get(i);
            sb.append(order.getFormattedString());
            sb.append("\n======================================================\n\n");
        }
        orderHistoryArea.setText(sb.toString());
        orderHistoryArea.setCaretPosition(0);
    }
    
    private double[] calculateTotals() {
        double subtotal = 0;
        for (Map.Entry<Product, Integer> entry : currentOrder.entrySet()) {
            subtotal += entry.getKey().price * entry.getValue();
        }
        double taxes = subtotal * 0.10;
        double total = subtotal + taxes;
        return new double[]{subtotal, taxes, total};
    }

    private void updatePaymentSummary(double[] totals) {
        priceValueLabel.setText(String.format("Rs %.2f", totals[0]));
        taxesValueLabel.setText(String.format("Rs %.2f", totals[1]));
        double discount = currentOrder.isEmpty() ? 0.0 : 10.0;
        discountValueLabel.setText(String.format("-Rs %.2f", discount));
        totalValueLabel.setText(String.format("Rs %.2f", totals[2] - discount));
        placeOrderButton.setEnabled(!currentOrder.isEmpty());
    }

    private JButton createSidebarButton(String text, String iconPath, java.awt.event.ActionListener action) {
        JButton button = new JButton(text); button.setFont(FONT_MENU_ITEM);
        button.setForeground(COLOR_SECONDARY_TEXT); button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(false); button.setContentAreaFilled(false); button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (iconPath != null) { button.setIcon(createIcon(iconPath, 24, 24, 0)); button.setIconTextGap(20); }
        button.addActionListener(action); return button;
    }

    private ImageIcon createIcon(String path, int width, int height, int cornerRadius) {
        URL url = getClass().getResource(path);
        if (url == null) { System.err.println("Icon not found: " + path); return null; }
        ImageIcon originalIcon = new ImageIcon(url);
        BufferedImage master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = master.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (cornerRadius > 0) { g2d.setClip(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius)); }
        g2d.drawImage(originalIcon.getImage(), 0, 0, width, height, null);
        g2d.dispose(); return new ImageIcon(master);
    }
    
    private ImageIcon createCircularImage(String path, int diameter) { return createIcon(path, diameter, diameter, diameter); }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(FONT_PRODUCT_DETAILS.deriveFont(16f));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_CARD_BORDER),
            new EmptyBorder(10, 10, 10, 10)
        ));
        addPlaceholder(textField, placeholder);
        return textField;
    }
    
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(COLOR_PLACEHOLDER_TEXT);
        textField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) { textField.setText(""); textField.setForeground(COLOR_PRIMARY_TEXT); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) { textField.setForeground(COLOR_PLACEHOLDER_TEXT); textField.setText(placeholder); }
            }
        });
    }

    // --- STATIC INNER CLASSES ---

    private static class ProductCardUI {
        final JPanel actionPanel;
        final CardLayout actionLayout;
        final JLabel quantityLabel;

        ProductCardUI(JPanel ap, CardLayout al, JLabel ql) {
            this.actionPanel = ap;
            this.actionLayout = al;
            this.quantityLabel = ql;
        }
    }

    private static class CircularButton extends JButton {
        public enum IconType { PLUS, MINUS }

        private final IconType iconType;
        private final Color hoverBackgroundColor = new Color(0xEAEAEB);
        private final Color pressedBackgroundColor = new Color(0xDCDDDF);

        public CircularButton(IconType type) {
            this(type, 36);
        }

        public CircularButton(IconType type, int size) {
            super();
            this.iconType = type;
            setPreferredSize(new Dimension(size, size));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(COLOR_ACCENT_PINK);
            setBackground(new Color(0xF5F5F7));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(pressedBackgroundColor);
            } else if (getModel().isRollover()) {
                g2.setColor(hoverBackgroundColor);
            } else {
                g2.setColor(getBackground());
            }

            g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));

            g2.setColor(getForeground());
            g2.setStroke(new BasicStroke(2));

            int iconSize = getWidth() / 4;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            if (iconType == IconType.MINUS) {
                g2.drawLine(centerX - iconSize, centerY, centerX + iconSize, centerY);
            } else if (iconType == IconType.PLUS) {
                g2.drawLine(centerX - iconSize, centerY, centerX + iconSize, centerY);
                g2.drawLine(centerX, centerY - iconSize, centerX, centerY + iconSize);
            }
            
            g2.dispose();
        }
    }

    private static class Order {
        final String orderNumber, customerName, customerAddress, customerPhone, paymentMethod, orderDate;
        final Map<Product, Integer> items;
        final double totalAmount;
        String status;

        Order(String num, Map<Product, Integer> i, String name, String addr, String phone, String pay, double total) {
            this.orderNumber = num; this.items = i; this.customerName = name; this.customerAddress = addr;
            this.customerPhone = phone; this.paymentMethod = pay; this.totalAmount = total;
            this.status = "Pending"; this.orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        
        public void setStatus(String status) {
            this.status = status;
        }

        public String getFormattedString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Order Number: %s\n", orderNumber));
            sb.append(String.format("Date: %s\n", orderDate));
            sb.append(String.format("Status: %s\n", status));
            sb.append(String.format("Customer: %s (%s)\n", customerName, customerPhone));
            sb.append("------------------------------------------------------\n");
            sb.append("Items:\n");
            for(Map.Entry<Product, Integer> entry : items.entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                sb.append(String.format("  - %-25s x%d  (Rs %.2f)\n", p.name, qty, p.price * qty));
            }
            sb.append("------------------------------------------------------\n");
            sb.append(String.format("Payment Method: %s\n", paymentMethod));
            sb.append(String.format("TOTAL: Rs %.2f\n", totalAmount));
            return sb.toString();
        }
    }

    private static class Product {
        final String name, description, imagePath;
        final double price; final int stock;
        Product(String n, String d, double p, String ip, int s) { name = n; description = d; price = p; imagePath = ip; stock = s; }
        @Override public boolean equals(Object o) { return o instanceof Product && name.equals(((Product) o).name); }
        @Override public int hashCode() { return Objects.hash(name); }
    }
    
    private static class RoundedPanel extends JPanel {
        private final int r; private final Color c;
        RoundedPanel(int radius, Color color) { super(); r = radius; c = color; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(c);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r)); if (getBorder() != null) {
        getBorder().paintBorder(this, g2, 0, 0, getWidth(), getHeight()); } g2.dispose(); }
    }
    
    private static class RoundedButton extends JButton {
        private final int r;
        RoundedButton(String text, int radius) {
            super(text);
            r = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
            g2.dispose();
            super.paintComponent(g);
        }
    }
        
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new CustomerDashboard().setVisible(true)); }
}