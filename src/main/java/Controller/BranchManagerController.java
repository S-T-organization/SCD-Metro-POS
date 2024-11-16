package Controller;

import backend.BranchManager;

public class BranchManagerController {

    private BranchManager branchManager;

    public BranchManagerController() {

        this.branchManager = new BranchManager();
    }


    public boolean addCashier(String branchCode, String name, String email, String salary) {
        // Call the addCashier method of BranchManager class
        return branchManager.addCashier(branchCode, name, email, salary);
    }


    public boolean addDataEntryOperator(String branchCode, String name, String email, String salary) {
        // Call the addDataEntryOperator method of BranchManager class
        return branchManager.addDataEntryOperator(branchCode, name, email, salary);
    }


    public boolean changePassword(String email, String oldPassword, String newPassword) {

        return branchManager.changePassword(email, oldPassword, newPassword);
    }


}
