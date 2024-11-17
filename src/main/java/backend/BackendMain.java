package backend;
import Controller.SuperAdminController;

public class BackendMain {
    public static void main(String[] args) throws InterruptedException {
        if (!DBConnection.isConnectionOpen()) {
            System.out.println("Connection Open");
            new DBConnection(); // Reinitialize connection
        }

        SuperAdmin superAdmin = new SuperAdmin();
        superAdmin.createBranch("3","Town","Lahore","Johar Town Lahore Pakistan","04212345678");
//        System.out.println(superAdmin.login("admin", "admin"));
        if (!DBConnection.isConnectionOpen()) {
            System.out.println("Connection is closed");
            Thread.sleep(1000);

        }
        else {
            System.out.println("Connection is Opened");
        }
        System.out.println(superAdmin.addBranchManager("3","tALHA",
                "cym73gmail.com","544037211","130000","03332306480"));


//        BranchManager branchManager = new BranchManager();
        //branchManager.changePassword("cym786@gmail.com","123","myPassword");
        //branchManager.addCashier("1001","Tato","tato@gmail.com","10000");
        //branchManager.addDataEntryOperator("1001","Zainab Iqbal","Zain@gmail.com","80048");
//        SuperAdminController superAdminController = new SuperAdminController();
//        System.out.println("Testing: Fetching all branch names from the database...");
//        superAdminController.printAllBranchNames();
    }
}