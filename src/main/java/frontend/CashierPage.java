package frontend;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class CashierPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double total = 0.0;

    public CashierPage(JFrame previousFrame) {
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

        // Add exit and return buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(createReturnButton(previousFrame));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(createExitButton());

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Create table
        createTable();

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
        totalLabel = new JLabel("Total: $0.00");
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

        // Add button action
        addButton.addActionListener(e -> showAddProductDialog());

        // Remove button action
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

        // Generate Bill button action
        generateBillButton.addActionListener(e -> {
            // TODO: Implement bill generation functionality
            // This would typically involve:
            // 1. Creating a bill with the current items
            // 2. Saving to database
            // 3. Printing or displaying bill
        });
    }

    private void createTable() {
        // Create table model with non-editable cells except quantity
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
                switch (column) {
                    case 1: return Double.class;
                    case 2: return Integer.class;
                    default: return String.class;
                }
            }
        };

        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Arial", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        productTable.setRowHeight(30);
        productTable.setSelectionBackground(METRO_BLUE);
        productTable.setSelectionForeground(Color.WHITE);

        // Add quantity change listener
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                updateTotal();
            }
        });

        // Custom renderer for alternate row colors
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        });
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField productIdField = new JTextField(15);
        JButton confirmButton = new JButton("Add");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(productIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            String productId = productIdField.getText();
            if (!productId.isEmpty()) {
                // TODO: In a real application, fetch product details from database
                // For demonstration, adding dummy data
                Vector<Object> row = new Vector<>();
                row.add("Product " + productId);
                row.add(99.99); // dummy price
                row.add(1); // default quantity
                tableModel.addRow(row);

                // Update total
                updateTotal();

                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateTotal() {
        total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double price = (double) tableModel.getValueAt(i, 1);
            int quantity = (int) tableModel.getValueAt(i, 2);
            total += price * quantity;
        }
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("Total: $%.2f", total));
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
}