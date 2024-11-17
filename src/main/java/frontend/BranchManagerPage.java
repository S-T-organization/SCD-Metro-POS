package frontend;

import javax.swing.*;
import java.awt.*;

public class BranchManagerPage extends JFrame
{
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    public BranchManagerPage(JFrame previousFrame) {
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

        // Add action listeners for navigation
        if (text.equals("Create Data Entry Operator")) {
            button.addActionListener(e -> {
                // TODO: Implement Create Data Entry Operator functionality
                System.out.println("Create Data Entry Operator clicked");
            });
        } else if (text.equals("Create Cashier")) {
            button.addActionListener(e -> {
                // TODO: Implement Create Cashier functionality
                System.out.println("Create Cashier clicked");
            });
        }

        return button;
    }
}