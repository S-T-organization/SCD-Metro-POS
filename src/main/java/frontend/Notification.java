package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Notification extends JDialog {

    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font MESSAGE_FONT = new Font("Arial", Font.PLAIN, 16);

    public Notification(JFrame parent, String title, String message, boolean isError) {
        super(parent, title, true); // Modal dialog

        // Configure dialog
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        // Create main panel with Metro theme styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(isError ? Color.RED : METRO_BLUE, 5));

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(isError ? Color.RED : METRO_BLUE);
        JLabel headerLabel = new JLabel(title, SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Create message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(MESSAGE_FONT);
        messageLabel.setForeground(METRO_BLUE);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(METRO_BLUE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(okButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to the dialog
        add(mainPanel);
    }

    public static void showMessage(JFrame parent, String message) {
        Notification notification = new Notification(parent, "Message", message, false);
        notification.setVisible(true);
    }

    public static void showErrorMessage(JFrame parent, String message) {
        Notification notification = new Notification(parent, "Error", message, true);
        notification.setVisible(true);
    }
}
