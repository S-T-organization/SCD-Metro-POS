package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BranchManagerTest {

    private BranchManager branchManager;
    private Connection conn;

    @BeforeEach
    void setUp() {
        branchManager = new BranchManager();
        conn = DBConnection.getConnection();
    }

    @AfterEach
    void tearDown() {
        try {
            // Remove test employees
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Employee WHERE branchCode LIKE 'TEST%'")) {
                pstmt.executeUpdate();
                System.out.println("Test Employee records deleted.");
            }

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
    void testAddCashier() {
        String branchCode = "TEST001";
        String branchName = "Test Branch for Cashier";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a cashier
        String cashierName = "Test Cashier";
        String email = "cashier@example.com";
        String salary = "30000";

        int result = branchManager.addCashier(branchCode, cashierName, email, salary);

        assertEquals(1, result, "Failed to add cashier");

        // Verify the cashier was inserted
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Employee WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Cashier not found in database");
                assertEquals(cashierName, rs.getString("name"), "Cashier name mismatch");
                assertEquals("cashier", rs.getString("role"), "Cashier role mismatch");
            }
        } catch (SQLException e) {
            fail("Error verifying cashier in database: " + e.getMessage());
        }
    }

    @Test
    void testAddDataEntryOperator() {
        String branchCode = "TEST002";
        String branchName = "Test Branch for DEO";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a data entry operator
        String deoName = "Test DEO";
        String email = "deo@example.com";
        String salary = "25000";

        int result = branchManager.addDataEntryOperator(branchCode, deoName, email, salary);

        assertEquals(1, result, "Failed to add data entry operator");

        // Verify the DEO was inserted
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Employee WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Data Entry Operator not found in database");
                assertEquals(deoName, rs.getString("name"), "DEO name mismatch");
                assertEquals("data_entry_operator", rs.getString("role"), "DEO role mismatch");
            }
        } catch (SQLException e) {
            fail("Error verifying DEO in database: " + e.getMessage());
        }
    }

    @Test
    void testChangePassword() {
        String branchCode = "TEST003";
        String branchName = "Test Branch for Password Change";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a branch manager
        String managerName = "Test Manager";
        String email = "manager@example.com";
        String salary = "50000";

        new SuperAdmin().addBranchManager(branchCode, managerName, email, "12345-6789012-3", salary, "1234567890");

        // Change password
        String newPassword = "new_password";
        int result = branchManager.changePassword(email, newPassword);

        assertEquals(1, result, "Failed to change password");

        // Verify the password was updated
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT password FROM BranchManager WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Branch Manager not found in database");
                assertEquals(newPassword, rs.getString("password"), "Password mismatch");
            }
        } catch (SQLException e) {
            fail("Error verifying password update in database: " + e.getMessage());
        }
    }

    @Test
    void testLogin() {
        String branchCode = "TEST004";
        String branchName = "Test Branch for Login";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create the branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Add a branch manager
        String managerName = "Test Manager";
        String email = "manager_login@example.com";
        String salary = "50000";

        new SuperAdmin().addBranchManager(branchCode, managerName, email, "12345-6789012-3", salary, "1234567890");

        // Perform login
        int result = branchManager.login(email, "123", branchCode);

        assertEquals(1, result, "Failed to login with correct credentials");
    }

    @Test
    void testGetAllBranchNames() {
        new SuperAdmin().createBranch("TEST005", "Branch One", "City One", "Address One", "1111111111");
        new SuperAdmin().createBranch("TEST006", "Branch Two", "City Two", "Address Two", "2222222222");

        // Fetch branch names
        String[] branchNames = branchManager.getAllBranchNames();

        assertNotNull(branchNames, "Branch names should not be null");
        assertTrue(Arrays.asList(branchNames).containsAll(Arrays.asList("Branch One", "Branch Two")),
                "Branch names do not match expected values");
    }

    @Test
    void testGetBranchCodeByName() {
        String branchCode = "TEST007";
        String branchName = "Test Branch for Code Retrieval";
        String city = "Test City";
        String address = "123 Test Street";
        String phone = "1234567890";

        // Create a branch
        new SuperAdmin().createBranch(branchCode, branchName, city, address, phone);

        // Fetch branch code by name
        String fetchedBranchCode = branchManager.getBranchCodeByName(branchName);

        assertEquals(branchCode, fetchedBranchCode, "Branch code does not match expected value");
    }
}
