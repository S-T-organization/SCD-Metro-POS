package Controller;

import backend.Cashier;

public class CashierController
{

    private Cashier cashier;

    public CashierController() {
        this.cashier = new Cashier();
    }

    // Cashier login
    public int login(String email, String password, String branchCode) {
        return cashier.login(email, password, branchCode);
    }

    // Get all branch names
    public String[] getAllBranchNames() {
        return cashier.getAllBranchNames();
    }

    // Get branch code by branch name
    public String getBranchCodeByName(String branchName) {
        return cashier.getBranchCodeByName(branchName);
    }
}
