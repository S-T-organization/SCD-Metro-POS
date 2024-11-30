package frontend;

import backend.Product;
import Controller.DataEntryOperatorController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DataEntryOperatorPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private JComboBox<String> vendorComboBox;
    private JPanel productsPanel;
    private DataEntryOperatorController controller;

    public DataEntryOperatorPage(JFrame previousFrame) {
        controller = new DataEntryOperatorController();

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(new ReturnButton(this, previousFrame), BorderLayout.WEST);
        topPanel.add(new ExitButton(), BorderLayout.EAST);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        contentPanel.add(mainPanel, BorderLayout.CENTER);

        JPanel leftPanel = createLeftPanel();
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.weightx = 0.3;
        leftGbc.weighty = 1.0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.insets = new Insets(40, 20, 20, 10);
        mainPanel.add(leftPanel, leftGbc);

        JPanel rightPanel = createRightPanel();
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0.7;
        rightGbc.weighty = 1.0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.insets = new Insets(40, 10, 20, 20);
        mainPanel.add(rightPanel, rightGbc);

        loadVendors();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addToDatabaseButton = createStyledButton("Add Products");
        addToDatabaseButton.setPreferredSize(new Dimension(200, 35));
        addToDatabaseButton.addActionListener(e -> addProductsToDatabase());

        JButton plusButton = createCircularButton("+");
        plusButton.setPreferredSize(new Dimension(35, 35));
        plusButton.addActionListener(e -> showAddProductDialog());

        bottomPanel.add(addToDatabaseButton);
        bottomPanel.add(plusButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addProductsToDatabase() {
        ArrayList<Product> products = new ArrayList<>();

        for (Component component : productsPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel productCard = (JPanel) component;

                // Initialize variables to extract product information
                String branchCode = null, productName = null, price = null, quantity = null, description = null;

                for (Component cardComponent : productCard.getComponents()) {
                    if (cardComponent instanceof JLabel) {
                        JLabel label = (JLabel) cardComponent;
                        String text = label.getText();

                        if (text.startsWith("Branch Code: ")) {
                            branchCode = text.replace("Branch Code: ", "");
                        } else if (text.startsWith("Name: ")) {
                            productName = text.replace("Name: ", "");
                        } else if (text.startsWith("Price: $")) {
                            price = text.replace("Price: $", "");
                        } else if (text.startsWith("Quantity: ")) {
                            quantity = text.replace("Quantity: ", "");
                        } else if (text.startsWith("Description: ")) {
                            description = text.replace("Description: ", "");
                        }
                    }
                }

                if (branchCode != null && productName != null && price != null && quantity != null && description != null) {
                    String vendorId = controller.getVendorIdByVendorName((String) vendorComboBox.getSelectedItem());
                    if (vendorId == null) {
                        Notification.showErrorMessage(this, "Vendor ID could not be determined. Ensure a valid vendor is selected.");
                        return;
                    }

                    products.add(new Product(branchCode, productName, price, description, quantity, vendorId));
                }
            }
        }

        System.out.println("Products to be added: " + products); // Debugging output

        int result = controller.addProducts(products);
        if (result == 1) {
            Notification.showMessage(this, "All products added to the database successfully!");
            productsPanel.removeAll();
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
        button.setPreferredSize(new Dimension(35, 35)); // Set size for circular shape
        return button;
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
        JTextField addressField = createStyledTextField();

        dialog.add(createStyledLabel("Name:"), gbc);
        dialog.add(nameField, gbc);
        dialog.add(createStyledLabel("CNIC:"), gbc);
        dialog.add(cnicField, gbc);
        dialog.add(createStyledLabel("Phone Number:"), gbc);
        dialog.add(phoneField, gbc);
        dialog.add(createStyledLabel("Address:"), gbc);
        dialog.add(addressField, gbc);

        JButton saveButton = createStyledButton("Save");
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

        JTextField productNameField = createStyledTextField();
        JTextField priceField = createStyledTextField();
        JTextField branchCodeField = createStyledTextField();
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
        dialog.add(createStyledLabel("Quantity:"), gbc);
        dialog.add(quantitySpinner, gbc);

        JButton saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            // Add product info to the panel
            addProductToPanel(
                    branchCodeField.getText(),
                    productNameField.getText(),
                    priceField.getText(),
                    descriptionArea.getText(),
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

    private void addProductToPanel(String branchCode, String productName, String price, String description, int quantity) {
        JPanel productCard = new JPanel(new GridBagLayout());
        productCard.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 1));
        productCard.setBackground(Color.WHITE);
        productCard.setPreferredSize(new Dimension(400, 120));
        productCard.setMaximumSize(new Dimension(400, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel nameLabel = new JLabel("Name: " + productName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel priceLabel = new JLabel("Price: $" + price);
        JLabel quantityLabel = new JLabel("Quantity: " + quantity);
        JLabel branchLabel = new JLabel("Branch Code: " + branchCode);
        JLabel descriptionLabel = new JLabel("<html><body>Description: " + description + "</body></html>");

        productCard.add(nameLabel, gbc);
        productCard.add(priceLabel, gbc);
        productCard.add(quantityLabel, gbc);
        productCard.add(branchLabel, gbc);
        productCard.add(descriptionLabel, gbc);

        // Add the card to the productsPanel
        productsPanel.add(productCard);

        // Add a spacer for separation between cards
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, 10));
        productsPanel.add(spacer);

        // Refresh the panel to reflect changes
        productsPanel.revalidate();
        productsPanel.repaint();
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

    private void refreshProductsPanel() {
        // Refresh product panel logic
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
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(4, 20);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JSpinner createStyledSpinner() {
        return new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }
}
