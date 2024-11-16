package backend;

public class BackendMain {
    public static void main(String[] args) {
        new DBConnection();
        // SuperAdmin superAdmin = new SuperAdmin();
//        System.out.println(superAdmin.login("admin", "admin"));
//        System.out.println(superAdmin.addBranchManager("1001","Saim Imran",
//                "cym786@gmail.com","5440070637191","130000","03332306480"));

        BranchManager branchManager = new BranchManager();
        //branchManager.changePassword("cym786@gmail.com","123","myPassword");
        //branchManager.addCashier("1001","Tato","tato@gmail.com","10000");
        //branchManager.addDataEntryOperator("1001","Zainab Iqbal","Zain@gmail.com","80048");

        }
}
