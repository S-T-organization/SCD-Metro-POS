package backend;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
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

    public Product getProductById(String productId,String BranchCode) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot fetch product.");
            return null; // Return null or handle offline operations here if needed
        }

        String query = "SELECT * FROM Products WHERE productId = ? AND branchCode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, productId);
            pstmt.setString(2, BranchCode);
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
    public String removeProduct(List<String> namesAndQuantities, String branchCode) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot perform operation.");
            return "false,Internet unavailable";
        }

        createSalesTableIfNotExists(); // Ensure the Sales table exists
        StringBuilder result = new StringBuilder("true");

        try {
            conn.setAutoCommit(false); // Start transaction

            for (String item : namesAndQuantities) {
                // Split the productName and quantity from the input string
                String[] parts = item.split(",");
                String productName = parts[0].trim();
                int quantityNeeded = Integer.parseInt(parts[1].trim());

                // Fetch the product's current details from the database
                String query = """
                SELECT productId, quantity, originalPrice, salesPrice 
                FROM Products WHERE productName = ? AND branchCode = ?
            """;
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, productName);
                    pstmt.setString(2, branchCode);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int productId = rs.getInt("productId");
                            int currentQuantity = Integer.parseInt(rs.getString("quantity"));
                            String originalPrice = rs.getString("originalPrice");
                            String salesPrice = rs.getString("salesPrice");

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

                                // Insert record into the Sales table
                                String insertSalesQuery = """
                                INSERT INTO Sales (branch_code, product_name, product_original_price, product_sales_price, products_sold, total_sales, sale_date)
                                VALUES (?, ?, ?, ?, ?, ?, ?)
                            """;
                                try (PreparedStatement insertSalesStmt = conn.prepareStatement(insertSalesQuery)) {
                                    insertSalesStmt.setString(1, branchCode); // branch_code
                                    insertSalesStmt.setString(2, productName); // product_name
                                    insertSalesStmt.setString(3, originalPrice); // product_original_price
                                    insertSalesStmt.setString(4, salesPrice); // product_sales_price
                                    insertSalesStmt.setInt(5, quantityNeeded); // products_sold
                                    insertSalesStmt.setBigDecimal(6, new BigDecimal(quantityNeeded).multiply(new BigDecimal(salesPrice))); // total_sales
                                    insertSalesStmt.setDate(7, Date.valueOf(LocalDate.now())); // sale_date
                                    insertSalesStmt.executeUpdate();
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
    private void createSalesTableIfNotExists() {
        String createTableQuery = """
        CREATE TABLE IF NOT EXISTS Sales (
            saleId INT AUTO_INCREMENT PRIMARY KEY,
            branch_code VARCHAR(50) NOT NULL,
            product_name VARCHAR(100) NOT NULL,
            product_original_price VARCHAR(50) NOT NULL,
            product_sales_price VARCHAR(50) NOT NULL,
            products_sold INT NOT NULL,
            total_sales DECIMAL(10, 2) NOT NULL,
            sale_date DATE NOT NULL,
            FOREIGN KEY (branch_code) REFERENCES Branch(branchCode) ON DELETE CASCADE
        )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Sales table verified/created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating Sales table: " + e.getMessage());
        }
    }
    public int changePassword(String email, String newPassword) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot change password.");
            return -1;
        }

        try {
            String updateQuery = "UPDATE Employee SET password = ? WHERE email = ? AND role = 'cashier'";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully.");
                    return 1;
                } else {
                    System.out.println("Cashier not found.");
                    return 5;
                }
            }
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public String getBranchNameByCode(String branchCode) {
        String branchName = null;
        String query = "SELECT name FROM Branch WHERE branchCode = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, branchCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    branchName = rs.getString("name");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching branch name for code '" + branchCode + "': " + e.getMessage());
            e.printStackTrace();
        }

        return branchName;
    }
}