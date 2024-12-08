package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DataEntryOperatorTest {

    private DataEntryOperator dataEntryOperator;
    private Connection conn;

    @BeforeAll
    void setUpAll() {
        dataEntryOperator = new DataEntryOperator();
        conn = DBConnection.getConnection();
    }

    @BeforeEach
    void setupTestData() {
        try {
            // Insert test branch
            String insertBranch = """
                INSERT INTO Branch (branchCode, name, city, address, phone)
                VALUES ('TEST999', 'Test Branch', 'Test City', 'Test Address', '1234567890')
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertBranch)) {
                pstmt.executeUpdate();
            }

            // Insert test vendor
            String insertVendor = """
                INSERT INTO Vendors (vendorId, vendorName, vendorCnic, vendorPhone, vendorAddress)
                VALUES (9999, 'Test Vendor', '12345-6789012-3', '1234567890', 'Test Vendor Address')
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertVendor)) {
                pstmt.executeUpdate();
            }

            // Insert test employee
            String insertEmployee = """
                INSERT INTO Employee (email, password, branchCode, role)
                VALUES ('testdeo@example.com', 'password123', 'TEST999', 'data_entry_operator')
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertEmployee)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error setting up test data");
        }
    }

    @AfterEach
    void cleanupTestData() {
        try {
            // Remove test employee
            String deleteEmployee = "DELETE FROM Employee WHERE email = 'testdeo@example.com'";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteEmployee)) {
                pstmt.executeUpdate();
            }

            // Remove test vendor
            String deleteVendor = "DELETE FROM Vendors WHERE vendorId = 9999";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteVendor)) {
                pstmt.executeUpdate();
            }

            // Remove test branch
            String deleteBranch = "DELETE FROM Branch WHERE branchCode = 'TEST999'";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteBranch)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error cleaning up test data");
        }
    }

    @Test
    void testLoginSuccess() {
        int result = dataEntryOperator.login("testdeo@example.com", "password123", "TEST999");
        assertEquals(1, result, "Login should be successful");
    }

    @Test
    void testLoginInvalidCredentials() {
        int result = dataEntryOperator.login("wrong@example.com", "wrongpassword", "TEST999");
        assertEquals(0, result, "Login should fail due to invalid credentials");
    }

    @Test
    void testGetAllBranchNames() {
        String[] branchNames = dataEntryOperator.getAllBranchNames();
        assertNotNull(branchNames, "Branch names should not be null");
        assertTrue(Arrays.asList(branchNames).contains("Test Branch"), "Branch names should include 'Test Branch'");
    }

    @Test
    void testGetBranchCodeByName() {
        String branchCode = dataEntryOperator.getBranchCodeByName("Test Branch");
        assertNotNull(branchCode, "Branch code should not be null");
        assertEquals("TEST999", branchCode, "Branch code should match the expected value");
    }

    @Test
    void testAddVendorSuccess() {
        int result = dataEntryOperator.addVendor("New Vendor", "12345-6789012-5", "9876543210", "New Vendor Address");
        assertEquals(1, result, "Vendor should be added successfully");

        // Cleanup
        try {
            String deleteVendor = "DELETE FROM Vendors WHERE vendorCnic = '12345-6789012-5'";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteVendor)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error cleaning up test vendor data");
        }
    }

    @Test
    void testAddVendorDuplicateCnic() {
        int result = dataEntryOperator.addVendor("Duplicate Vendor", "12345-6789012-3", "9876543210", "Duplicate Address");
        assertEquals(0, result, "Vendor addition should fail due to duplicate CNIC");
    }

    @Test
    void testAddProductsSuccess() {
        List<Product> products = List.of(
                new Product("TEST999", "Product A", "50.0", "60.0", "Product A Description", "10", "9999"),
                new Product("TEST999", "Product B", "100.0", "120.0", "Product B Description", "5", "9999")
        );

        int result = dataEntryOperator.addProducts(products);
        assertEquals(1, result, "Products should be added successfully");

        // Cleanup
        try {
            String deleteProducts = "DELETE FROM Products WHERE branchCode = 'TEST999'";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteProducts)) {
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error cleaning up test product data");
        }
    }

    @Test
    void testChangePasswordSuccess() {
        int result = dataEntryOperator.changePassword("testdeo@example.com", "newpassword");
        assertEquals(1, result, "Password should be updated successfully");

        // Reset the password back for other tests
        int resetResult = dataEntryOperator.changePassword("testdeo@example.com", "password123");
        assertEquals(1, resetResult, "Password should be reset successfully");
    }

    @Test
    void testChangePasswordInvalidEmail() {
        int result = dataEntryOperator.changePassword("invalid@example.com", "newpassword");
        assertEquals(5, result, "Password change should fail for non-existent email");
    }
}
