package frontend;

import Controller.SuperAdminController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SuperAdminPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);
    private final SuperAdminController superAdminController;

    public SuperAdminPage(JFrame previousFrame) {
        superAdminController = new SuperAdminController();

        setTitle("Metro Billing System - Super Admin Dashboard");
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
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String metroText = "METRO";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(metroText);
                g2d.drawString(metroText, (getWidth() - textWidth) / 2, 70);

                g2d.setColor(METRO_BLUE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                String tagline = "SUPER ADMIN DASHBOARD";
                FontMetrics fm2 = g2d.getFontMetrics();
                int taglineWidth = fm2.stringWidth(tagline);
                g2d.drawString(tagline, (getWidth() - taglineWidth) / 2, 160);
            }
        };
        setContentPane(contentPanel);

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

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        contentPanel.add(optionsPanel, BorderLayout.CENTER);

        createOptions(optionsPanel);
    }

    private void createOptions(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);

        String[] buttonTexts = {"Add Branch", "Add Branch Manager", "See Reports"};
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

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (text) {
                    case "Add Branch":
                        showAddBranchDialog();
                        break;
                    case "Add Branch Manager":
                        showAddBranchManagerDialog();
                        break;
                    case "See Reports":
                        showReportsDialog();
                        break;
                }
            }
        });

        return button;
    }

    private void showAddBranchDialog() {
        JDialog addBranchDialog = new JDialog(this, "Add Branch", true);
        addBranchDialog.setLayout(new GridBagLayout());
        addBranchDialog.getContentPane().setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField branchCodeField = createStyledTextField("Branch Code");
        JTextField branchNameField = createStyledTextField("Branch Name");
        JTextField branchCityField = createStyledTextField("City");
        JTextField branchAddressField = createStyledTextField("Branch Address");
        JTextField branchPhoneField = createStyledTextField("Phone");
        JButton submitButton = createStyledButton("Add Branch");

        addBranchDialog.add(branchCodeField, gbc);
        addBranchDialog.add(branchNameField, gbc);
        addBranchDialog.add(branchCityField, gbc);
        addBranchDialog.add(branchAddressField, gbc);
        addBranchDialog.add(branchPhoneField, gbc);
        addBranchDialog.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String branchCode = branchCodeField.getText();
            String branchName = branchNameField.getText();
            String branchCity = branchCityField.getText();
            String branchAddress = branchAddressField.getText();
            String branchPhone = branchPhoneField.getText();

            int resultCode = superAdminController.createBranch(branchCode, branchName, branchCity, branchAddress, branchPhone);

            String resultMessage = ErrorMapper.getErrorMessage(resultCode);
            if (resultCode == 1) { // Success
                Notification.showMessage(this, "Branch Added Sucessfully");
            } else { // Error
                Notification.showErrorMessage(this, resultMessage);
            }
            addBranchDialog.dispose();
        });

        addBranchDialog.pack();
        addBranchDialog.setLocationRelativeTo(this);
        addBranchDialog.setVisible(true);
    }


    private void showAddBranchManagerDialog() {
        JDialog addManagerDialog = new JDialog(this, "Add Branch Manager", true);
        addManagerDialog.setLayout(new GridBagLayout());
        addManagerDialog.getContentPane().setBackground(METRO_YELLOW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] branchNames = superAdminController.getAllBranchNames();
        JComboBox<String> branchSelector = new JComboBox<>(branchNames);

        JTextField managerNameField = createStyledTextField("Manager Name");
        JTextField managerEmailField = createStyledTextField("Manager Email");
        JTextField managerCnicField = createStyledTextField("Manager CNIC");
        JTextField managerPhoneField = createStyledTextField("Phone Number");
        JTextField managerSalaryField = createStyledTextField("Salary");
        JButton submitButton = createStyledButton("Add Manager");

        addManagerDialog.add(branchSelector, gbc);
        addManagerDialog.add(managerNameField, gbc);
        addManagerDialog.add(managerEmailField, gbc);
        addManagerDialog.add(managerCnicField, gbc);
        addManagerDialog.add(managerPhoneField, gbc);
        addManagerDialog.add(managerSalaryField, gbc);
        addManagerDialog.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String selectedBranch = (String) branchSelector.getSelectedItem();
            String branchCode = superAdminController.getBranchCodeByName(selectedBranch);
            String managerName = managerNameField.getText();
            String managerEmail = managerEmailField.getText();
            String managerCnic = managerCnicField.getText();
            String managerPhone = managerPhoneField.getText();
            String managerSalary = managerSalaryField.getText();

            if (branchCode == null || branchCode.isEmpty()) {
                Notification.showErrorMessage(this, "Unable to find branch code for the selected branch.");
                return;
            }

            int resultCode = superAdminController.addBranchManager(branchCode, managerName, managerEmail, managerCnic, managerSalary, managerPhone);

            String resultMessage = ErrorMapper.getErrorMessage(resultCode);
            if (resultCode == 1) { // Success
                Notification.showMessage(this, "Branch Manager Added Sucessfully");
            } else { // Error
                Notification.showErrorMessage(this, resultMessage);
            }
            addManagerDialog.dispose();
        });

        addManagerDialog.pack();
        addManagerDialog.setLocationRelativeTo(this);
        addManagerDialog.setVisible(true);
    }


    private void showReportsDialog() {
        System.out.println("Reports dialog opened.");
        Notification.showMessage(this, "Reports feature coming soon!");
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
