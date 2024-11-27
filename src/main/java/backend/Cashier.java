package backend;

import java.sql.*;
import java.util.ArrayList;

public class Cashier
{
    private Connection conn;
    private int empId;
    private String branchCode;
    private String name;
    private String email;
    private String password;
    private String salary;

    public Cashier() {
        conn = DBConnection.getConnection();
    }

    // Login function for cashier
    public int login(String email, String password, String branchCode) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot perform login.");
            return -1;
        }

        try {
            String query = "SELECT * FROM Employee WHERE email = ? AND password = ? AND branchCode = ? AND role = 'cashier'";
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
                        this.password = rs.getString("password");
                        this.salary = rs.getString("salary");
                        System.out.println("Login Successful!");
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

    // Get branch names
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

    // Get branch code by branch name
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
        return "Cashier{" +
                "empId=" + empId +
                ", branchCode='" + branchCode + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
