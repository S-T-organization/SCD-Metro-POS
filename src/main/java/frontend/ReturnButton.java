package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReturnButton extends JButton {
    private static final Color METRO_BLUE = new Color(0, 41, 84);
    private static final Color HOVER_BLUE = new Color(0, 62, 126);

    public ReturnButton(JFrame currentFrame, JFrame previousFrame) {
        setText("←");
        setFont(new Font("Arial", Font.BOLD, 24));
        setForeground(Color.WHITE);
        setBackground(METRO_BLUE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(40, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(HOVER_BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(METRO_BLUE);
            }
        });

        addActionListener(e -> {
            currentFrame.dispose();
            previousFrame.setVisible(true);
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2d.setColor(HOVER_BLUE);
        } else if (getModel().isRollover()) {
            g2d.setColor(HOVER_BLUE);
        } else {
            g2d.setColor(getBackground());
        }

        g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), x, y);
        g2d.dispose();
    }
}