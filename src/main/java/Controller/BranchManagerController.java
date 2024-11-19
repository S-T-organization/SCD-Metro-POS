package Controller;

import backend.BranchManager;

public class BranchManagerController {

    private BranchManager branchManager;

    public BranchManagerController() {

        this.branchManager = new BranchManager();
    }


    public int addCashier(String branchCode, String name, String email, String salary) {
        // Call the addCashier method of BranchManager class
        return branchManager.addCashier(branchCode, name, email, salary);
    }


    public int addDataEntryOperator(String branchCode, String name, String email, String salary) {
        return branchManager.addDataEntryOperator(branchCode, name, email, salary);
    }


    public int changePassword(String email, String oldPassword, String newPassword) {
        return branchManager.changePassword(email, oldPassword, newPassword);
    }

    public int login(String email, String password,String BranchCode) {
        return branchManager.login(email, password, BranchCode);
    }

    public String[] getAllBranchNames() {
        return branchManager.getAllBranchNames();
    }

    public String getBranchCodeByName(String branchName) {
        return branchManager.getBranchCodeByName(branchName);
    }
}
