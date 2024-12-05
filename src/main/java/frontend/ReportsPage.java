package frontend;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class ReportsPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private ChartPanel pieChartPanel;
    private ChartPanel barChartPanel;
    private JComboBox<String> timeFrameSelector;

    public ReportsPage(JFrame previousFrame) {
        setTitle("Metro Billing System - Reports");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Main scrollable panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(METRO_YELLOW);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(METRO_BLUE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Navigation buttons panel
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);

        // Return button on left
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(new ReturnButton(this, previousFrame));

        // Exit button on right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(new ExitButton());

        navPanel.add(leftPanel, BorderLayout.WEST);
        navPanel.add(rightPanel, BorderLayout.EAST);

        // Metro text in center of header
        JLabel metroLabel = new JLabel("METRO", SwingConstants.CENTER);
        metroLabel.setFont(new Font("Arial", Font.BOLD, 48));
        metroLabel.setForeground(Color.WHITE);

        headerPanel.add(navPanel, BorderLayout.NORTH);
        headerPanel.add(metroLabel, BorderLayout.CENTER);

        // Subtitle Panel
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setBackground(METRO_YELLOW);
        subtitlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel subtitleLabel = new JLabel("REPORTS DASHBOARD");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(METRO_BLUE);
        subtitlePanel.add(subtitleLabel);

        // Time Frame Selector Panel
        JPanel selectorPanel = new JPanel();
        selectorPanel.setBackground(METRO_YELLOW);
        selectorPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        selectorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        timeFrameSelector = new JComboBox<>(new String[]{"Weekly", "Monthly", "Annually"});
        timeFrameSelector.setFont(new Font("Arial", Font.BOLD, 16));
        timeFrameSelector.setPreferredSize(new Dimension(200, 40));
        timeFrameSelector.setMaximumSize(new Dimension(200, 40));
        timeFrameSelector.setBackground(Color.WHITE);
        timeFrameSelector.setForeground(METRO_BLUE);
        timeFrameSelector.addActionListener(e -> updateCharts());

        JLabel selectorLabel = new JLabel("Select Time Frame: ");
        selectorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        selectorLabel.setForeground(METRO_BLUE);

        selectorPanel.add(selectorLabel);
        selectorPanel.add(timeFrameSelector);

        // Charts Panel
        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(METRO_YELLOW);

        // Initialize charts
        pieChartPanel = createPieChart();
        pieChartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pieChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barChartPanel = createBarChart();
        barChartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        barChartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to charts panel
        chartsPanel.add(pieChartPanel);
        chartsPanel.add(Box.createVerticalStrut(20));
        chartsPanel.add(barChartPanel);

        // Add all components to main panel
        mainPanel.add(headerPanel);
        mainPanel.add(subtitlePanel);
        mainPanel.add(selectorPanel);
        mainPanel.add(chartsPanel);

        // Create scroll pane for the entire content
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setContentPane(scrollPane);
    }

    private ChartPanel createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        updatePieChartData(dataset);

        JFreeChart chart = ChartFactory.createPieChart(
                "Sales Distribution by Product",
                dataset,
                true,
                true,
                false
        );

        customizeChart(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        return chartPanel;
    }

    private ChartPanel createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        updateBarChartData(dataset);

        JFreeChart chart = ChartFactory.createBarChart(
                "Sales Performance by Product",
                "Product",
                "Total Sales (Rs)",
                dataset
        );

        customizeChart(chart);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        customizeBarPlot(plot);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        return chartPanel;
    }

    private void customizeChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 20));
        chart.getTitle().setPaint(METRO_BLUE);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setBackgroundPaint(new Color(255, 255, 255, 200));
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void customizeBarPlot(CategoryPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(METRO_BLUE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, METRO_BLUE);
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, METRO_YELLOW);
        renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
    }

    private void updateCharts() {
        String timeFrame = (String) timeFrameSelector.getSelectedItem();

        // Update Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        updatePieChartData(pieDataset);
        ((PiePlot) pieChartPanel.getChart().getPlot()).setDataset(pieDataset);

        // Update Bar Chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        updateBarChartData(barDataset);
        ((CategoryPlot) barChartPanel.getChart().getPlot()).setDataset(barDataset);
    }

    private void updatePieChartData(DefaultPieDataset dataset) {
        String timeFrame = (String) timeFrameSelector.getSelectedItem();

        switch (timeFrame) {
            case "Weekly":
                dataset.setValue("Sunsilk", 15);
                dataset.setValue("Turkish Iphone", 25);
                dataset.setValue("Sugar", 40);
                dataset.setValue("Dalda Cooking Oil", 20);
                break;
            case "Monthly":
                dataset.setValue("Sunsilk", 20);
                dataset.setValue("Turkish Iphone", 30);
                dataset.setValue("Sugar", 35);
                dataset.setValue("Dalda Cooking Oil", 15);
                break;
            case "Annually":
                dataset.setValue("Sunsilk", 25);
                dataset.setValue("Turkish Iphone", 35);
                dataset.setValue("Sugar", 25);
                dataset.setValue("Dalda Cooking Oil", 15);
                break;
        }
    }

    private void updateBarChartData(DefaultCategoryDataset dataset) {
        String timeFrame = (String) timeFrameSelector.getSelectedItem();

        switch (timeFrame) {
            case "Weekly":
                dataset.addValue(1500.00, "Sales", "Sunsilk");
                dataset.addValue(3500.00, "Sales", "Turkish Iphone");
                dataset.addValue(8250.00, "Sales", "Sugar");
                dataset.addValue(1000.00, "Sales", "Dalda Cooking Oil");
                break;
            case "Monthly":
                dataset.addValue(6000.00, "Sales", "Sunsilk");
                dataset.addValue(14000.00, "Sales", "Turkish Iphone");
                dataset.addValue(33000.00, "Sales", "Sugar");
                dataset.addValue(4000.00, "Sales", "Dalda Cooking Oil");
                break;
            case "Annually":
                dataset.addValue(72000.00, "Sales", "Sunsilk");
                dataset.addValue(168000.00, "Sales", "Turkish Iphone");
                dataset.addValue(396000.00, "Sales", "Sugar");
                dataset.addValue(48000.00, "Sales", "Dalda Cooking Oil");
                break;
        }
    }

}