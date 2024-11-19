package frontend;

import javax.swing.*;
import java.awt.*;

public class MainLoginPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    public MainLoginPage() {
        setTitle("Metro Billing System - Login");
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
                String tagline = "SELECT YOUR ROLE";
                FontMetrics fm2 = g2d.getFontMetrics();
                int taglineWidth = fm2.stringWidth(tagline);
                g2d.drawString(tagline, (getWidth() - taglineWidth) / 2, 160);
            }
        };
        setContentPane(contentPanel);

        // Add exit button
        ExitButton exitButton = new ExitButton();
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(exitButton);
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

        String[] buttonTexts = {"Super Admin", "Branch Manager", "Cashier", "Data Entry Operator"};
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

        // Add action listeners for navigation
        if (text.equals("Super Admin")) {
            button.addActionListener(e -> {
                dispose(); // Close the current frame
                new SuperAdminLoginPage(this).setVisible(true); // Pass the current frame
            });
        }
        else if (text.equals("Branch Manager"))
        {
            button.addActionListener(e -> {
                dispose(); // Close the current frame
                new BranchManagerLoginPage(this).setVisible(true); // Pass the current frame
            });
        }
        else if (text.equals("Data Entry Operator")) {
            button.addActionListener(e -> {
                dispose(); // Close the current frame
                new DataEntryOperatorLoginPage(this).setVisible(true); // Pass the current frame
            });
        }
        else if (text.equals("Cashier")) {
            button.addActionListener(e -> {
                dispose(); // Close the current frame
                new CashierLoginPage(this).setVisible(true); // Pass the current frame
            });
        }

        return button;
    }
}
