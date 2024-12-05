package frontend;

import Controller.ReportsController;
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
import java.util.Map;

public class ReportsPage extends JFrame {
    private static final Color METRO_YELLOW = new Color(230, 190, 0);
    private static final Color METRO_BLUE = new Color(0, 41, 84);

    private final ReportsController reportsController;
    private ChartPanel pieChartPanelProduct;
    private ChartPanel pieChartPanelBranch;
    private ChartPanel barChartPanelProduct;
    private ChartPanel barChartPanelBranch;
    private JComboBox<String> timeFrameSelector;

    public ReportsPage(JFrame previousFrame) {
        reportsController = new ReportsController();

        setTitle("Metro Billing System - Reports");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Main scrollable panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(METRO_YELLOW);

        // Header Panel
        JPanel headerPanel = createHeaderPanel(previousFrame);

        // Subtitle Panel
        JPanel subtitlePanel = createSubtitlePanel();

        // Time Frame Selector Panel
        JPanel selectorPanel = createTimeFrameSelector();

        // Charts Panel
        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(METRO_YELLOW);

        // Initialize charts
        pieChartPanelProduct = createPieChart("Sales Distribution by Product", true);
        pieChartPanelBranch = createPieChart("Sales Distribution by Branch", false);
        barChartPanelProduct = createBarChart("Sales Performance by Product", true);
        barChartPanelBranch = createBarChart("Sales Performance by Branch", false);

        // Add components to charts panel
        chartsPanel.add(pieChartPanelProduct);
        chartsPanel.add(Box.createVerticalStrut(20));
        chartsPanel.add(pieChartPanelBranch);
        chartsPanel.add(Box.createVerticalStrut(20));
        chartsPanel.add(barChartPanelProduct);
        chartsPanel.add(Box.createVerticalStrut(20));
        chartsPanel.add(barChartPanelBranch);

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

    private JPanel createHeaderPanel(JFrame previousFrame) {
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

        return headerPanel;
    }

    private JPanel createSubtitlePanel() {
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setBackground(METRO_YELLOW);
        subtitlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel subtitleLabel = new JLabel("REPORTS DASHBOARD");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(METRO_BLUE);
        subtitlePanel.add(subtitleLabel);
        return subtitlePanel;
    }

    private JPanel createTimeFrameSelector() {
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
        return selectorPanel;
    }

    private ChartPanel createPieChart(String title, boolean isProduct) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        updatePieChartData(dataset, isProduct);

        JFreeChart chart = ChartFactory.createPieChart(
                title,
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

    private ChartPanel createBarChart(String title, boolean isProduct) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        updateBarChartData(dataset, isProduct);

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                isProduct ? "Product" : "Branch", // X-axis label: Product or Branch
                "Total Sales (Rs)",               // Y-axis label: Total Sales in Rs
                dataset
        );

        customizeChart(chart);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        customizeBarPlot(plot);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));

        return chartPanel;
    }


    private void updatePieChartData(DefaultPieDataset dataset, boolean isProduct) {
        String timeFrame = (String) timeFrameSelector.getSelectedItem();
        Map<String, Double> data;

        if (isProduct) {
            // Fetch sales by product
            data = reportsController.getProductSalesPercentages(timeFrame, null);
        } else {
            // Fetch sales by branch with branch names
            data = reportsController.getBranchSalesPercentages(timeFrame);
        }

        data.forEach(dataset::setValue);
    }

    private void updateBarChartData(DefaultCategoryDataset dataset, boolean isProduct) {
        String timeFrame = (String) timeFrameSelector.getSelectedItem();
        Map<String, Double> data;

        if (isProduct) {
            // Fetch total sales for products
            data = reportsController.getProductSalesData(timeFrame); // Update backend to include this method
        } else {
            // Fetch total sales for branches
            data = reportsController.getBranchSalesData(timeFrame); // This method is already in ReportsController
        }

        // Populate the dataset with total sales data
        data.forEach((key, value) -> dataset.addValue(value, "Sales", key));
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

        // Update Pie Charts
        DefaultPieDataset pieDatasetProduct = new DefaultPieDataset();
        updatePieChartData(pieDatasetProduct, true);
        ((PiePlot) pieChartPanelProduct.getChart().getPlot()).setDataset(pieDatasetProduct);

        DefaultPieDataset pieDatasetBranch = new DefaultPieDataset();
        updatePieChartData(pieDatasetBranch, false);
        ((PiePlot) pieChartPanelBranch.getChart().getPlot()).setDataset(pieDatasetBranch);

        // Update Bar Charts
        DefaultCategoryDataset barDatasetProduct = new DefaultCategoryDataset();
        updateBarChartData(barDatasetProduct, true);
        ((CategoryPlot) barChartPanelProduct.getChart().getPlot()).setDataset(barDatasetProduct);

        DefaultCategoryDataset barDatasetBranch = new DefaultCategoryDataset();
        updateBarChartData(barDatasetBranch, false);
        ((CategoryPlot) barChartPanelBranch.getChart().getPlot()).setDataset(barDatasetBranch);
    }
}
