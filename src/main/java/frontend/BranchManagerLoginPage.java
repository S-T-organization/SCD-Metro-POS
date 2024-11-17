package frontend;

import javax.swing.*;
import java.awt.*;

public class BranchManagerLoginPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    public BranchManagerLoginPage(JFrame previousFrame) {
        setTitle("Metro Billing System - Branch Manager Login");
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
                String tagline = "BRANCH MANAGER LOGIN";
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
        JComboBox<String> branchComboBox = createStyledComboBox(new String[]{"Branch 1", "Branch 2", "Branch 3"});
        JLabel emailLabel = createStyledLabel("Email");
        JTextField emailField = createStyledTextField();
        JLabel passwordLabel = createStyledLabel("Password");
        JPasswordField passwordField = createStyledPasswordField();
        JButton loginButton = createStyledButton("Login");

        panel.add(branchLabel, gbc);
        panel.add(branchComboBox, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(emailLabel, gbc);
        panel.add(emailField, gbc);
        panel.add(Box.createVerticalStrut(10), gbc);
        panel.add(passwordLabel, gbc);
        panel.add(passwordField, gbc);
        panel.add(Box.createVerticalStrut(20), gbc);
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String selectedBranch = (String) branchComboBox.getSelectedItem();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            // TODO: Implement login logic
            System.out.println("Login attempt - Branch: " + selectedBranch + ", Email: " + email);
        });
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
}