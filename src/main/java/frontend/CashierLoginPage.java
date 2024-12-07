package frontend;

import Controller.CashierController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierLoginPage extends JFrame
{
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private JComboBox<String> branchComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;

    private final CashierController cashierController;

    public CashierLoginPage(JFrame previousFrame) {
        cashierController = new CashierController();

        setTitle("Metro Billing System - Cashier Login");
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
                String tagline = "CASHIER LOGIN";
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

        // Add login form panel
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setOpaque(false);
        contentPanel.add(loginFormPanel, BorderLayout.CENTER);

        createLoginForm(loginFormPanel);
    }

    private void createLoginForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel branchLabel = createStyledLabel("Branch Name");

        // Initialize branchComboBox
        CashierController cashierController = new CashierController(); // Assuming controller is accessible
        String[] branchNames = cashierController.getAllBranchNames(); // Fetch branch names
        branchComboBox = createStyledComboBox(branchNames);

        JLabel emailLabel = createStyledLabel("Email");
        emailField = createStyledTextField();
        JLabel passwordLabel = createStyledLabel("Password");
        passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Login");

        if (branchLabel != null) panel.add(branchLabel, gbc);
        if (branchComboBox != null) panel.add(branchComboBox, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        if (emailLabel != null) panel.add(emailLabel, gbc);
        if (emailField != null) panel.add(emailField, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        if (passwordLabel != null) panel.add(passwordLabel, gbc);
        if (passwordField != null) panel.add(passwordField, gbc);
        panel.add(Box.createVerticalStrut(20), gbc);
        if (loginButton != null) panel.add(loginButton, gbc);

        if (loginButton != null) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleLogin();
                }
            });
        }
    }


    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(METRO_BLUE);
        return label;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setForeground(METRO_BLUE);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(METRO_BLUE, 2));
        comboBox.setPreferredSize(new Dimension(250, 30)); // Adjust size if necessary
        return comboBox;
    }


    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setForeground(METRO_BLUE);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setForeground(METRO_BLUE);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(METRO_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return passwordField;
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
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 50));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin() {
        if (branchComboBox == null || emailField == null || passwordField == null) {
            Notification.showErrorMessage(this, "Login form not properly initialized.");
            return;
        }

        String selectedBranch = (String) branchComboBox.getSelectedItem();
        String branchCode = cashierController.getBranchCodeByName(selectedBranch);
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());


        if (branchCode == null || branchCode.isEmpty()) {
            Notification.showErrorMessage(this, "Invalid branch selected.");
            return;
        }

        // Perform login
        int resultCode = cashierController.login(email, password, branchCode);

        // Map result code to error message
        String resultMessage = ErrorMapper.getErrorMessage(resultCode);

        if (resultCode == 1) { // Success case
            Notification.showMessage(this, "Login Successful");
            dispose(); // Close the login page
            SwingUtilities.invokeLater(() -> new CashierPage(this,branchCode,email).setVisible(true)); // Navigate to dashboard
        } else { // Error case
            Notification.showErrorMessage(this, resultMessage);
        }


    }
}