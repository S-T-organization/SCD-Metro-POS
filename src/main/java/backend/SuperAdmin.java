package backend;

import java.sql.*;

public class SuperAdmin {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public boolean login(String inputUsername, String inputPassword) {
        return USERNAME.equals(inputUsername) && PASSWORD.equals(inputPassword);
    }

    public boolean createBranch(String branchCode, String name, String city, String address, String phone) {
        try (Connection conn = DBConnection.getConnection()) {

            String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS Branch (
                        branchCode VARCHAR(50) PRIMARY KEY,
                        name VARCHAR(100),
                        city VARCHAR(100),
                        address VARCHAR(255),
                        phone VARCHAR(15),
                        noOfEmployees INT DEFAULT 0,
                        isActive BOOLEAN DEFAULT TRUE
                    );
                    """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableQuery);
                System.out.println("Branch table verified/created successfully.");
            }

            String insertQuery = """
                    INSERT INTO Branch (branchCode, name, city, address, phone)
                    VALUES (?, ?, ?, ?, ?);
                    """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, branchCode);
                pstmt.setString(2, name);
                pstmt.setString(3, city);
                pstmt.setString(4, address);
                pstmt.setString(5, phone);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Branch created successfully.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error creating branch: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addBranchManager(String branchCode, String name, String email, String cnic, String salary, String phoneNumber) {
        try (Connection conn = DBConnection.getConnection()) {
            // Verify branch exists
            String branchCheckQuery = "SELECT branchCode FROM Branch WHERE branchCode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(branchCheckQuery)) {
                pstmt.setString(1, branchCode);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Branch does not exist. Cannot add Branch Manager.");
                        return false;
                    }
                }
            }

            // Create or verify BranchManager table with phoneNumber
            String createTableQuery = """
            CREATE TABLE IF NOT EXISTS BranchManager (
                empId INT AUTO_INCREMENT PRIMARY KEY,
                branchCode VARCHAR(50),
                name VARCHAR(100),
                email VARCHAR(100) UNIQUE,
                cnic VARCHAR(15) UNIQUE,
                password VARCHAR(100),
                salary VARCHAR(100),
                phonenumber VARCHAR(20),
                FOREIGN KEY (branchCode) REFERENCES Branch(branchCode)
            );
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableQuery);
                System.out.println("BranchManager table verified/created successfully.");
            }

            // Check for duplicate CNIC after table creation
            String cnicCheckQuery = "SELECT cnic FROM BranchManager WHERE cnic = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(cnicCheckQuery)) {
                pstmt.setString(1, cnic);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Duplicate CNIC detected. Cannot add Branch Manager.");
                        return false;
                    }
                }
            }

            // Insert Branch Manager data (including phoneNumber)
            String insertQuery = """
            INSERT INTO BranchManager (branchCode, name, email, cnic, password, salary, phonenumber)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, branchCode);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, cnic);
                pstmt.setString(5, "123"); // Default password
                pstmt.setString(6, salary);
                pstmt.setString(7, phoneNumber); // Include phoneNumber

                int rowsInserted = pstmt.executeUpdate();
                increaseEmployeeCount(branchCode);
                if (rowsInserted > 0) {
                    System.out.println("Branch Manager created successfully with default password '123'.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error creating Branch Manager: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean increaseEmployeeCount(String branchCode) {
        String updateBranchQuery = """
            UPDATE Branch
            SET noOfEmployees = noOfEmployees + 1
            WHERE branchCode = ?;
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateBranchQuery)) {

            pstmt.setString(1, branchCode); // Set the branch code parameter

            int rowsUpdated = pstmt.executeUpdate(); // Execute the update
            if (rowsUpdated > 0) {
                System.out.println("Employee count updated successfully for branch: " + branchCode);
                return true;
            } else {
                System.out.println("No branch found with the provided branchCode: " + branchCode);
            }
        } catch (Exception e) {
            System.out.println("Error updating employee count: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
