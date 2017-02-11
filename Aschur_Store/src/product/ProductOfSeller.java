package product;

/**
 * Created by Aschur on 14.10.2016.
 */
public class ProductOfSeller extends Product {

    private String fullName;

    public ProductOfSeller(String name, String barcode, double price) {
        super(name, barcode, price);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
