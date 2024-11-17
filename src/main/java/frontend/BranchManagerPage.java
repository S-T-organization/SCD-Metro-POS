package frontend;

import Controller.BranchManagerController;

import javax.swing.*;
import java.awt.*;

public class BranchManagerPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);
    private final BranchManagerController branchManagerController;

    public BranchManagerPage(JFrame previousFrame) {
        branchManagerController = new BranchManagerController();

        setTitle("Metro Billing System - Branch Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

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

                // Metro text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String metroText = "METRO";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(metroText);
                g2d.drawString(metroText, (getWidth() - textWidth) / 2, 70);

                // Tagline
                g2d.setColor(METRO_BLUE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                String tagline = "BRANCH MANAGER";
                FontMetrics fm2 = g2d.getFontMetrics();
                int taglineWidth = fm2.stringWidth(tagline);
                g2d.drawString(tagline, (getWidth() - taglineWidth) / 2, 160);
            }
        };
        setContentPane(contentPanel);

        // Add exit and return buttons
        ExitButton exitButton = new ExitButton();
        ReturnButton returnButton = new ReturnButton(this, previousFrame);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(returnButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(exitButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        contentPanel.add(buttonsPanel, BorderLayout.CENTER);

        createButtons(buttonsPanel);
    }

    private void createButtons(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);

        String[] buttonTexts = {"Create Data Entry Operator", "Create Cashier"};
        for (String text : buttonTexts) {
            JButton button = createStyledButton(text);
            panel.add(button, gbc);
        }
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
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(400, 80));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if (text.equals("Create Data Entry Operator")) {
                showCreateDataEntryOperatorDialog();
            } else if (text.equals("Create Cashier")) {
                showCreateCashierDialog();
            }
        });

        return button;
    }

    private void showCreateDataEntryOperatorDialog() {
        JDialog dialog = new JDialog(this, "Create Data Entry Operator", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] branchNames = branchManagerController.getAllBranchNames();
        JComboBox<String> branchSelector = new JComboBox<>(branchNames);
        JTextField nameField = createStyledTextField("Name");
        JTextField emailField = createStyledTextField("Email");
        JTextField salaryField = createStyledTextField("Salary");
        JButton submitButton = createStyledButton("Create");

        dialog.add(branchSelector, gbc);
        dialog.add(nameField, gbc);
        dialog.add(emailField, gbc);
        dialog.add(salaryField, gbc);
        dialog.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String selectedBranch = (String) branchSelector.getSelectedItem();
            String branchCode = branchManagerController.getBranchCodeByName(selectedBranch);
            String name = nameField.getText();
            String email = emailField.getText();
            String salary = salaryField.getText();

            boolean success = branchManagerController.addDataEntryOperator(branchCode, name, email, salary);

            if (success) {
                Notification.showMessage(this, "Data Entry Operator created successfully!");
            } else {
                Notification.showErrorMessage(this, "Failed to create Data Entry Operator. Please try again.");
            }
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showCreateCashierDialog() {
        JDialog dialog = new JDialog(this, "Create Cashier", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] branchNames = branchManagerController.getAllBranchNames();
        JComboBox<String> branchSelector = new JComboBox<>(branchNames);
        JTextField nameField = createStyledTextField("Name");
        JTextField emailField = createStyledTextField("Email");
        JTextField salaryField = createStyledTextField("Salary");
        JButton submitButton = createStyledButton("Create");

        dialog.add(branchSelector, gbc);
        dialog.add(nameField, gbc);
        dialog.add(emailField, gbc);
        dialog.add(salaryField, gbc);
        dialog.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String selectedBranch = (String) branchSelector.getSelectedItem();
            String branchCode = branchManagerController.getBranchCodeByName(selectedBranch);
            String name = nameField.getText();
            String email = emailField.getText();
            String salary = salaryField.getText();

            boolean success = branchManagerController.addCashier(branchCode, name, email, salary);

            if (success) {
                Notification.showMessage(this, "Cashier created successfully!");
            } else {
                Notification.showErrorMessage(this, "Failed to create Cashier. Please try again.");
            }
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setForeground(METRO_BLUE);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setText(placeholder);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }
}