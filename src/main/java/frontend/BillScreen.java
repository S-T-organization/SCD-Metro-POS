package frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class BillScreen extends JFrame {
    private static final Font RECEIPT_FONT = new Font("Courier New", Font.PLAIN, 12);
    private static final Font RECEIPT_FONT_BOLD = new Font("Courier New", Font.BOLD, 12);
    private final List<String[]> billDetails;
    private final double total;
    private final String branchName;
    private final double taxRate = 0.1; // 10% tax

    public BillScreen(JFrame parent, List<String[]> billDetails, double total, String branchName) {
        this.billDetails = billDetails;
        this.total = total;
        this.branchName = branchName;

        setTitle("Receipt");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(parent);
        setResizable(true); // Allow resizing

        // Main panel with white background
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout());

        // Receipt panel
        JPanel receiptPanel = new JPanel();
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));

        // Store header
        addCenteredText(receiptPanel, "Metro Cash & Carry", true);
        addCenteredText(receiptPanel, branchName + " Branch", false);
        addCenteredText(receiptPanel, "Lahore, Punjab", false);
        addCenteredText(receiptPanel, "Tel: 042-35314159", false);
        addCenteredText(receiptPanel, "NTN: 1234567-8", false);
        addSeparator(receiptPanel);

        // Date and Time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
        Date now = new Date();

        addLeftAlignedText(receiptPanel, "Date: " + dateFormat.format(now), false);
        addLeftAlignedText(receiptPanel, "Time: " + timeFormat.format(now), false);
        addSeparator(receiptPanel);

        // Column headers
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(Color.WHITE);
        addText(headerPanel, "Qty Item", false, SwingConstants.LEFT);
        addText(headerPanel, "Price", false, SwingConstants.RIGHT);
        addText(headerPanel, "Total", false, SwingConstants.RIGHT);
        receiptPanel.add(headerPanel);
        addSeparator(receiptPanel);

        // Bill items
        for (String[] item : billDetails) {
            JPanel itemPanel = new JPanel(new GridLayout(1, 3));
            itemPanel.setBackground(Color.WHITE);

            try {
                double itemPrice = Double.parseDouble(item[1].trim());
                int itemQuantity = Integer.parseInt(item[2].trim());
                double itemTotal = itemPrice * itemQuantity;

                addText(itemPanel, itemQuantity + " " + item[0], false, SwingConstants.LEFT);
                addText(itemPanel, String.format("%.2f", itemPrice), false, SwingConstants.RIGHT);
                addText(itemPanel, String.format("%.2f", itemTotal), false, SwingConstants.RIGHT);

                receiptPanel.add(itemPanel);
            } catch (NumberFormatException e) {
                // Skip invalid items
                System.err.println("Invalid item in billDetails: " + e.getMessage());
            }
        }

        addSeparator(receiptPanel);

        // Tax Calculation
        double tax = total * taxRate;
        double grandTotal = total + tax;

        // Total Amount
        JPanel totalPanel = new JPanel(new GridLayout(1, 2));
        totalPanel.setBackground(Color.WHITE);
        addText(totalPanel, "Total Amount:", true, SwingConstants.LEFT);
        addText(totalPanel, String.format("Rs. %.2f", total), true, SwingConstants.RIGHT);
        receiptPanel.add(totalPanel);

        // Tax Amount
        JPanel taxPanel = new JPanel(new GridLayout(1, 2));
        taxPanel.setBackground(Color.WHITE);
        addText(taxPanel, "Tax (10%):", true, SwingConstants.LEFT);
        addText(taxPanel, String.format("Rs. %.2f", tax), true, SwingConstants.RIGHT);
        receiptPanel.add(taxPanel);

        // Grand Total
        JPanel grandTotalPanel = new JPanel(new GridLayout(1, 2));
        grandTotalPanel.setBackground(Color.WHITE);
        addText(grandTotalPanel, "Grand Total:", true, SwingConstants.LEFT);
        addText(grandTotalPanel, String.format("Rs. %.2f", grandTotal), true, SwingConstants.RIGHT);
        receiptPanel.add(grandTotalPanel);

        addSeparator(receiptPanel);

        // Footer
        addCenteredText(receiptPanel, "Thank you for shopping with us!", false);
        addCenteredText(receiptPanel, "Please come again", false);
        addCenteredText(receiptPanel, "*** End of Bill ***", false);

        // Add receiptPanel directly to mainPanel
        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton downloadButton = new JButton("Download PDF");
        downloadButton.setBackground(new Color(13, 110, 253));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFocusPainted(false);
        downloadButton.addActionListener(e -> downloadPDF(branchName, tax, grandTotal));

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        buttonsPanel.add(downloadButton);
        buttonsPanel.add(closeButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Add a component listener to adjust the frame size
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = receiptPanel.getPreferredSize().height + buttonsPanel.getPreferredSize().height + 100;
                setSize(width, height);
            }
        });
    }

    private void addCenteredText(JPanel panel, String text, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(bold ? RECEIPT_FONT_BOLD : RECEIPT_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
    }

    private void addLeftAlignedText(JPanel panel, String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(bold ? RECEIPT_FONT_BOLD : RECEIPT_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    private void addText(JPanel panel, String text, boolean bold, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(bold ? RECEIPT_FONT_BOLD : RECEIPT_FONT);
        panel.add(label);
    }

    private void addSeparator(JPanel panel) {
        JLabel separator = new JLabel("-".repeat(48), SwingConstants.CENTER);
        separator.setFont(RECEIPT_FONT);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(separator);
    }

    private void downloadPDF(String branchName, double tax, double grandTotal) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream("Receipt.pdf"));
            document.open();

            com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.COURIER, 12);
            com.itextpdf.text.Font boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12);

            // Header
            Paragraph header = new Paragraph("Metro Cash & Carry\n", boldFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            document.add(new Paragraph(branchName + " Branch\nLahore, Punjab\nTel: 042-35314159\nNTN: 1234567-8\n", normalFont));
            document.add(new Paragraph("-".repeat(48) + "\n", normalFont));

            // Date and Time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a");
            Date now = new Date();
            document.add(new Paragraph("Date: " + dateFormat.format(now) + "\nTime: " + timeFormat.format(now) + "\n", normalFont));
            document.add(new Paragraph("-".repeat(48) + "\n", normalFont));

            // Items
            for (String[] item : billDetails) {
                try {
                    double itemPrice = Double.parseDouble(item[1].trim());
                    int itemQuantity = Integer.parseInt(item[2].trim());
                    double itemTotal = itemPrice * itemQuantity;

                    Paragraph itemLine = new Paragraph(
                            String.format("%-20s %10.2f %10.2f",
                                    itemQuantity + " " + item[0],
                                    itemPrice,
                                    itemTotal
                            ),
                            normalFont
                    );
                    document.add(itemLine);
                } catch (NumberFormatException e) {
                    // Skip invalid items
                    System.err.println("Invalid item in PDF generation: " + e.getMessage());
                }
            }

            document.add(new Paragraph("-".repeat(48) + "\n", normalFont));

            // Total
            document.add(new Paragraph(String.format("Total Amount: %33.2f", total), boldFont));
            document.add(new Paragraph(String.format("Tax (10%%): %36.2f", tax), boldFont));
            document.add(new Paragraph(String.format("Grand Total: %32.2f", grandTotal), boldFont));
            document.add(new Paragraph("-".repeat(48) + "\n", normalFont));

            // Footer
            Paragraph footer = new Paragraph(
                    "Thank you for shopping with us!\nPlease come again\n*** End of Bill ***",
                    normalFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            Notification.showMessage(null, "PDF saved as Receipt.pdf");
        } catch (Exception e) {
            Notification.showErrorMessage(null, "Error creating PDF: " + e.getMessage());
        }
    }

}

