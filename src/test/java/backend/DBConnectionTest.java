package backend;

import org.junit.jupiter.api.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DBConnectionTest {

    private DBConnection dbConnection;

    @BeforeAll
    void setup() {
        dbConnection = new DBConnection();
    }

    @Test
    void testConnectionInitialization() {
        Connection conn = DBConnection.getConnection();
        assertNotNull(conn, "Database connection should not be null");
        assertTrue(DBConnection.isConnectionOpen(), "Database connection should be open");
    }

    @Test
    void testReinitializeConnection() {
        Connection initialConnection = DBConnection.getConnection();

        // Close the connection manually to simulate connection loss
        try {
            initialConnection.close();
        } catch (Exception e) {
            fail("Failed to close connection for test setup");
        }

        // Get a new connection and check if it's reinitialized
        Connection newConnection = DBConnection.getConnection();
        assertNotNull(newConnection, "Database connection should be reinitialized and not null");
    }

    @Test
    void testIsConnectionOpenWhenClosed() {
        Connection conn = DBConnection.getConnection();

        try {
            conn.close();
        } catch (Exception e) {
            fail("Failed to close connection for test setup");
        }

        assertFalse(DBConnection.isConnectionOpen(), "isConnectionOpen should return false when connection is closed");
    }

    @Test
    void testIsConnectionOpen() {
        Connection conn = DBConnection.getConnection();
        assertTrue(DBConnection.isConnectionOpen(), "isConnectionOpen should return true when connection is open");
    }

    @Test
    void testMultipleGetConnectionCalls() {
        Connection conn1 = DBConnection.getConnection();
        Connection conn2 = DBConnection.getConnection();

        assertNotNull(conn1, "First connection should not be null");
        assertNotNull(conn2, "Second connection should not be null");
        assertEquals(conn1, conn2, "Both connections should point to the same instance");
    }

    @AfterAll
    void teardown() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("DB connection closed successfully after tests.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error closing database connection after tests");
        }
    }
}
