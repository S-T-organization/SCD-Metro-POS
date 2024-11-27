package Controller;

import backend.DataEntryOperator;

public class DataEntryOperatorController {
    private DataEntryOperator dataEntryOperator;

    public DataEntryOperatorController() {
        this.dataEntryOperator = new DataEntryOperator();
    }

    // Data Entry Operator login
    public int login(String email, String password, String branchCode) {
        return dataEntryOperator.login(email, password, branchCode);
    }

    // Get all branch names
    public String[] getAllBranchNames() {
        return dataEntryOperator.getAllBranchNames();
    }

    // Get branch code by branch name
    public String getBranchCodeByName(String branchName) {
        return dataEntryOperator.getBranchCodeByName(branchName);
    }
}