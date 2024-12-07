package Controller;

import backend.Cashier;
import backend.Product;

import java.util.List;

public class CashierController {

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

    public Product getProductById(String productId) {
        return cashier.getProductById(productId);
    }
    public String getBranchNameByCode(String branchCode) {
        return cashier.getBranchNameByCode(branchCode);
    }

    public String removeProduct(List<String> namesAndQuantity,String branchCode) {
        return cashier.removeProduct(namesAndQuantity,branchCode);
    }
    public int ChangePasswordForCashier(String email, String newPassword) {
        return cashier.changePassword(email, newPassword);
    }
}
