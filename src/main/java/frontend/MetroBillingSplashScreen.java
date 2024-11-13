package frontend;

import javax.swing.*;
import java.awt.*;

public class MetroBillingSplashScreen extends JWindow {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final Color METRO_YELLOW = new Color(230, 190, 0);  // Darker yellow
    private static final Color METRO_BLUE = new Color(0, 41, 84);  // Darker blue

    private float progress = 0;
    private float slideIn = -1.0f;

    public MetroBillingSplashScreen() {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Yellow background
                g2d.setColor(METRO_YELLOW);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Blue header rectangle with slide-in animation
                int headerHeight = 120;
                int slideOffset = (int)(slideIn * getWidth());
                g2d.setColor(METRO_BLUE);
                g2d.fillRect(slideOffset, 50, getWidth() - 40, headerHeight);

                // METRO text
                if (slideIn > -0.1f) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 72));
                    String metroText = "METRO";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(metroText);
                    g2d.drawString(metroText, slideOffset + (getWidth() - 40 - textWidth) / 2, 130);
                }

                // Tagline
                g2d.setColor(METRO_BLUE);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                String tagline = "YOUR SUCCESS IS OUR BUSINESS";
                FontMetrics fm2 = g2d.getFontMetrics();
                int taglineWidth = fm2.stringWidth(tagline);
                g2d.drawString(tagline, (getWidth() - taglineWidth) / 2, 220);

                // System text
                g2d.setFont(new Font("Arial", Font.BOLD, 28));
                String systemText = "Billing System";
                int systemWidth = fm2.stringWidth(systemText);
                g2d.drawString(systemText, (getWidth() - systemWidth) / 2, 260);

                // Progress bar
                g2d.setColor(METRO_BLUE);
                g2d.fillRect(50, getHeight() - 50, getWidth() - 100, 6);
                g2d.setColor(Color.WHITE);  // Change to white color for loading
                g2d.fillRect(50, getHeight() - 50, (int)((getWidth() - 100) * progress), 6);

            }
        };
        content.setBackground(METRO_YELLOW);
        add(content, BorderLayout.CENTER);

        // Animation timer
        Timer timer = new Timer(30, e -> {
            progress += 0.01f;
            slideIn += 0.05f;

            if (slideIn > 0) slideIn = 0;

            if (progress >= 1) {
                ((Timer)e.getSource()).stop();
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(500);
                        dispose();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            repaint();
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MetroBillingSplashScreen splash = new MetroBillingSplashScreen();
            splash.setVisible(true);
        });
    }
}