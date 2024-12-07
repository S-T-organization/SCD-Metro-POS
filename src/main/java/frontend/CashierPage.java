package frontend;

import BarcodeScanner.RealTimeBarcodeScanner;
import Controller.CashierController;
import backend.Product;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CashierPage extends JFrame
{private static final String SERVER_ADDRESS = "localhost"; // Server Address
    private static final int SERVER_PORT = 5050; // Server Port

    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0.0;
    private final CashierController controller;
    private final String branchCode;
    private Thread clientThread;
    private String email;
    public CashierPage(JFrame previousFrame, String branchCode,String email)
    {
        this.email=email;

        this.branchCode = branchCode;
        controller = new CashierController(); // Initialize the controller

        setTitle("Metro Billing System - Cashier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Main content panel with custom painting
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

                // Cashier text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String headerText = "CASHIER";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(headerText);
                g2d.drawString(headerText, (getWidth() - textWidth) / 2, 70);
            }
        };
        setContentPane(contentPanel);

        // Add exit, return, and change password buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(createReturnButton(previousFrame));

        // Add Change Password button
        JButton changePasswordButton = createStyledButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(200, 40));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        leftPanel.add(changePasswordButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(createExitButton());

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Create table
        createTable();
        startBarcodeScannerServer();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        startBarcodeScannerClient();

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);

        JButton addButton = createStyledButton("Add");
        JButton generateBillButton = createStyledButton("Generate Bill");

        buttonsPanel.add(addButton);
        buttonsPanel.add(generateBillButton);

        // Create bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JButton removeButton = createStyledButton("Remove Product");
        totalLabel = new JLabel("Total: Rs 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setForeground(METRO_BLUE);

        bottomPanel.add(removeButton, BorderLayout.WEST);
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        // Create center panel to hold table and buttons
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonsPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add components to content panel
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Add product action
        addButton.addActionListener(e -> showAddProductDialog());

        // Remove product action
        removeButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                // Update total before removing
                double price = (double) tableModel.getValueAt(selectedRow, 1);
                int quantity = (int) tableModel.getValueAt(selectedRow, 2);
                total -= price * quantity;
                updateTotalLabel();

                tableModel.removeRow(selectedRow);
            }
        });

        // Generate Bill action
        generateBillButton.addActionListener(e -> generateBill());
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this);
        dialog.addSaveListener(e -> {
            String newPassword = dialog.getNewPassword();
            String confirmPassword = dialog.getConfirmPassword();
            if (newPassword.equals(confirmPassword))
            {

                int result = controller.ChangePasswordForCashier(email,newPassword);
                if (result == 1) {
                    Notification.showMessage(this, "Password changed successfully!");
                    dialog.dispose();
                } else {
                    Notification.showErrorMessage(this, "Failed to change password. Please try again.");
                }

            } else {
                Notification.showErrorMessage(this, "Passwords do not match!");
            }
        });
        dialog.setVisible(true);
    }

    private void startBarcodeScannerServer() {
        // Start the Barcode Scanner server in a separate thread
        new Thread(() -> {
            RealTimeBarcodeScanner scanner = new RealTimeBarcodeScanner();
            scanner.startServer();
        }).start();
    }
    private void startBarcodeScannerClient() {
        // Start the client to listen for QR codes
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                System.out.println("Connected to the Barcode Scanner server.");
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                while (true) {
                    String qrCode = inputStream.readUTF(); // Receive QR code
                    System.out.println("QR Code received: " + qrCode);

                    SwingUtilities.invokeLater(() -> addProductToTable(qrCode));
                }
            } catch (Exception e) {
                System.out.println("Error in client socket: " + e.getMessage());
            }
        }).start();
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"Product Name", "Price", "Quantity"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 1 ? Double.class : column == 2 ? Integer.class : String.class;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.setRowHeight(30);
        productTable.setSelectionBackground(METRO_BLUE);
        productTable.setSelectionForeground(Color.WHITE);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                updateTotal();
            }
        });
    }
    private void startClientThread() {
        clientThread = new Thread(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                System.out.println("Connected to the Scanner Server.");
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                while (true) {
                    String qrCode = inputStream.readUTF(); // Receive QR code from server
                    System.out.println("QR Code Received: " + qrCode);

                    SwingUtilities.invokeLater(() -> addProductToTable(qrCode)); // Process on UI thread
                }
            } catch (Exception e) {
                System.out.println("Error in Client: " + e.getMessage());
            }
        });
        clientThread.setDaemon(true); // Ensure the thread stops with the application
        clientThread.start();
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(METRO_YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel productIdLabel = new JLabel("Product ID:");
        productIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        productIdLabel.setForeground(METRO_BLUE);
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(productIdLabel, gbc);

        JTextField productIdField = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        dialog.add(productIdField, gbc);

        JButton confirmButton = createStyledButton("Add");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            String productId = productIdField.getText();
            if (!productId.isEmpty()) {
                Product product = controller.getProductById(productId);
                if (product != null) {
                    Vector<Object> row = new Vector<>();
                    row.add(product.getProductName());
                    row.add(Double.parseDouble(product.getSalesPrice()));
                    row.add(1); // Default quantity
                    tableModel.addRow(row);

                    updateTotal();
                    Notification.showMessage(this, "Product added successfully!");
                } else {
                    Notification.showErrorMessage(this, "Product not found!");
                }
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void generateBill() {
        List<String> productQuantities = new ArrayList<>();
        List<String[]> billDetails = new ArrayList<>();

        // Collect product details from the table
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String productName = (String) tableModel.getValueAt(i, 0);
            double price = (double) tableModel.getValueAt(i, 1);
            int quantity = (int) tableModel.getValueAt(i, 2);
            productQuantities.add(productName + "," + quantity);
            billDetails.add(new String[]{productName, String.valueOf(price), String.valueOf(quantity)});
        }

        // Call controller to remove products from database
        String result = controller.removeProduct(productQuantities, branchCode);

        if (result.startsWith("true")) {
            Notification.showMessage(this, "Bill generated successfully!");

            // Clear the table
            updateTotal1();
            tableModel.setRowCount(0);

            // Open the Bill screen
            SwingUtilities.invokeLater(() -> new BillScreen(this, billDetails, total).setVisible(true));


        } else {
            // Handle errors
            String[] parts = result.split(",", 2);
            if (parts[1].startsWith("Internet")) {
                Notification.showErrorMessage(this, "Error: Internet not available");
            } else {
                Notification.showErrorMessage(this, "Error: " + parts[1] + " - Not Enough Stock");
            }
        }
    }

    private void addProductToTable(String productId) {
        Product product = controller.getProductById(productId);
        if (product != null) {
            Vector<Object> row = new Vector<>();
            row.add(product.getProductName());
            row.add(Double.parseDouble(product.getSalesPrice()));
            row.add(1); // Default quantity
            tableModel.addRow(row);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void updateTotal() {
        total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double price = (double) tableModel.getValueAt(i, 1);
            int quantity = (int) tableModel.getValueAt(i, 2);
            total += price * quantity;
        }
        updateTotalLabel();
    }

    public void updateTotal1() {
        total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double price = (double) tableModel.getValueAt(i, 1);
            int quantity = (int) tableModel.getValueAt(i, 2);
            total += price * quantity;
        }
        updateTotalLabel1();
    }

    private void updateTotalLabel1() {
        totalLabel.setText(String.format("Total: Rs%.2f", 0.00));
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: Rs%.2f", total));
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
        button.setPreferredSize(new Dimension(150, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createReturnButton(JFrame previousFrame) {
        JButton returnButton = new JButton("Return") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(METRO_BLUE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        returnButton.setForeground(Color.WHITE);
        returnButton.setFont(new Font("Arial", Font.BOLD, 14));
        returnButton.setPreferredSize(new Dimension(100, 30));
        returnButton.setContentAreaFilled(false);
        returnButton.setBorderPainted(false);
        returnButton.setFocusPainted(false);
        returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnButton.addActionListener(e -> {
            dispose();
            previousFrame.setVisible(true);
        });
        return returnButton;
    }

    private JButton createExitButton() {
        JButton exitButton = new JButton("Exit") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(METRO_BLUE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));
        return exitButton;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setForeground(METRO_BLUE);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return textField;
    }



}

