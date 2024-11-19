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
        System.out.println( superAdmin.createBranch("40", "Main Branch", "City A", "Address A", "1234567890"));
        System.out.println(superAdmin.createBranch("40", "Branch1", "Lahore", "Paki", "1234567890"));

        // Pause for user to turn off Wi-Fi
        System.out.println("\nPlease turn off your Wi-Fi and press Enter to continue testing offline mode...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.println(  superAdmin.createBranch("0", "Main Branch2", "City A", "Address A", "1234567890"));

        System.out.println(superAdmin.addBranchManager("1","hiuh","ss@gmail.com","5440020637191","545465","15164811"));



    }
}
