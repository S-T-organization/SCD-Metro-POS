package backend;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CashierTest {

    private Cashier cashier;
    private Connection conn;

    @BeforeEach
    void setUp() {
        cashier = new Cashier();
        conn = DBConnection.getConnection();
    }

    @AfterEach
    void tearDown() {
        try {
            // Remove test sales
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Sales WHERE branch_code LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test Sales records deleted.");
            }

            // Remove test products
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Products WHERE branchCode LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test Products records deleted.");
            }

            // Remove test employees
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Employee WHERE branchCode LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test Employee records deleted.");
            }

            // Remove test branches
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Branch WHERE branchCode LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test Branch records deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Error cleaning up test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void testLogin() {
        String branchCode = "TEST001";
        String branchName = "Test Branch for Cashier Login";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a cashier
        String cashierName = "Test Cashier";
        String email = "cashier_login@example.com";
        String salary = "30000";

        new BranchManager().addCashier(branchCode, cashierName, email, salary);

        // Perform login
        int result = cashier.login(email, "123", branchCode);

        assertEquals(1, result, "Failed to login with correct credentials");
    }

    void testGetAllBranchNames() {
        new SuperAdmin().createBranch("TEST002", "Branch One", "City One", "Address One", "1111111111");
        new SuperAdmin().createBranch("TEST003", "Branch Two", "City Two", "Address Two", "2222222222");

        // Fetch branch names
        String[] branchNames = cashier.getAllBranchNames();

        assertNotNull(branchNames, "Branch names should not be null");
        assertTrue(Arrays.asList(branchNames).containsAll(Arrays.asList("Branch One", "Branch Two")), "Branch names do not match expected values");
    }

    @Test
    void testGetBranchCodeByName() {
        String branchCode = "TEST004";
        String branchName = "Test Branch for Code Retrieval";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create a branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Fetch branch code by name
        String fetchedBranchCode = cashier.getBranchCodeByName(branchName);

        assertEquals(branchCode, fetchedBranchCode, "Branch code does not match expected value");
    }

    @Test
    void testChangePassword() {
        String branchCode = "TEST007";
        String branchName = "Test Branch for Password Change";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a cashier
        String cashierName = "Cashier Password Test";
        String email = "cashier_password@example.com";
        String salary = "30000";

        new BranchManager().addCashier(branchCode, cashierName, email, salary);

        // Change password
        String newPassword = "new_password";
        int result = cashier.changePassword(email, newPassword);

        assertEquals(1, result, "Failed to change password");

        // Verify the password was updated
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM Employee WHERE email = ? AND role = 'cashier'")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals(newPassword, rs.getString("password"), "Password mismatch");
                } else {
                    fail("Cashier not found after password change");
                }
            }
        } catch (SQLException e) {
            fail("Error verifying password change: " + e.getMessage());
        }
    }

    @Test
    void testGetProductById() {
        String branchCode = "TEST005";
        String branchName = "Test Branch for Products";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a product
        String productName = "TEST Product";
        String originalPrice = "100";
        String salesPrice = "80";
        String productDescription = "A test product";
        String quantity = "10";
        String vendorId = "1";
        String productId = null;

        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, productDescription, quantity, dateAdded, vendorId)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, productName);
            pstmt.setString(3, originalPrice);
            pstmt.setString(4, salesPrice);
            pstmt.setString(5, productDescription);
            pstmt.setString(6, quantity);
            pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setString(8, vendorId);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    productId = String.valueOf(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            fail("Error adding test product: " + e.getMessage());
        }

        assertNotNull(productId, "Product ID should not be null");

        // Fetch the product by ID
        Product product = cashier.getProductById(productId);

        assertNotNull(product, "Product should not be null");
        assertEquals(productName, product.getProductName(), "Product name mismatch");
    }

    @Test
    void testRemoveProduct() {
        String branchCode = "TEST006";
        String branchName = "Test Branch for Remove Product";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a product
        String productName = "TEST Product To Remove";
        String originalPrice = "200";
        String salesPrice = "150";
        String productDescription = "Product for removal test";
        String quantity = "20";
        String vendorId = "1";
        String productId = null;

        try (PreparedStatement pstmt = conn.prepareStatement("""
                INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, productDescription, quantity, dateAdded, vendorId)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, productName);
            pstmt.setString(3, originalPrice);
            pstmt.setString(4, salesPrice);
            pstmt.setString(5, productDescription);
            pstmt.setString(6, quantity);
            pstmt.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setString(8, vendorId);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    productId = String.valueOf(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            fail("Error adding test product: " + e.getMessage());
        }

        assertNotNull(productId, "Product ID should not be null");

        // Remove the product
        List<String> items = List.of(productName + ",10");
        String result = cashier.removeProduct(items, branchCode);

        assertEquals("true", result, "Product removal failed");

        // Verify product quantity is updated
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT quantity FROM Products WHERE productId = ?")) {
            pstmt.setString(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals("10", rs.getString("quantity"), "Product quantity mismatch after removal");
                } else {
                    fail("Product not found after removal");
                }
            }
        } catch (SQLException e) {
            fail("Error verifying product quantity: " + e.getMessage());
        }
    }
}
