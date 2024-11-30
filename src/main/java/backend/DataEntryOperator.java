package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public int addVendor(String vendorName, String vendorCnic, String vendorPhone, String vendorAddress) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Saving vendor operation to file.");
            String columns = "vendorName,vendorCnic,vendorPhone,vendorAddress";
            String values = String.join(",", vendorName, vendorCnic, vendorPhone, vendorAddress);
            CheckConnectionOfInternet.saveOperationToFile("Vendors", columns, values);
            CheckConnectionOfInternet.writeTempFile(true);
            return -1; // Indicates the operation is saved offline
        }

        try {
            // Ensure the Vendors table exists
            createVendorsTableIfNotExists();

            // Check if CNIC already exists
            String checkQuery = "SELECT vendorId FROM Vendors WHERE vendorCnic = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, vendorCnic);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Duplicate CNIC detected: " + vendorCnic);
                        return 0; // Duplicate CNIC
                    }
                }
            }

            // Insert vendor into the table
            String insertQuery = """
                INSERT INTO Vendors (vendorName, vendorCnic, vendorPhone, vendorAddress)
                VALUES (?, ?, ?, ?)
            """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, vendorName);
                pstmt.setString(2, vendorCnic);
                pstmt.setString(3, vendorPhone);
                pstmt.setString(4, vendorAddress);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Vendor added successfully.");
                    return 1; // Success
                } else {
                    System.out.println("Failed to add the vendor.");
                    return 0; // Insert failed
                }
            }
        } catch (Exception e) {
            System.out.println("Error adding vendor: " + e.getMessage());
            return -1; // Error
        }
    }
    private void createVendorsTableIfNotExists() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS Vendors (
                vendorId INT AUTO_INCREMENT PRIMARY KEY,
                vendorName VARCHAR(100) NOT NULL,
                vendorCnic VARCHAR(50) NOT NULL UNIQUE, -- CNIC must be unique
                vendorPhone VARCHAR(20) NOT NULL,
                vendorAddress VARCHAR(255) NOT NULL
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Vendors table verified/created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating Vendors table: " + e.getMessage());
        }
    }
    public int addProducts(List<Product> products) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Saving product operations to file.");
            for (Product product : products) {
                String columns = "branchCode,productName,originalPrice,salesPrice,productDescription,quantity,dateAdded,vendorId";
                String values = String.join(",", product.getBranchCode(), product.getProductName(),
                        product.getOriginalPrice(), product.getSalesPrice(),
                        product.getProductDescription(), product.getQuantity(),
                        LocalDate.now().toString(), product.getVendorId());
                CheckConnectionOfInternet.saveOperationToFile("Products", columns, values);
            }
            CheckConnectionOfInternet.writeTempFile(true);
            return -1; // Indicates the operation is saved offline
        }

        try {
            createProductsTableIfNotExists(); // Ensure Products table exists

            conn.setAutoCommit(false); // Start transaction

            for (Product product : products) {
                int result = addSingleProduct(product);
                if (result <= 0) {
                    conn.rollback(); // Rollback if any operation fails
                    System.out.println("Transaction rolled back due to failure for product: " + product.getProductName());
                    return -3; // Rollback occurred
                }
            }

            conn.commit(); // Commit transaction if all succeed
            System.out.println("All products added successfully.");
            return 1; // Success
        } catch (Exception e) {
            System.out.println("Error adding products: " + e.getMessage());
            try {
                conn.rollback(); // Rollback on error
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return -3; // Transaction rollback due to error
        } finally {
            try {
                conn.setAutoCommit(true); // Reset auto-commit
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int addSingleProduct(Product product) {
        try {
            // Check if the product already exists
            String checkQuery = "SELECT productId, quantity FROM Products WHERE productName = ? AND branchCode = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, product.getProductName());
                checkStmt.setString(2, product.getBranchCode());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Product exists, update the quantity
                        String productId = rs.getString("productId");
                        int existingQuantity = Integer.parseInt(rs.getString("quantity"));
                        int newQuantity = existingQuantity + Integer.parseInt(product.getQuantity());

                        String updateQuery = """
                            UPDATE Products 
                            SET quantity = ?, originalPrice = ?, salesPrice = ?, 
                                productDescription = ?, dateAdded = ?
                            WHERE productId = ?
                        """;
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, String.valueOf(newQuantity));
                            updateStmt.setString(2, product.getOriginalPrice());
                            updateStmt.setString(3, product.getSalesPrice());
                            updateStmt.setString(4, product.getProductDescription());
                            updateStmt.setString(5, LocalDate.now().toString());
                            updateStmt.setString(6, productId);

                            int rowsUpdated = updateStmt.executeUpdate();
                            if (rowsUpdated > 0) {
                                System.out.println("Product updated successfully: " + product.getProductName());
                                return 1; // Success
                            } else {
                                System.out.println("Failed to update the product: " + product.getProductName());
                                return 0; // Update failed
                            }
                        }
                    } else {
                        // Product does not exist, insert a new record
                        String insertQuery = """
                            INSERT INTO Products (branchCode, productName, originalPrice, salesPrice, 
                                                  productDescription, quantity, dateAdded, vendorId)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, product.getBranchCode());
                            insertStmt.setString(2, product.getProductName());
                            insertStmt.setString(3, product.getOriginalPrice());
                            insertStmt.setString(4, product.getSalesPrice());
                            insertStmt.setString(5, product.getProductDescription());
                            insertStmt.setString(6, product.getQuantity());
                            insertStmt.setString(7, LocalDate.now().toString());
                            insertStmt.setString(8, product.getVendorId());

                            int rowsInserted = insertStmt.executeUpdate();
                            if (rowsInserted > 0) {
                                System.out.println("Product added successfully: " + product.getProductName());
                                return 1; // Success
                            } else {
                                System.out.println("Failed to add the product: " + product.getProductName());
                                return 0; // Insert failed
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing product: " + product.getProductName() + " - " + e.getMessage());
            return -1; // Error
        }
    }

    private void createProductsTableIfNotExists() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS Products (
                productId INT AUTO_INCREMENT PRIMARY KEY,
                branchCode VARCHAR(50) NOT NULL,
                productName VARCHAR(100) NOT NULL,
                originalPrice VARCHAR(50) NOT NULL,
                salesPrice VARCHAR(50) NOT NULL,
                productDescription TEXT,
                quantity VARCHAR(50) NOT NULL,
                dateAdded VARCHAR(50) NOT NULL,
                vendorId INT NOT NULL,
                FOREIGN KEY (vendorId) REFERENCES Vendors(vendorId),
                UNIQUE(branchCode, productName)
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Products table verified/created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating Products table: " + e.getMessage());
        }
    }
    public String[] getAllVendors() {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot fetch vendors.");
            return null; // Return null or empty array as needed
        }

        ArrayList<String> vendorNames = new ArrayList<>();
        String query = "SELECT vendorName FROM Vendors";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                vendorNames.add(rs.getString("vendorName"));
            }
            System.out.println("Fetched all vendor names successfully.");
        } catch (Exception e) {
            System.out.println("Error fetching vendor names: " + e.getMessage());
        }

        return vendorNames.toArray(new String[0]); // Convert ArrayList to String array
    }
    public String getVendorIdByVendorName(String vendorName) {
        if (!CheckConnectionOfInternet.isInternetAvailable()) {
            System.out.println("Internet unavailable. Cannot fetch vendor ID.");
            return null; // Return null in case of no internet
        }

        String query = "SELECT vendorId FROM Vendors WHERE vendorName = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, vendorName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Vendor ID fetched successfully for vendor: " + vendorName);
                    return rs.getString("vendorId");
                } else {
                    System.out.println("No vendor found with name: " + vendorName);
                    return null; // Vendor not found
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching vendor ID for vendor name '" + vendorName + "': " + e.getMessage());
            return null; // Error occurred
        }
    }
}
