package backend;

import frontend.Notification;

import java.sql.*;
import java.util.ArrayList;

public class BranchManager {
    private Connection conn;
    private int empId;
    private String branchCode;
    private String name;
    private String email;
    private String role;
    private String password;
    private String salary;

    public BranchManager() {
        conn = DBConnection.getConnection();
    }

    // Add Cashier
    public int addCashier(String branchCode, String name, String email, String salary) {
        return addEmployee(branchCode, name, email, "cashier", salary);
    }

    // Add Data Entry Operator
    public int addDataEntryOperator(String branchCode, String name, String email, String salary) {
        return addEmployee(branchCode, name, email, "data_entry_operator", salary);
    }

    // General function to add employees
    private int addEmployee(String branchCode, String name, String email, String role, String salary) {
        SuperAdmin superAdmin = new SuperAdmin();
        if (!CheckConnectionOfInternet.isInternetAvailable()) {

            System.out.println("Internet unavailable. Saving operation to file.");
            String columns = "branchCode,name,email,role,password,salary";
            String values = String.join(",", branchCode, name, email, role, "123", salary); // Default password is "123"
            CheckConnectionOfInternet.saveOperationToFile("Employee", columns, values);



            CheckConnectionOfInternet.writeTempFile(true);
            return -1;
        }

        try {
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS Employee (
                    empId INT AUTO_INCREMENT PRIMARY KEY,
                    branchCode VARCHAR(50),
                    name VARCHAR(100),
                    email VARCHAR(100) UNIQUE,
                    role VARCHAR(50),
                    password VARCHAR(100) DEFAULT '123',
                    salary VARCHAR(100),
                    FOREIGN KEY (branchCode) REFERENCES Branch(branchCode)
                );
                """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableQuery);
                System.out.println("Employee table verified/created successfully.");
            }

            String emailCheckQuery = "SELECT email FROM Employee WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(emailCheckQuery)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Duplicate email detected. Cannot add employee.");
                        return 3;
                    }
                }
            }

            String insertQuery = """
                INSERT INTO Employee (branchCode, name, email, role, password, salary)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, branchCode);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, role);
                pstmt.setString(5, "123"); // Default password
                pstmt.setString(6, salary);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println(role.substring(0, 1).toUpperCase() + role.substring(1) + " added successfully.");

                    return 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error adding " + role + ": " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Change Password
    public int changePassword(String email, String oldPassword, String newPassword) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot change password.");
            return -1;
        }

        try {
            String verifyQuery = "SELECT password FROM Employee WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(verifyQuery)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String currentPassword = rs.getString("password");
                        if (!currentPassword.equals(oldPassword)) {
                            System.out.println("Old password is incorrect.");
                            return 4;
                        }
                    } else {
                        System.out.println("Employee not found.");
                        return 5;
                    }
                }
            }

            String updateQuery = "UPDATE Employee SET password = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully.");
                    return 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Login Function
    public int login(String email, String password, String branchCode) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot perform login.");
            return -1;
        }

        try {
            String query = "SELECT * FROM Employee WHERE email = ? AND password = ? AND branchCode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.setString(3, branchCode);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        this.empId = rs.getInt("empId");
                        this.branchCode = rs.getString("branchCode");
                        this.name = rs.getString("name");
                        this.email = rs.getString("email");
                        this.role = rs.getString("role");
                        this.password = rs.getString("password");
                        this.salary = rs.getString("salary");

                        System.out.println("Login successful for: " + name + " (" + role + ") at branch: " + branchCode);
                        return 1;
                    } else {
                        System.out.println("Invalid credentials or branch code.");
                        return 6;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Get All Branch Names
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

    // Get Branch Code by Branch Name
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

    @Override
    public String toString() {
        return "BranchManager{" +
                "empId=" + empId +
                ", branchCode='" + branchCode + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
