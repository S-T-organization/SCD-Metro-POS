// File: backend/Product.java
package backend;

public class Product {
    private String branchCode;
    private String productName;
    private String originalPrice;
    private String salesPrice;
    private String productDescription;
    private String quantity;
    private String vendorId;

    public Product(String branchCode, String productName, String originalPrice, String salesPrice,
                   String productDescription, String quantity, String vendorId) {
        this.branchCode = branchCode;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.salesPrice = salesPrice;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.vendorId = vendorId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }
}
