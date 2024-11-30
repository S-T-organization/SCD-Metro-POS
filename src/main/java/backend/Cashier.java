package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Product getProductById(String productId) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot fetch product.");
            return null; // Return null or handle offline operations here if needed
        }

        String query = "SELECT * FROM Products WHERE productId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product(
                            rs.getString("branchCode"),
                            rs.getString("productName"),
                            rs.getString("originalPrice"),
                            rs.getString("salesPrice"),
                            rs.getString("productDescription"),
                            rs.getString("quantity"),
                            rs.getString("vendorId")
                    );
                    System.out.println("Product fetched successfully: " + product.getProductName());
                    return product;
                } else {
                    System.out.println("No product found with ID: " + productId);
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public String removeProduct(List<String> namesAndQuantity) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot perform operation.");
            return "false,Internet unavailable";
        }

        StringBuilder result = new StringBuilder("true");

        try {
            conn.setAutoCommit(false); // Start transaction

            for (String item : namesAndQuantity) {
                // Split the productName and quantity from the input string
                String[] parts = item.split(",");
                String productName = parts[0].trim();
                int quantityNeeded = Integer.parseInt(parts[1].trim());

                // Fetch the product's current quantity from the database
                String query = "SELECT productId, quantity FROM Products WHERE productName = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, productName);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int productId = rs.getInt("productId");
                            int currentQuantity = Integer.parseInt(rs.getString("quantity"));

                            if (currentQuantity >= quantityNeeded) {
                                // Reduce the quantity or remove the product if quantity becomes zero
                                int updatedQuantity = currentQuantity - quantityNeeded;

                                if (updatedQuantity > 0) {
                                    // Update the product's quantity
                                    String updateQuery = "UPDATE Products SET quantity = ? WHERE productId = ?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                        updateStmt.setString(1, String.valueOf(updatedQuantity));
                                        updateStmt.setInt(2, productId);
                                        updateStmt.executeUpdate();
                                    }
                                } else {
                                    // Remove the product from the database
                                    String deleteQuery = "DELETE FROM Products WHERE productId = ?";
                                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                        deleteStmt.setInt(1, productId);
                                        deleteStmt.executeUpdate();
                                    }
                                }
                            } else {
                                // Not enough quantity available, rollback and return false with product name
                                conn.rollback();
                                System.out.println("Not enough quantity available for: " + productName);
                                return "false," + productName;
                            }
                        } else {
                            // Product not found, rollback and return false
                            conn.rollback();
                            System.out.println("Product not found: " + productName);
                            return "false," + productName;
                        }
                    }
                }
            }

            conn.commit(); // Commit transaction if all operations succeed
        } catch (Exception e) {
            try {
                conn.rollback(); // Rollback transaction on any error
                System.out.println("Transaction rolled back due to error: " + e.getMessage());
            } catch (Exception rollbackEx) {
                System.out.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
            return "false,Error occurred during operation";
        } finally {
            try {
                conn.setAutoCommit(true); // Reset auto-commit
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result.toString(); // If all operations are successful, return "true"
    }
}
