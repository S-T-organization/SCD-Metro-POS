package Controller;

import backend.SuperAdmin;

public class SuperAdminController {
    private SuperAdmin superAdmin;

    public SuperAdminController() {
        this.superAdmin = new SuperAdmin();
    }

    public boolean login(String inputUsername, String inputPassword) {
        return superAdmin.login(inputUsername, inputPassword);
    }

    public boolean createBranch(String branchCode, String name, String city, String address, String phone) {
        return superAdmin.createBranch(branchCode, name, city, address, phone);
    }

    public boolean addBranchManager(String branchCode, String name, String email, String cnic, String salary, String phoneNumber) {
        return superAdmin.addBranchManager(branchCode, name, email, cnic, salary, phoneNumber);
    }

    public String[] getAllBranchNames() {
        return superAdmin.getAllBranchNames(); // Fetch branch names via SuperAdmin
    }

    public String getBranchCodeByName(String branchName) {
        return superAdmin.getBranchCodeByName(branchName); // Fetch branch code via SuperAdmin
    }
}
