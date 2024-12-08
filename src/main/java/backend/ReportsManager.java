package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ReportsManager {
    private static Connection conn = null;

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

    public double getTotalSalesByTimeFrame(String timeFrame) {
        double totalSales = 0.0;
        String query = "";

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branchName;
    }

    public Map<String, Double> getBranchSalesPercentages(String timeFrame) {
        Map<String, Double> branchSalesPercentage = new HashMap<>();
        double totalSales = getTotalSalesByTimeFrame(timeFrame);

        if (totalSales == 0) {
            System.out.println("Total sales is zero. Cannot calculate percentages.");
            return branchSalesPercentage;
        }

        String query = "";

        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) " +
                        "GROUP BY branch_code";
                break;
            case "monthly":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code";
                break;
            case "annually":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return branchSalesPercentage;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String branchCode = rs.getString("branch_code");
                double branchSales = rs.getDouble("branch_sales");
                double percentage = (branchSales / totalSales) * 100;

                String branchName = getBranchNameByCode(branchCode);
                branchSalesPercentage.put(branchName != null ? branchName : branchCode, percentage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branchSalesPercentage;
    }

    public Map<String, Double> getBranchSalesData(String timeFrame) {
        Map<String, Double> branchSalesData = new HashMap<>();
        String query = "";

        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) " +
                        "GROUP BY branch_code";
                break;
            case "monthly":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code";
                break;
            case "annually":
                query = "SELECT branch_code, SUM(total_sales) AS branch_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY branch_code";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return branchSalesData;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String branchCode = rs.getString("branch_code");
                double branchSales = rs.getDouble("branch_sales");

                String branchName = getBranchNameByCode(branchCode);
                branchSalesData.put(branchName != null ? branchName : branchCode, branchSales);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branchSalesData;
    }

    public double getTotalSalesByBranchAndTime(String timeFrame, String branchId)
    {
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

    public Map<String, Double> getProductSalesData(String timeFrame) {
        Map<String, Double> productSalesData = new HashMap<>();
        String query = "";

        switch (timeFrame.toLowerCase()) {
            case "weekly":
                query = "SELECT product_name, SUM(total_sales) AS product_sales " +
                        "FROM Sales " +
                        "WHERE YEARWEEK(sale_date, 1) = YEARWEEK(CURDATE(), 1) " +
                        "GROUP BY product_name";
                break;
            case "monthly":
                query = "SELECT product_name, SUM(total_sales) AS product_sales " +
                        "FROM Sales " +
                        "WHERE MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY product_name";
                break;
            case "annually":
                query = "SELECT product_name, SUM(total_sales) AS product_sales " +
                        "FROM Sales " +
                        "WHERE YEAR(sale_date) = YEAR(CURDATE()) " +
                        "GROUP BY product_name";
                break;
            default:
                System.out.println("Invalid time frame: " + timeFrame);
                return productSalesData;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String productName = rs.getString("product_name");
                double productSales = rs.getDouble("product_sales");
                productSalesData.put(productName, productSales);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productSalesData;
    }


    public double getTotalSales_test(String branchCode) {
        double totalSales = 0.0;

        String query = """
                SELECT SUM(total_sales) AS totalSales
                FROM Sales
                WHERE branch_code = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getDouble("totalSales");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }

    public Map<String, Double> getProductSalesData_test(String branchCode, String timePeriod) {
        Map<String, Double> productSalesData = new HashMap<>();

        String timeFilter = "";
        if ("monthly".equalsIgnoreCase(timePeriod)) {
            timeFilter = "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
        } else if ("yearly".equalsIgnoreCase(timePeriod)) {
            timeFilter = "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
        }

        String query = """
                SELECT product_name, SUM(total_sales) AS totalSales
                FROM Sales
                WHERE branch_code = ?
                %s
                GROUP BY product_name
                """.formatted(timeFilter);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    double totalSales = rs.getDouble("totalSales");
                    productSalesData.put(productName, totalSales);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productSalesData;
    }

    public Map<String, Double> getBranchSalesData_test(String branchCode, String timePeriod) {
        Map<String, Double> branchSalesData = new HashMap<>();

        String timeFilter = "";
        if ("monthly".equalsIgnoreCase(timePeriod)) {
            timeFilter = "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
        } else if ("yearly".equalsIgnoreCase(timePeriod)) {
            timeFilter = "AND sale_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
        }

        String query = """
                SELECT b.name AS branchName, SUM(s.total_sales) AS totalSales
                FROM Branch b
                INNER JOIN Sales s ON b.branchCode = s.branch_code
                WHERE b.branchCode = ?
                %s
                GROUP BY b.name
                """.formatted(timeFilter);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String branchName = rs.getString("branchName");
                    double totalSales = rs.getDouble("totalSales");
                    branchSalesData.put(branchName, totalSales);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branchSalesData;
    }


    public static class ReportData
    {
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

        public double getTotalSales_test(String branchCode) {
            double totalSales = 0.0;

            String query = """
                SELECT SUM(total_sales) AS totalSales
                FROM Sales
                WHERE branch_code = ?
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, branchCode);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalSales = rs.getDouble("totalSales");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return totalSales;
        }

    }
}
