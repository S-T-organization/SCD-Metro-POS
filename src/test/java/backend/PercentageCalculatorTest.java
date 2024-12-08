package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PercentageCalculatorTest {

    private PercentageCalculator percentageCalculator;
    private Connection conn;

    @BeforeAll
    void setUpAll() {
        ReportsManager reportsManager = new ReportsManager();
        percentageCalculator = new PercentageCalculator(reportsManager);
        conn = DBConnection.getConnection();
    }

    @BeforeEach
    void setupTestData() {
        try {
            // Insert vendor with a unique ID
            String insertVendor = """
                INSERT INTO Vendors (vendorId, vendorName, vendorCnic, vendorPhone, vendorAddress)
                VALUES (9999, 'Test Vendor', '12345-6789012-9', '1234567890', 'Test Vendor Address')
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertVendor)) {
                pstmt.executeUpdate();
            }

            // Insert branch with a unique code
            String insertBranch = """
                INSERT INTO Branch (branchCode, name, city, address, phone)
                VALUES ('TEST999', 'Test Branch', 'Test City', 'Test Address', '9876543210')
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertBranch)) {
                pstmt.executeUpdate();
            }

            // Insert products with unique names
            String insertProduct1 = """
                INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, productDescription, quantity, dateAdded, vendorId)
                VALUES ('TEST999', 'Test Product A', 100.0, 120.0, 'Description A', 50, ?, 9999)
            """;
            String insertProduct2 = """
                INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, productDescription, quantity, dateAdded, vendorId)
                VALUES ('TEST999', 'Test Product B', 200.0, 240.0, 'Description B', 30, ?, 9999)
            """;
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertProduct1);
                 PreparedStatement pstmt2 = conn.prepareStatement(insertProduct2)) {
                pstmt1.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                pstmt2.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                pstmt1.executeUpdate();
                pstmt2.executeUpdate();
            }

            // Insert sales for the test products
            String insertSales1 = """
                INSERT INTO Sales (branch_code, product_name, product_original_price, product_sales_price, products_sold, total_sales, sale_date)
                VALUES ('TEST999', 'Test Product A', 100.0, 120.0, 20, 2400.0, ?)
            """;
            String insertSales2 = """
                INSERT INTO Sales (branch_code, product_name, product_original_price, product_sales_price, products_sold, total_sales, sale_date)
                VALUES ('TEST999', 'Test Product B', 200.0, 240.0, 10, 2400.0, ?)
            """;
            try (PreparedStatement pstmt1 = conn.prepareStatement(insertSales1);
                 PreparedStatement pstmt2 = conn.prepareStatement(insertSales2)) {
                pstmt1.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                pstmt2.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                pstmt1.executeUpdate();
                pstmt2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error setting up test data");
        }
    }

    @AfterEach
    void cleanupTestData() {
        try {
            // Remove sales data for the test branch
            String deleteSales = "DELETE FROM Sales WHERE branch_code = 'TEST999'";
            conn.prepareStatement(deleteSales).executeUpdate();

            // Remove products for the test branch
            String deleteProducts = "DELETE FROM Products WHERE branchCode = 'TEST999'";
            conn.prepareStatement(deleteProducts).executeUpdate();

            // Remove the test branch
            String deleteBranch = "DELETE FROM Branch WHERE branchCode = 'TEST999'";
            conn.prepareStatement(deleteBranch).executeUpdate();

            // Remove the test vendor
            String deleteVendor = "DELETE FROM Vendors WHERE vendorId = 9999";
            conn.prepareStatement(deleteVendor).executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error cleaning up test data");
        }
    }

    @Test
    void testCalculateProductSalesPercentageForAllBranches() {
        Map<String, Double> productSalesPercentage = percentageCalculator.calculateProductSalesPercentage("weekly", "TEST999");

        // Log results for debugging
        System.out.println("Product Sales Percentages (Test Branch): " + productSalesPercentage);

        assertNotNull(productSalesPercentage, "Product sales percentages should not be null");
        assertEquals(2, productSalesPercentage.size(), "Unexpected number of products");
        assertEquals(50.0, productSalesPercentage.get("Test Product A"), 0.1, "Percentage mismatch for Product A");
        assertEquals(50.0, productSalesPercentage.get("Test Product B"), 0.1, "Percentage mismatch for Product B");
    }
}
