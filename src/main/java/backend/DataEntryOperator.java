package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DataEntryOperator {
    private Connection conn;

    public DataEntryOperator() {
        conn = DBConnection.getConnection();
    }


    public int login(String email, String password, String branchCode) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot perform login.");
            return -1;
        }
        String query = "SELECT * FROM Employee WHERE email = ? AND password = ? AND branchCode = ? AND role = 'data_entry_operator'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful for Data Entry Operator: " + email);
                    return 1; // Success
                } else {
                    System.out.println("Invalid login credentials for Data Entry Operator.");
                    return 0; // Invalid credentials
                }
            }
        } catch (Exception e) {
            System.out.println("Error during login for Data Entry Operator: " + e.getMessage());
            return -1; // Error
        }
    }


    public String[] getAllBranchNames() {
        if (!CheckConnectionOfInternet.isInternetAvailable()){
            return null;
        }
        ArrayList<String> branchNames = new ArrayList<>();
        String query = "SELECT name FROM Branch";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                branchNames.add(rs.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Error fetching branch names: " + e.getMessage());
        }

        return branchNames.toArray(new String[0]); // Convert ArrayList to String array
    }


    public String getBranchCodeByName(String branchName) {
        if (!CheckConnectionOfInternet.isInternetAvailable()){
            return null;
        }
        String query = "SELECT branchCode FROM Branch WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("branchCode");
                } else {
                    System.out.println("No branch found with name: " + branchName);
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching branch code for branch name '" + branchName + "': " + e.getMessage());
        }
        return null; // Branch not found
    }
}
