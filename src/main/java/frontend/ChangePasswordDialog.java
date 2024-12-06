package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChangePasswordDialog extends JDialog {
    private static final Color METRO_YELLOW = new Color(255, 219, 0); // #FFDB00
    private static final Color METRO_BLUE = new Color(0, 41, 79);     // #00294F

    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton saveButton;

    public ChangePasswordDialog(JFrame parent) {
        super(parent, "Change Password", true);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(METRO_YELLOW);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        newPasswordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        saveButton = createStyledButton("Save");

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(createStyledLabel("New Password:"), gbc);

        gbc.gridy = 1;
        mainPanel.add(newPasswordField, gbc);

        gbc.gridy = 2;
        mainPanel.add(createStyledLabel("Confirm Password:"), gbc);

        gbc.gridy = 3;
        mainPanel.add(confirmPasswordField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(saveButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public String getNewPassword() {
        return new String(newPasswordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(200, 40));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(METRO_BLUE);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(METRO_BLUE);
        return label;
    }
}

