package frontend;

/*
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
*/

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.util.List;

public class BillScreen extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private final List<String[]> billDetails;
    private final double total;

    public BillScreen(JFrame parent, List<String[]> billDetails, double total) {
        this.billDetails = billDetails;
        this.total = total;

        setTitle("Metro Billing System - Bill");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 620); // Increased by 20 pixels in both dimensions to account for the border
        setLocationRelativeTo(parent);
        setResizable(true);

        // Main content panel
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

                // Bill text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String headerText = "BILL";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(headerText);
                g2d.drawString(headerText, (getWidth() - textWidth) / 2, 70);
            }
        };
        setContentPane(contentPanel);

        // Add a border to the content pane
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.darkGray, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create center panel to display bill details
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JTable billTable = createBillTable();
        centerPanel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        JLabel totalLabel = new JLabel(String.format("Total: Rs%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalLabel.setForeground(METRO_BLUE);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        centerPanel.add(totalLabel, BorderLayout.SOUTH);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setOpaque(false);

        JButton downloadButton = createStyledButton("Download PDF");
        //downloadButton.addActionListener(e -> downloadBillAsPDF());

        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> dispose());

        buttonsPanel.add(downloadButton);
        buttonsPanel.add(closeButton);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private JTable createBillTable() {
        String[] columnNames = {"Product Name", "Price", "Quantity"};
        Object[][] data = billDetails.stream().map(row -> row).toArray(Object[][]::new);

        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(METRO_BLUE);
        table.setSelectionForeground(Color.WHITE);

        return table;
    }

    /*
    private void downloadBillAsPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Bill.pdf"));

            document.open();

            // Add title
            Paragraph title = new Paragraph("Metro Billing System - Bill", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Create table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Product Name");
            table.addCell("Price");
            table.addCell("Quantity");

            for (String[] row : billDetails) {
                table.addCell(row[0]);
                table.addCell(row[1]);
                table.addCell(row[2]);
            }

            // Add total
            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(String.format("Total: $%.2f", total), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));

            document.close();
            Notification.showMessage(this, "Bill downloaded as PDF successfully!");
        } catch (Exception e) {
            Notification.showErrorMessage(this, "Failed to download PDF: " + e.getMessage());
        }
    }
    */

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
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}