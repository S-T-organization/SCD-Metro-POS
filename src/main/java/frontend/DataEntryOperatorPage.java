package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DataEntryOperatorPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private JComboBox<String> vendorComboBox;
    private JPanel productsPanel;

    public DataEntryOperatorPage(JFrame previousFrame) {
        setTitle("Metro Billing System - Data Entry Operator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Main content panel with yellow background
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Yellow background
                g2d.setColor(METRO_YELLOW);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Blue header
                g2d.setColor(METRO_BLUE);
                g2d.fillRect(0, 0, getWidth(), 100);

                // Header text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                String headerText = "Data Entry Operator";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(headerText);
                g2d.drawString(headerText, (getWidth() - textWidth) / 2, 65);
            }
        };
        setContentPane(contentPanel);

        // Add exit and return buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(new ReturnButton(this, previousFrame), BorderLayout.WEST);
        topPanel.add(new ExitButton(), BorderLayout.EAST);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Main layout panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        // Left panel with adjusted width and position
        JPanel leftPanel = createLeftPanel();
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.3;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.insets = new Insets(40, 20, 20, 10); // Increased top inset to 40
        mainPanel.add(leftPanel, leftGbc);

        // Right panel with vertical layout and reduced height
        JPanel rightPanel = createRightPanel();
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.7;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(40, 10, 20, 20); // Increased top inset to 40
        mainPanel.add(rightPanel, rightGbc);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Vendor dropdown with adjusted size
        JPanel dropdownPanel = new JPanel(new BorderLayout(0, 10));
        dropdownPanel.setOpaque(false);

        JLabel vendorLabel = new JLabel("Vendor Information");
        vendorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        vendorLabel.setForeground(METRO_BLUE);

        vendorComboBox = new JComboBox<>();
        vendorComboBox.setPreferredSize(new Dimension(250, 30));
        vendorComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        vendorComboBox.setBackground(Color.WHITE);

        dropdownPanel.add(vendorLabel, BorderLayout.NORTH);
        dropdownPanel.add(vendorComboBox, BorderLayout.CENTER);

        // Add New Vendor button with adjusted size and text
        JButton addVendorButton = createStyledButton("Add New Vendor");
        addVendorButton.setPreferredSize(new Dimension(250, 35));
        addVendorButton.addActionListener(e -> showAddVendorDialog());

        panel.add(dropdownPanel, BorderLayout.NORTH);
        panel.add(addVendorButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // Title for the products panel
        JLabel titleLabel = new JLabel("Products Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(METRO_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Products panel with vertical layout
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Bottom panel with adjusted button positions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> handleAddProducts());

        JButton plusButton = createCircularButton("+");
        plusButton.setPreferredSize(new Dimension(35, 35));
        plusButton.addActionListener(e -> showAddProductDialog());

        bottomPanel.add(addButton);
        bottomPanel.add(plusButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddVendorDialog() {
        JDialog dialog = new JDialog(this, "Add New Vendor", true);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField nameField = createStyledTextField();
        JTextField cnicField = createStyledTextField();
        JTextField phoneField = createStyledTextField();

        dialog.add(createStyledLabel("Name:"), gbc);
        dialog.add(nameField, gbc);
        dialog.add(createStyledLabel("CNIC:"), gbc);
        dialog.add(cnicField, gbc);
        dialog.add(createStyledLabel("Phone Number:"), gbc);
        dialog.add(phoneField, gbc);

        JButton saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            // TODO: Implement save vendor logic
            dialog.dispose();
        });

        gbc.insets = new Insets(15, 10, 10, 10);
        dialog.add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField branchCodeField = createStyledTextField();
        JTextField productNameField = createStyledTextField();
        JTextField priceField = createStyledTextField();
        JTextArea descriptionArea = createStyledTextArea();
        JSpinner quantitySpinner = createStyledSpinner();

        dialog.add(createStyledLabel("Branch Code:"), gbc);
        dialog.add(branchCodeField, gbc);
        dialog.add(createStyledLabel("Product Name:"), gbc);
        dialog.add(productNameField, gbc);
        dialog.add(createStyledLabel("Product Price:"), gbc);
        dialog.add(priceField, gbc);
        dialog.add(createStyledLabel("Product Description:"), gbc);
        dialog.add(new JScrollPane(descriptionArea), gbc);
        dialog.add(createStyledLabel("Product Quantity:"), gbc);
        dialog.add(quantitySpinner, gbc);

        JButton saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            addProductToPanel(
                    productNameField.getText(),
                    Double.parseDouble(priceField.getText()),
                    (Integer) quantitySpinner.getValue()
            );
            dialog.dispose();
        });

        gbc.insets = new Insets(15, 10, 10, 10);
        dialog.add(saveButton, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addProductToPanel(String name, double price, int quantity) {
        JPanel productCard = new JPanel(new GridBagLayout());
        productCard.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        productCard.setBackground(Color.WHITE);
        productCard.setPreferredSize(new Dimension(400, 100));
        productCard.setMaximumSize(new Dimension(400, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", price));
        JLabel quantityLabel = new JLabel(String.format("Quantity: %d", quantity));

        productCard.add(nameLabel, gbc);
        productCard.add(priceLabel, gbc);
        productCard.add(quantityLabel, gbc);

        // Add some spacing between cards
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, 10));

        productsPanel.add(productCard);
        productsPanel.add(spacer);
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private void handleAddProducts() {
        // TODO: Implement AddProducts functionality
        Notification.showMessage(this, "Products added successfully!");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(METRO_BLUE.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(METRO_BLUE.brighter());
                } else {
                    g2d.setColor(METRO_BLUE);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createCircularButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(METRO_BLUE.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(METRO_BLUE.brighter());
                } else {
                    g2d.setColor(METRO_BLUE);
                }

                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(METRO_BLUE);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(4, 20);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(METRO_BLUE),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );
        return spinner;
    }
}