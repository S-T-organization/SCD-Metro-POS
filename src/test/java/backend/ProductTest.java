package backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductConstructorAndGetters() {
        String branchCode = "BR001";
        String productName = "Laptop";
        String originalPrice = "1500.00";
        String salesPrice = "1300.00";
        String productDescription = "High-end gaming laptop";
        String quantity = "50";
        String vendorId = "V001";

        Product product = new Product(branchCode, productName, originalPrice, salesPrice, productDescription, quantity, vendorId);

        assertEquals(branchCode, product.getBranchCode());
        assertEquals(productName, product.getProductName());
        assertEquals(originalPrice, product.getOriginalPrice());
        assertEquals(salesPrice, product.getSalesPrice());
        assertEquals(productDescription, product.getProductDescription());
        assertEquals(quantity, product.getQuantity());
        assertEquals(vendorId, product.getVendorId());
    }

    @Test
    void testSetters() {
        Product product = new Product("", "", "", "", "", "", "");

        product.setBranchCode("BR002");
        product.setProductName("Smartphone");
        product.setOriginalPrice("800.00");
        product.setSalesPrice("700.00");
        product.setProductDescription("Latest model smartphone");
        product.setQuantity("100");
        product.setVendorId("V002");

        assertEquals("BR002", product.getBranchCode());
        assertEquals("Smartphone", product.getProductName());
        assertEquals("800.00", product.getOriginalPrice());
        assertEquals("700.00", product.getSalesPrice());
        assertEquals("Latest model smartphone", product.getProductDescription());
        assertEquals("100", product.getQuantity());
        assertEquals("V002", product.getVendorId());
    }

    @Test
    void testToString() {
        Product product = new Product(
                "BR003",
                "Tablet",
                "600.00",
                "550.00",
                "Lightweight and portable",
                "30",
                "V003"
        );

        String expectedString = "Product [branchCode=BR003, productName=Tablet, originalPrice=600.00, salesPrice=550.00, productDescription=Lightweight and portable, quantity=30, vendorId=V003]";
        assertEquals(expectedString, product.toString());
    }

    @Test
    void testEmptyConstructor() {
        Product product = new Product("", "", "", "", "", "", "");

        assertNotNull(product);
        assertEquals("", product.getBranchCode());
        assertEquals("", product.getProductName());
        assertEquals("", product.getOriginalPrice());
        assertEquals("", product.getSalesPrice());
        assertEquals("", product.getProductDescription());
        assertEquals("", product.getQuantity());
        assertEquals("", product.getVendorId());
    }
}
