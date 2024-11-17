package backend;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BranchManager {
    public  Connection conn;
    private int empId;
    private String branchCode;
    private String name;
    private String email;
    private String cnic;
    private String password;
    private String salary;
    private String phoneNumber;

     public  BranchManager() {
         conn = DBConnection.getConnection();
     }
    public BranchManager(int empId, String branchCode, String name, String email, String cnic, String password, String salary, String phoneNumber) {
        this.empId = empId;
        this.branchCode = branchCode;
        this.name = name;
        this.email = email;
        this.cnic = cnic;
        this.password = password;
        this.salary = salary;
        this.phoneNumber = phoneNumber;
    }


    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public boolean addCashier(String branchCode, String name, String email, String salary) {
        return addEmployee(branchCode, name, email, "cashier", salary);
    }

    // Function to add Data Entry Operator
    public boolean addDataEntryOperator(String branchCode, String name, String email, String salary) {
        return addEmployee(branchCode, name, email, "data_entry_operator", salary);
    }

    // Common function to add employees
    private boolean addEmployee(String branchCode, String name, String email, String role, String salary) {
        try  {

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

            // Check for duplicate email
            String emailCheckQuery = "SELECT email FROM Employee WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(emailCheckQuery)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Duplicate email detected. Cannot add employee.");
                        return false;
                    }
                }
            }

            // Insert Employee data
            String insertQuery = """
                INSERT INTO Employee (branchCode, name, email, role, salary)
                VALUES (?, ?, ?, ?, ?);
                """;
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, branchCode);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, role);
                pstmt.setString(5, salary);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println(role.substring(0, 1).toUpperCase() + role.substring(1) + " added successfully.");
                    SuperAdmin superAdmin= new SuperAdmin();
                    superAdmin.increaseEmployeeCount(branchCode);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error adding " + role + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Function to change password
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try{
            // Verify old password
            String verifyQuery = "SELECT password FROM BranchManager WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(verifyQuery)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String currentPassword = rs.getString("password");
                        if (!currentPassword.equals(oldPassword)) {
                            System.out.println("Old password is incorrect.");
                            return false;
                        }
                    } else {
                        System.out.println("Employee not found.");
                        return false;
                    }
                }
            }

            // Update password
            String updateQuery = "UPDATE BranchManager SET password = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Password updated successfully.");
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "BranchManager{" +
                "empId=" + empId +
                ", branchCode='" + branchCode + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cnic='" + cnic + '\'' +
                ", salary='" + salary + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
