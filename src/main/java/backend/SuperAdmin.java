package backend;

import java.sql.*;
import java.util.ArrayList;

public class SuperAdmin {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private Connection conn;

    public SuperAdmin() {
        System.out.println("Super Admin Initialized");
        conn = DBConnection.getConnection();
    }

    public boolean login(String inputUsername, String inputPassword) {
        return USERNAME.equals(inputUsername) && PASSWORD.equals(inputPassword);
    }

    public boolean createBranch(String branchCode, String name, String city, String address, String phone) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            // Save operation to file when database is disconnected
            String columns = "branchCode,name,city,address,phone,noOfEmployees,isActive";
            String values = String.join(",", branchCode, name, city, address, phone, "0", "1");
            CheckConnectionOfInternet.saveOperationToFile("Branch", columns, values);
            CheckConnectionOfInternet.writeTempFile(true);
            System.out.println("Database not connected. Branch creation operation saved to file.");
            return false;
        }

        try {
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
                    INSERT INTO Branch (branchCode, name, city, address, phone, noOfEmployees, isActive)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, branchCode);
                pstmt.setString(2, name);
                pstmt.setString(3, city);
                pstmt.setString(4, address);
                pstmt.setString(5, phone);
                pstmt.setInt(6, 0); // Default value for noOfEmployees
                pstmt.setBoolean(7, true); // Default value for isActive

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
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            // Save operation to file when database is disconnected
            String columns = "branchCode,name,email,cnic,password,salary,phoneNumber";
            String values = String.join(",", branchCode, name, email, cnic, "123",salary, phoneNumber);
            CheckConnectionOfInternet.saveOperationToFile("BranchManager", columns, values);
            CheckConnectionOfInternet.writeTempFile(true);



            System.out.println("Database not connected. Branch Manager creation operation saved to file.");
            return false;
        }

        try {
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
                pstmt.setString(7, phoneNumber);

                int rowsInserted = pstmt.executeUpdate();
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
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            // Save the operation to the update file when database is disconnected
            String updateQuery = "UPDATE Branch SET noOfEmployees = noOfEmployees + 1 WHERE branchCode = ?";
            String params = branchCode;
            CheckConnectionOfInternet.saveUpdateToFile("Branch", updateQuery, params);
            CheckConnectionOfInternet.writeTempFile(true);
            System.out.println("Database not connected. Employee count increment saved to file.");
            return false;
        }

        String updateBranchQuery = """
        UPDATE Branch
        SET noOfEmployees = noOfEmployees + 1
        WHERE branchCode = ?;
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(updateBranchQuery)) {
            pstmt.setString(1, branchCode);

            int rowsUpdated = pstmt.executeUpdate();
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

    public String[] getAllBranchNames() {
        ArrayList<String> branchNames = new ArrayList<>();

        String query = "SELECT name FROM Branch";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                branchNames.add(rs.getString("name"));
            }

        } catch (Exception e) {
            System.out.println("Error fetching branch names: " + e.getMessage());
            e.printStackTrace();
        }

        return branchNames.toArray(new String[0]); // Convert ArrayList to String array
    }

    public String getBranchCodeByName(String branchName) {
        String branchCode = null;
        String query = "SELECT branchCode FROM Branch WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    branchCode = rs.getString("branchCode");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching branch code for name '" + branchName + "': " + e.getMessage());
            e.printStackTrace();
        }

        return branchCode;
    }

}
