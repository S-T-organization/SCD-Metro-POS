package backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class BackendMain {
    public static void main(String[] args) throws InterruptedException, SQLException {
        // Initialize the connection and monitoring

        Connection conn  = DBConnection.getConnection();
       CheckConnectionOfInternet check = new CheckConnectionOfInternet();
       check.monitorInternet();

        // Testing SuperAdmin functionalities
        SuperAdmin superAdmin = new SuperAdmin();
        System.out.println("\n=== Testing Branch Creation ===");
        superAdmin.createBranch("33", "Main Branch", "City A", "Address A", "1234567890");
        superAdmin.createBranch("34", "Branch1", "Lahore", "Paki", "1234567890");

        // Pause for user to turn off Wi-Fi
        System.out.println("\nPlease turn off your Wi-Fi and press Enter to continue testing offline mode...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        superAdmin.createBranch("9", "Main Branch2", "City A", "Address A", "1234567890");
        superAdmin.addBranchManager("1","hiuh","ss@gmail.com","5440020637191","545465","15164811");



    }
}
