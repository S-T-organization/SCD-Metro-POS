package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReportsManagerTest {

    private ReportsManager reportsManager;
    private Connection conn;

    @BeforeAll
    void setUpAll() {
        reportsManager = new ReportsManager();
        conn = DBConnection.getConnection();
    }

    @BeforeEach
    void setupTestData() {
        setupTestBranch("TEST001");
    }

    @AfterEach
    void cleanupTestData() {
        deleteTestSales("TEST001");
        deleteTestBranch("TEST001");
    }

    @Test
    void testGetTotalSales() {
        addTestSale("TEST001", "Product A", 1000.00, 10);
        addTestSale("TEST001", "Product B", 500.00, 5);

        double totalSales = reportsManager.getTotalSales();
        assertEquals(1500.00, totalSales, 0.01, "Total sales do not match expected value.");
    }

    @Test
    void testGetBranchSalesData() {
        addTestSale("TEST001", "Product C", 700.00, 7);
        addTestSale("TEST001", "Product D", 300.00, 3);

        Map<String, Double> branchSalesData = reportsManager.getBranchSalesData("monthly");
        assertTrue(branchSalesData.containsKey("Test Branch TEST001"), "Branch not found in sales data.");
        assertEquals(1000.00, branchSalesData.get("Test Branch TEST001"), 0.01, "Branch sales data mismatch.");
    }

    @Test
    void testGetSalesByBranchAndTime() {
        addTestSale("TEST001", "Product E", 1200.00, 12);

        var salesData = reportsManager.getSalesByBranchAndTime("monthly", "TEST001");
        assertEquals(1, salesData.size(), "Unexpected number of sales records.");
        assertEquals(1200.00, salesData.get(0).getTotalSales(), 0.01, "Sales total mismatch.");
    }


    @Test
    void testGetProductSalesData() {
        addTestSale("TEST001", "Product H", 300.00, 3);
        addTestSale("TEST001", "Product I", 700.00, 7);

        Map<String, Double> productSalesData = reportsManager.getProductSalesData("monthly");
        assertEquals(2, productSalesData.size(), "Unexpected number of product sales records.");
        assertEquals(300.00, productSalesData.get("Product H"), 0.01, "Sales mismatch for Product H.");
        assertEquals(700.00, productSalesData.get("Product I"), 0.01, "Sales mismatch for Product I.");
    }

    private void setupTestBranch(String branchCode) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Branch (branchCode, name, city, address, phone)
                VALUES (?, ?, ?, ?, ?)
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, "Test Branch " + branchCode);
            pstmt.setString(3, "Test City");
            pstmt.setString(4, "Test Address");
            pstmt.setString(5, "1234567890");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error setting up test branch: " + e.getMessage());
        }
    }

    private void deleteTestBranch(String branchCode) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM Branch WHERE branchCode = ?
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error deleting test branch: " + e.getMessage());
        }
    }

    private void addTestSale(String branchCode, String productName, double totalSales, int productsSold) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Sales (branch_code, product_name, product_original_price, product_sales_price, products_sold, total_sales, sale_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, productName);
            pstmt.setDouble(3, 100.00); // Default original price
            pstmt.setDouble(4, totalSales); // Match sales price with total sales
            pstmt.setInt(5, productsSold);
            pstmt.setDouble(6, totalSales);
            pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error adding test sale: " + e.getMessage());
        }
    }

    private void deleteTestSales(String branchCode) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM Sales WHERE branch_code = ?
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error deleting test sales: " + e.getMessage());
        }
    }
}

