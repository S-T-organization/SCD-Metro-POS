package backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PercentageCalculator {
    private final ReportsManager reportsManager;

    public PercentageCalculator(ReportsManager reportsManager) {
        this.reportsManager = reportsManager;
    }


    public Map<String, Double> calculateProductSalesPercentage(String time, String branchId) {
        Map<String, Double> productSalesPercentage = new HashMap<>();

        // Fetch total sales (filtered by branch ID if provided)
        double totalSales = branchId == null
                ? reportsManager.getTotalSalesByTimeFrame(time) // Total sales for all branches
                : reportsManager.getTotalSalesByBranchAndTime(time, branchId); // Total sales for the specific branch

        if (totalSales == 0) {
            System.out.println("Total sales is zero. Cannot calculate percentages.");
            return productSalesPercentage;
        }

        // Fetch sales for each product (filtered by branch ID if provided)
        List<ReportsManager.ReportData> reportDataList = branchId == null
                ? reportsManager.getSalesByTimeFrame(time) // Sales for all branches
                : reportsManager.getSalesByBranchAndTime(time, branchId); // Sales for the specific branch

        for (ReportsManager.ReportData data : reportDataList) {
            // Calculate percentage
            double percentage = (data.getTotalSales() / totalSales) * 100;
            productSalesPercentage.put(data.getProductName(), percentage);
        }

        return productSalesPercentage;
    }

}
