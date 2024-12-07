package frontend;

import backend.Product;
import Controller.DataEntryOperatorController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataEntryOperatorPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(255, 219, 0); // #FFDB00
    private static final Color METRO_BLUE = new Color(0, 41, 79);     // #00294F

    private JComboBox<String> vendorComboBox;
    private JPanel productsPanel;
    private final DataEntryOperatorController controller;
    private final List<Product> productList;
    private JLabel noProductsLabel;
    private String email;
    public DataEntryOperatorPage(JFrame previousFrame,String email) {
        controller = new DataEntryOperatorController();
        productList = new ArrayList<>();
         this.email=email;
        setTitle("Metro Billing System - Data Entry Operator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(METRO_YELLOW);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(METRO_BLUE);
                g2d.fillRect(0, 0, getWidth(), 100);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                String headerText = "Data Entry Operator";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(headerText);
                g2d.drawString(headerText, (getWidth() - textWidth) / 2, 65);
            }
        };
        setContentPane(contentPanel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setOpaque(false);

        // Add return button
        topPanel.add(new ReturnButton(this, previousFrame));

        // Add change password button
        JButton changePasswordButton = createStyledButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(200, 40));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        topPanel.add(changePasswordButton);

        // Add exit button in a wrapper panel to keep it on the right
        JPanel exitWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitWrapper.setOpaque(false);
        exitWrapper.add(new ExitButton());

        // Use BorderLayout for the main top panel to keep exit button on right
        JPanel mainTopPanel = new JPanel(new BorderLayout());
        mainTopPanel.setOpaque(false);
        mainTopPanel.add(topPanel, BorderLayout.WEST);
        mainTopPanel.add(exitWrapper, BorderLayout.EAST);

        contentPanel.add(mainTopPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel leftPanel = createLeftPanel();
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.25;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.insets = new Insets(40, 20, 20, 10);
        mainPanel.add(leftPanel, leftGbc);

        JPanel rightPanel = createRightPanel();
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.75;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(40, 10, 20, 20);
        mainPanel.add(rightPanel, rightGbc);

        loadVendors();
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.addSaveListener(e -> {
            String newPassword = dialog.getNewPassword();
            String confirmPassword = dialog.getConfirmPassword();
            if (newPassword.equals(confirmPassword)) {
                if(controller.ChangePasswordForDEO(email,newPassword)==1)
                {
                    Notification.showMessage(this, "Password changed successfully!");
                    dialog.dispose();
                }
            } else {
                Notification.showErrorMessage(this, "Passwords do not match. Please try again.");
            }
        });
        dialog.setVisible(true);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel dropdownPanel = new JPanel(new BorderLayout(0, 10));
        dropdownPanel.setOpaque(false);

        JLabel vendorLabel = new JLabel("Vendor Information");
        vendorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        vendorLabel.setForeground(METRO_BLUE);

        vendorComboBox = new JComboBox<>();
        vendorComboBox.setPreferredSize(new Dimension(250, 40));
        vendorComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        vendorComboBox.setBackground(Color.WHITE);

        dropdownPanel.add(vendorLabel, BorderLayout.NORTH);
        dropdownPanel.add(vendorComboBox, BorderLayout.CENTER);

        JButton addVendorButton = createStyledButton("Add New Vendor");
        addVendorButton.setPreferredSize(new Dimension(250, 40));
        addVendorButton.addActionListener(e -> showAddVendorDialog());

        panel.add(dropdownPanel, BorderLayout.NORTH);
        panel.add(addVendorButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Products Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(METRO_BLUE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Initialize productsPanel here to avoid NullPointerException
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setOpaque(false);

        noProductsLabel = new JLabel("NO PRODUCT ADDED");
        noProductsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        noProductsLabel.setForeground(METRO_BLUE);
        noProductsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        productsPanel.add(noProductsLabel);

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addToDatabaseButton = createStyledButton("Add Products");
        addToDatabaseButton.setPreferredSize(new Dimension(200, 40));
        addToDatabaseButton.addActionListener(e -> addProductsToDatabase());

        JButton plusButton = createCircularButton("+");
        plusButton.setPreferredSize(new Dimension(40, 40));
        plusButton.addActionListener(e -> showAddProductDialog());

        bottomPanel.add(addToDatabaseButton);
        bottomPanel.add(plusButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setMinimumSize(new Dimension(600, 600)); // Adjusted size to accommodate new fields
        dialog.getContentPane().setBackground(METRO_YELLOW);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Branch selection
        JComboBox<String> branchNameComboBox = new JComboBox<>();
        branchNameComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        branchNameComboBox.setPreferredSize(new Dimension(400, 40));

        String[] branchNames = controller.getAllBranchNames();
        if (branchNames != null) {
            for (String branchName : branchNames) {
                branchNameComboBox.addItem(branchName);
            }
        }

        // Input fields for product details
        JTextField productNameField = createStyledTextField();
        JTextField originalPriceField = createStyledTextField(); // Original price
        JTextField salesPriceField = createStyledTextField(); // Sales price
        JTextArea descriptionArea = createStyledTextArea();
        JSpinner quantitySpinner = createStyledSpinner();

        // Adding components to the panel
        contentPanel.add(createStyledLabel("Branch Name:"), gbc);
        contentPanel.add(branchNameComboBox, gbc);
        contentPanel.add(createStyledLabel("Product Name:"), gbc);
        contentPanel.add(productNameField, gbc);
        contentPanel.add(createStyledLabel("Original Price:"), gbc); // Label for Original Price
        contentPanel.add(originalPriceField, gbc);
        contentPanel.add(createStyledLabel("Sales Price:"), gbc); // Label for Sales Price
        contentPanel.add(salesPriceField, gbc);
        contentPanel.add(createStyledLabel("Product Description:"), gbc);
        contentPanel.add(new JScrollPane(descriptionArea), gbc);
        contentPanel.add(createStyledLabel("Quantity:"), gbc);
        contentPanel.add(quantitySpinner, gbc);

        // Save Button
        JButton saveButton = createStyledButton("Save");
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.addActionListener(e -> {
            String selectedBranchName = (String) branchNameComboBox.getSelectedItem();
            String branchCode = controller.getBranchCodeByName(selectedBranchName);

            if (branchCode == null || branchCode.isEmpty()) {
                Notification.showErrorMessage(this, "Invalid branch selection. Please select a valid branch.");
                return;
            }

            String vendorId = controller.getVendorIdByVendorName((String) vendorComboBox.getSelectedItem());
            if (vendorId != null) {
                // Create a product object with new fields
                Product product = new Product(
                        branchCode,
                        productNameField.getText(),
                        originalPriceField.getText(),
                        salesPriceField.getText(), // Include sales price
                        descriptionArea.getText(),
                        String.valueOf(quantitySpinner.getValue()),
                        vendorId
                );

                // Add product to the global product list and panel
                productList.add(product);
                addProductToPanel(product);
                dialog.dispose();
            } else {
                Notification.showErrorMessage(this, "Please select a valid vendor.");
            }
        });

        gbc.insets = new Insets(20, 20, 20, 20);
        contentPanel.add(saveButton, gbc);

        dialog.add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addProductToPanel(Product product) {
        // Remove noProductsLabel if visible
        if (noProductsLabel != null && noProductsLabel.isVisible()) {
            productsPanel.remove(noProductsLabel);
            noProductsLabel.setVisible(false);
        }

        // Create a product card panel
        JPanel productCard = new JPanel(new GridBagLayout());
        productCard.setBackground(Color.WHITE);
        productCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        productCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180)); // Adjusted height for additional fields
        productCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // GridBagConstraints for layout management
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Add product details to the card
        JLabel nameLabel = new JLabel("Product: " + product.getProductName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(METRO_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        productCard.add(nameLabel, gbc);

        JLabel originalPriceLabel = new JLabel(String.format("Original Price: Rs %.2f", Double.parseDouble(product.getOriginalPrice())));
        originalPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        productCard.add(originalPriceLabel, gbc);

        JLabel salesPriceLabel = new JLabel(String.format("Sales Price: Rs %.2f", Double.parseDouble(product.getSalesPrice())));
        salesPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        productCard.add(salesPriceLabel, gbc);

        JLabel quantityLabel = new JLabel(String.format("Quantity: %s", product.getQuantity()));
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        productCard.add(quantityLabel, gbc);

        JLabel branchLabel = new JLabel("Branch: " + product.getBranchCode());
        branchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        productCard.add(branchLabel, gbc);

        JLabel vendorLabel = new JLabel("Vendor ID: " + product.getVendorId());
        vendorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 4;
        productCard.add(vendorLabel, gbc);

        JLabel descriptionLabel = new JLabel("<html><body>Description: " + product.getProductDescription() + "</body></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 5;
        productCard.add(descriptionLabel, gbc);

        // Add the product card to the productsPanel
        productsPanel.add(productCard);
        productsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between cards
        productsPanel.revalidate();
        productsPanel.repaint();
    }


    private void addProductsToDatabase() {
        if (productList.isEmpty()) {
            Notification.showErrorMessage(this, "No products to add. Please add products first.");
            return;
        }

        int result = controller.addProducts(new ArrayList<>(productList)); // Send the product list to the controller
        if (result == 1) {
            Notification.showMessage(this, "All products added to the database successfully!");

            // Clear the products panel
            productsPanel.removeAll();
            productsPanel.revalidate();
            productsPanel.repaint();

            // Clear the global product list
            productList.clear();

            // Add back the noProductsLabel
            noProductsLabel.setVisible(true);
            productsPanel.add(noProductsLabel);
            productsPanel.revalidate();
            productsPanel.repaint();
        } else {
            Notification.showErrorMessage(this, "Failed to add products to the database.");
        }
    }


    private JButton createCircularButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(METRO_BLUE.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(METRO_BLUE.brighter());
                } else {
                    g2d.setColor(METRO_BLUE);
                }

                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        return button;
    }

    private void showAddVendorDialog() {
        JDialog dialog = new JDialog(this, "Add New Vendor", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setMinimumSize(new Dimension(600, 500));
        dialog.getContentPane().setBackground(METRO_YELLOW);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        JTextField nameField = createStyledTextField();
        JTextField cnicField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        JTextField addressField = createStyledTextField();

        contentPanel.add(createStyledLabel("Name:"), gbc);
        contentPanel.add(nameField, gbc);
        contentPanel.add(createStyledLabel("CNIC:"), gbc);
        contentPanel.add(cnicField, gbc);
        contentPanel.add(createStyledLabel("Phone Number:"), gbc);
        contentPanel.add(phoneField, gbc);
        contentPanel.add(createStyledLabel("Address:"), gbc);
        contentPanel.add(addressField, gbc);

        JButton saveButton = createStyledButton("Save");
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.addActionListener(e -> {
            int result = controller.addVendor(
                    nameField.getText(),
                    cnicField.getText(),
                    phoneField.getText(),
                    addressField.getText()
            );
            if (result == 1) {
                Notification.showMessage(this, "Vendor added successfully!");
                loadVendors();
            } else if (result == 0) {
                Notification.showErrorMessage(this, "Vendor already exists (Duplicate CNIC).");
            } else {
                Notification.showErrorMessage(this, "Error adding vendor.");
            }
            dialog.dispose();
        });

        gbc.insets = new Insets(20, 20, 20, 20);
        contentPanel.add(saveButton, gbc);

        dialog.add(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadVendors() {
        String[] vendors = controller.getAllVendors();
        if (vendors != null) {
            vendorComboBox.removeAllItems();
            for (String vendor : vendors) {
                vendorComboBox.addItem(vendor);
            }
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(METRO_BLUE);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(400, 40));
        field.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(4, 20);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        return area;
    }

    private JSpinner createStyledSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinner.setFont(new Font("Arial", Font.PLAIN, 16));
        spinner.setPreferredSize(new Dimension(400, 40));
        return spinner;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(METRO_BLUE);
        return label;
    }


}