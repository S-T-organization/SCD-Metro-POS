package backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BackendMain {
    public static void main(String[] args) throws InterruptedException, SQLException {
        // Initialize the connection and monitoring

        Connection conn = DBConnection.getConnection();
        CheckConnectionOfInternet check = new CheckConnectionOfInternet();
        check.monitorInternet();
//       BranchManager manager = new BranchManager();
//       manager.addCashier("101","Saim","cym786@gmail.com","122200");
//       manager.addDataEntryOperator("101","Talha","tato@gmail,com","100000");

////        // Testing SuperAdmin functionalities
//          SuperAdmin superAdmin = new SuperAdmin();
////        System.out.println("\n=== Testing Branch Creation ===");
//        //System.out.println( superAdmin.createBranch("40", "Main Branch", "City A", "Address A", "1234567890"));
////        System.out.println(superAdmin.createBranch("40", "Branch1", "Lahore", "Paki", "1234567890"));
////
////        // Pause for user to turn off Wi-Fi
//        System.out.println("\nPlease turn off your Wi-Fi and press Enter to continue testing offline mode...");
//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();
////        System.out.println(  superAdmin.createBranch("0", "Main Branch2", "City A", "Address A", "1234567890"));
////
//         // System.out.println(superAdmin.addBranchManager("40","hiuh","@gmai.com","544031","545465","15164811"));
////
        //       DataEntryOperator data = new DataEntryOperator();
//        System.out.println(data.login("tato@gmail,com","123","101"));
//        String []names = data.getAllBranchNames();

        //data.addVendor("Shiekh sahab","544007815654","03332306480","Lahore");
//     data.addVendor("Adeel","544007885654","03332306481","Karachi");
        // data.addProduct("101","Sunslik","1200","Shampoooo","4","1");
//     String[] vendors=data.getAllVendors();
//     for(int i=0;i<vendors.length;i++){
//         System.out.println(vendors[i]);
//     }
//        System.out.println(data.getVendorIdByVendorName("Adeel"));
        DataEntryOperator dataEntryOperator = new DataEntryOperator();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

//        dataEntryOperator.addVendor("Sharjeena", "54001230123", "0293924410", "LAHORE");


    }
}
