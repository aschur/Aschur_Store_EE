package product;

/**
 * Created by Aschur on 14.10.2016.
 */
public abstract class Product {
    private String name;
    private String barcode;
    private double price;

    public Product(String name, String barcode, double price) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return  "{" +
                "name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", price=" + price +
                '}';
    }
}
