package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SuperAdminTest {

    private SuperAdmin superAdmin;
    private Connection conn;

    @BeforeEach
    void setUp() {
        superAdmin = new SuperAdmin();
        conn = DBConnection.getConnection();
    }

    @AfterEach
    void tearDown() {
        try {
            // Remove test branch managers
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM BranchManager WHERE branchCode LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test BranchManager records deleted.");
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
    void testCreateBranch() {
        String branchCode = "TEST001";
        String name = "Test Branch";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        int result = superAdmin.createBranch(branchCode, name, city, address, phone);

        assertEquals(1, result, "Branch creation failed");

        // Verify the branch was inserted
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Branch WHERE branchCode = ?")) {
            pstmt.setString(1, branchCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Branch not found in database");
                assertEquals(name, rs.getString("name"), "Branch name mismatch");
                assertEquals(city, rs.getString("city"), "Branch city mismatch");
                assertEquals(address, rs.getString("address"), "Branch address mismatch");
                assertEquals(phone, rs.getString("phone"), "Branch phone mismatch");
            }
        } catch (SQLException e) {
            fail("Error verifying branch in database: " + e.getMessage());
        }
    }

    @Test
    void testAddBranchManager() {
        String branchCode = "TEST002";
        String branchName = "Test Branch Manager Branch";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // First, create the branch
        superAdmin.createBranch(branchCode, branchName, city, address, phone);

        String managerName = "Test Manager";
        String email = "test.manager@example.com";
        String cnic = "12345-6789012-3";
        String salary = "50000";
        String phoneNumber = "0987654321";

        int result = superAdmin.addBranchManager(branchCode, managerName, email, cnic, salary, phoneNumber);

        assertEquals(1, result, "Branch Manager creation failed");

        // Verify the manager was inserted
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM BranchManager WHERE branchCode = ? AND email = ?")) {
            pstmt.setString(1, branchCode);
            pstmt.setString(2, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Branch Manager not found in database");
                assertEquals(managerName, rs.getString("name"), "Manager name mismatch");
                assertEquals(cnic, rs.getString("cnic"), "Manager CNIC mismatch");
                assertEquals(salary, rs.getString("salary"), "Manager salary mismatch");
                assertEquals(phoneNumber, rs.getString("phonenumber"), "Manager phone mismatch");
            }
        } catch (SQLException e) {
            fail("Error verifying branch manager in database: " + e.getMessage());
        }
    }

    @Test
    void testIncreaseEmployeeCount() {
        String branchCode = "TEST003";
        String branchName = "Test Branch for Employee Count";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // First, create the branch
        superAdmin.createBranch(branchCode, branchName, city, address, phone);

        // Increase employee count
        int result = superAdmin.increaseEmployeeCount(branchCode);

        assertEquals(1, result, "Failed to increase employee count");

        // Verify the employee count was updated
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT noOfEmployees FROM Branch WHERE branchCode = ?")) {
            pstmt.setString(1, branchCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    assertEquals(1, rs.getInt("noOfEmployees"), "Employee count mismatch");
                } else {
                    fail("Branch not found in database");
                }
            }
        } catch (SQLException e) {
            fail("Error verifying employee count in database: " + e.getMessage());
        }
    }

    @Test
    void testGetAllBranchNames() {
        // Create multiple branches
        superAdmin.createBranch("TEST004", "Branch One", "City One", "Address One", "1111111111");
        superAdmin.createBranch("TEST005", "Branch Two", "City Two", "Address Two", "2222222222");

        // Fetch branch names
        String[] branchNames = superAdmin.getAllBranchNames();

        assertNotNull(branchNames, "Branch names should not be null");
        assertTrue(Arrays.asList(branchNames).containsAll(Arrays.asList("Branch One", "Branch Two")),
                "Branch names do not match expected values");
    }

    @Test
    void testGetBranchCodeByName() {
        String branchCode = "TEST006";
        String branchName = "Test Branch for Code Retrieval";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create a branch
        superAdmin.createBranch(branchCode, branchName, city, address, phone);

        // Fetch branch code by name
        String fetchedBranchCode = superAdmin.getBranchCodeByName(branchName);

        assertEquals(branchCode, fetchedBranchCode, "Branch code does not match expected value");
    }
}
