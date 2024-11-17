package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ExitButton extends JButton {
    private static final Color EXIT_RED = new Color(220, 53, 69);
    private static final Color HOVER_RED = new Color(200, 35, 51);

    public ExitButton() {
        setText("X");
        setFont(new Font("Arial", Font.BOLD, 20));
        setForeground(Color.WHITE);
        setBackground(EXIT_RED);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(30, 30));
        setMargin(new Insets(0, 0, 0, 0));

        addActionListener(e -> System.exit(0));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(HOVER_RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(EXIT_RED);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.setColor(getForeground());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }
}