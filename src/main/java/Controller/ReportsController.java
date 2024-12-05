package Controller;

import backend.PercentageCalculator;
import backend.ReportsManager;

import java.util.Map;

public class ReportsController {
    private final ReportsManager reportsManager;
    private final PercentageCalculator percentageCalculator;

    public ReportsController() {
        this.reportsManager = new ReportsManager();
        this.percentageCalculator = new PercentageCalculator(reportsManager);
    }

    public String GetBranchNameById(String branchId) {
        return reportsManager.getBranchNameByCode(branchId);
    }
    /**
     * Fetch sales percentages for products.
     * If branchId is null, fetches data for all branches.
     *
     * @param time     The timeframe (e.g., "weekly", "monthly", "annually").
     * @param branchId The branch ID (or null for all branches).
     * @return A map of product names to their sales percentages.
     */
    public Map<String, Double> getProductSalesPercentages(String time, String branchId) {
        return percentageCalculator.calculateProductSalesPercentage(time, branchId);
    }

    /**
     * Fetch total sales for a given timeframe.
     *
     * @param time The timeframe (e.g., "weekly", "monthly", "annually").
     * @return The total sales for the specified timeframe.
     */
    public double getTotalSalesByTime(String time) {
        return reportsManager.getTotalSalesByTimeFrame(time);
    }

    /**
     * Fetch total sales for a given timeframe and branch.
     *
     * @param time     The timeframe (e.g., "weekly", "monthly", "annually").
     * @param branchId The branch ID to filter the sales.
     * @return The total sales for the specified timeframe and branch.
     */
    public double getTotalSalesByBranchAndTime(String time, String branchId) {
        if (branchId == null) {
            return getTotalSalesByTime(time);
        }
        return reportsManager.getTotalSalesByBranchAndTime(time, branchId);
    }

    /**
     * Fetch sales data (list of product and their sales) for a given timeframe and branch.
     * If branchId is null, fetches data for all branches.
     *
     * @param time     The timeframe (e.g., "weekly", "monthly", "annually").
     * @param branchId The branch ID (or null for all branches).
     * @return A list of `ReportData` objects containing branch code, product name, and total sales.
     */
    public Map<String, Double> getSalesByTimeAndBranch(String time, String branchId) {
        return percentageCalculator.calculateProductSalesPercentage(time, branchId);
    }
}
