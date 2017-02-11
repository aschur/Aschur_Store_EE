package product;

import money.Wallet;

/**
 * Created by Aschur on 13.12.2016.
 */
public class ProductSalesData {

    private Product product;
    private int count;
    private double price;
    private double total;

    public ProductSalesData(Product product, int count) {

        this.product = product;
        this.count = count;

        double price = Wallet.roundDouble(product.getPrice());

        this.price = price;
        this.total = Wallet.roundDouble(price * count);

    }

    public Product getProduct() {
        return product;
    }

    public int getCount() {
        return count;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }

    public void increment(int count){
        this.count += count;
        this.total = Wallet.roundDouble(this.price * this.count);
    }

    public void setCount(int count){
        this.count = count;
        this.total = Wallet.roundDouble(this.price * this.count);
    }

    @Override
    public String toString() {
        return "ProductSalesData: " + "Product:" + getProduct() + ", Count: " + getCount() + ", Price: " + getPrice() + ", Total: " + getTotal();
    }
}
