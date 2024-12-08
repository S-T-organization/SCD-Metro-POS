package backend;

import Controller.ReportsController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BackendMain {
    public static void main(String[] args) throws InterruptedException, SQLException {
        // Initialize the connection and monitoring

        Connection conn = DBConnection.getConnection();
        CheckConnectionOfInternet check = new CheckConnectionOfInternet();
        check.monitorInternet();
        BranchManager manager = new BranchManager();
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
       //manager.addCashier("101","Saim","cym26@gmail.com","122200");
//       manager.addDataEntryOperator("101","Talha","tato@gmail,com","100000");

////        // Testing SuperAdmin functionalities
//          SuperAdmin superAdmin = new SuperAdmin();
////        System.out.println("\n=== Testing Branch Creation ===");
//        //System.out.println( superAdmin.createBranch("40", "Main Branch", "City A", "Address A", "1234567890"));
////        System.out.println(superAdmin.createBranch("40", "Branch1", "Lahore", "Paki", "1234567890"));
////
////        // Pause for user to turn off Wi-Fi
//        System.out.println("\nPlease turn off your Wi-Fi and press Enter to continue testing offline mode...");
//
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
//        DataEntryOperator dataEntryOperator = new DataEntryOperator();
//
//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();

//        dataEntryOperator.addVendor("Sharjeena", "54001230123", "0293924410", "LAHORE");


//        ReportsController reportsController = new ReportsController();
//
//// Fetch weekly total sales for all branches
//        double totalWeeklySales = reportsController.getTotalSalesByTime("weekly");
//        System.out.println("---------- Weekly Sales Report ----------");
//        System.out.printf("Total Weekly Sales (All Branches): %.2f%n", totalWeeklySales);
//
//// Fetch branch name by ID
//        String branchName = reportsController.GetBranchNameById("101");
//        if (branchName != null) {
//            // Fetch total sales for a specific branch (weekly)
//            double branchSales = reportsController.getTotalSalesByBranchAndTime("weekly", "101");
//            System.out.printf("%nTotal Weekly Sales for %s Branch: %.2f%n", branchName, branchSales);
//
//            // Fetch sales percentages for products in the branch
//            System.out.println("\nProduct Sales Percentages for " + branchName + " Branch:");
//            Map<String, Double> branchProductPercentages = reportsController.getProductSalesPercentages("weekly", "101");
//            for (Map.Entry<String, Double> entry : branchProductPercentages.entrySet()) {
//                System.out.printf("Product: %s, Percentage: %.2f%%%n", entry.getKey(), entry.getValue());
//            }
//        } else {
//            System.out.println("\nBranch ID '101' not found.");
//        }
//
//// Fetch sales percentages for all branches
//        System.out.println("\n---------- Product Sales Percentages (All Branches) ----------");
//        Map<String, Double> allBranchProductPercentages = reportsController.getProductSalesPercentages("weekly", null);
//        for (Map.Entry<String, Double> entry : allBranchProductPercentages.entrySet()) {
//            System.out.printf("Product: %s, Percentage: %.2f%%%n", entry.getKey(), entry.getValue());
//        }
//
//

    }
}

