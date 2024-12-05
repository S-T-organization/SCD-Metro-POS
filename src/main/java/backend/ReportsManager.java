package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportsManager {
    private final Connection conn;

    public ReportsManager() {
        this.conn = DBConnection.getConnection();
    }

    public double getTotalSales() {
        double totalSales = 0.0;
        String query = "SELECT SUM(total_sales) AS total_sales FROM Sales";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    // Fetch sales data by time frame
    public List<ReportData> getSalesByTimeFrame(String timeFrame) {
        List<ReportData> reportDataList = new ArrayList<>();
        String query = "";

        // Determine query based on time frame
        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) " +
                        "GROUP BY branch_code, product_name";
                break;
            case "monthly":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code, product_name";
                break;
            case "annually":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code, product_name";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return reportDataList;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Populate the list with query results
            while (rs.next()) {
                String branchCode = rs.getString("branch_code");
                String productName = rs.getString("product_name");
                double totalSales = rs.getDouble("total_sales");
                reportDataList.add(new ReportData(branchCode, productName, totalSales));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportDataList;
    }

    public double getTotalSalesByTimeFrame(String timeFrame) {
        double totalSales = 0.0;
        String query = "";

        // Determine query based on time frame
        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1)";
                break;
            case "monthly":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE())";
                break;
            case "annually":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE())";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return totalSales;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Retrieve the total sales
            if (rs.next()) {
                totalSales = rs.getDouble("total_sales");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    public String getBranchNameByCode(String branchCode) {
        String branchName = null;
        String query = "SELECT name FROM Branch WHERE branchCode = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    branchName = rs.getString("name");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching branch name for code '" + branchCode + "': " + e.getMessage());
            e.printStackTrace();
        }

        return branchName;
    }
    public double getTotalSalesByBranchAndTime(String timeFrame, String branchId) {
        double totalSales = 0.0;
        String query = "";

        // Determine query based on time frame
        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) AND branch_code = ?";
                break;
            case "monthly":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) AND branch_code = ?";
                break;
            case "annually":
                query = "SELECT SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) AND branch_code = ?";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return totalSales;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getDouble("total_sales");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    public List<ReportData> getSalesByBranchAndTime(String timeFrame, String branchId) {
        List<ReportData> reportDataList = new ArrayList<>();
        String query = "";

        // Determine query based on time frame
        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) AND branch_code = ? " +
                        "GROUP BY branch_code, product_name";
                break;
            case "monthly":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) AND branch_code = ? " +
                        "GROUP BY branch_code, product_name";
                break;
            case "annually":
                query = "SELECT branch_code, product_name, SUM(total_sales) AS total_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) AND branch_code = ? " +
                        "GROUP BY branch_code, product_name";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return reportDataList;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String branchCode = rs.getString("branch_code");
                    String productName = rs.getString("product_name");
                    double totalSales = rs.getDouble("total_sales");
                    reportDataList.add(new ReportData(branchCode, productName, totalSales));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportDataList;
    }

    public static class ReportData {
        private final String branchCode;
        private final String productName;
        private final double totalSales;

        public ReportData(String branchCode, String productName, double totalSales) {
            this.branchCode = branchCode;
            this.productName = productName;
            this.totalSales = totalSales;
        }

        public String getBranchCode() {
            return branchCode;
        }

        public String getProductName() {
            return productName;
        }

        public double getTotalSales() {
            return totalSales;
        }
    }
}
