package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        deleteTestSales();
        deleteTestProducts();
        deleteTestBranch("TEST001");
        validateCleanup();
    }

    @Test
    void testGetTotalSales() {
        addTestProduct("TEST001", "Test Product A", 100.0, 150.0, "Test description A", 10);
        addTestSale("TEST001", "Test Product A", 1500.0, 10);

        double totalSales = reportsManager.getTotalSales_test("TEST001"); // Filter by branchCode
        assertEquals(1500.0, totalSales, 0.01, "Total sales do not match expected value.");
    }

    @Test
    void testGetProductSalesData() {
        addTestProduct("TEST001", "Test Product H", 300.0, 400.0, "Test description H", 10);
        addTestProduct("TEST001", "Test Product I", 500.0, 700.0, "Test description I", 15);

        addTestSale("TEST001", "Test Product H", 4000.0, 10);
        addTestSale("TEST001", "Test Product I", 10500.0, 15);

        Map<String, Double> productSalesData = reportsManager.getProductSalesData_test("TEST001", "monthly"); // Filter by branchCode
        assertEquals(2, productSalesData.size(), "Unexpected number of product sales records.");
        assertEquals(4000.0, productSalesData.get("Test Product H"), 0.01, "Sales mismatch for Product H.");
        assertEquals(10500.0, productSalesData.get("Test Product I"), 0.01, "Sales mismatch for Product I.");
    }

    @Test
    void testGetBranchSalesData() {
        addTestProduct("TEST001", "Test Product C", 50.0, 70.0, "Test description C", 20);
        addTestSale("TEST001", "Test Product C", 1400.0, 20);

        Map<String, Double> branchSalesData = reportsManager.getBranchSalesData_test("TEST001", "monthly"); // Filter by branchCode
        assertTrue(branchSalesData.containsKey("Test Branch TEST001"), "Branch not found in sales data.");
        assertEquals(1400.0, branchSalesData.get("Test Branch TEST001"), 0.01, "Branch sales data mismatch.");
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

    private void addTestProduct(String branchCode, String productName, double originalPrice, double salesPrice, String description, int quantity) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, productDescription, quantity, dateAdded, vendorId)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, productName);
            pstmt.setDouble(3, originalPrice);
            pstmt.setDouble(4, salesPrice);
            pstmt.setString(5, description);
            pstmt.setInt(6, quantity);
            pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setInt(8, 1); // Assume test vendorId = 1
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error adding test product: " + e.getMessage());
        }
    }

    private void addTestSale(String branchCode, String productName, double totalSales, int productsSold) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Sales (branch_code, product_name, product_original_price, product_sales_price, products_sold, total_sales, sale_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, productName);
            pstmt.setDouble(3, 100.0); // Dummy original price
            pstmt.setDouble(4, totalSales / productsSold); // Sales price per product
            pstmt.setInt(5, productsSold);
            pstmt.setDouble(6, totalSales); // Total sales
            pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now())); // Current date
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error adding test sale: " + e.getMessage());
        }
    }

    private void deleteTestSales() {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM Sales WHERE branch_code = 'TEST001'
                """)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error deleting test sales: " + e.getMessage());
        }
    }

    private void deleteTestProducts() {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                DELETE FROM Products WHERE branchCode = 'TEST001'
                """)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            fail("Error deleting test products: " + e.getMessage());
        }
    }

    private void validateCleanup() {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT COUNT(*) FROM Sales WHERE branch_code = 'TEST001'
                """)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assertEquals(0, rs.getInt(1), "Test sales cleanup failed!");
            }
        } catch (SQLException e) {
            fail("Error validating sales cleanup: " + e.getMessage());
        }

        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT COUNT(*) FROM Products WHERE branchCode = 'TEST001'
                """)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assertEquals(0, rs.getInt(1), "Test products cleanup failed!");
            }
        } catch (SQLException e) {
            fail("Error validating products cleanup: " + e.getMessage());
        }
    }
}
