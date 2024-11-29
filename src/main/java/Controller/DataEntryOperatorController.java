package Controller;

import backend.DataEntryOperator;
import backend.Product;

import java.util.List;

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

    // Add a vendor
    public int addVendor(String vendorName, String vendorCnic, String vendorPhone, String vendorAddress) {
        return dataEntryOperator.addVendor(vendorName, vendorCnic, vendorPhone, vendorAddress);
    }

    // Add multiple products
    public int addProducts(List<Product> products) {
        return dataEntryOperator.addProducts(products);
    }

    // Get all vendor names
    public String[] getAllVendors() {
        return dataEntryOperator.getAllVendors();
    }


    public String getVendorIdByVendorName(String vendorName) {
        return dataEntryOperator.getVendorIdByVendorName(vendorName);
    }
}
