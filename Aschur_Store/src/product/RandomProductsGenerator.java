package product;

import java.util.Random;

/**
 * Created by Aschur on 15.10.2016.
 */
public class RandomProductsGenerator {

    private int count;
    private int number;

    public RandomProductsGenerator(int count) {
        this.count = count;
    }

    public boolean hasNext(){
        return count > 0;
    }

    public TupleOfProducts next(){

        TupleOfProducts top = new TupleOfProducts();
        String barcode = BarcodeGenerator.generate();
        String name = "Product" + ++number;
        Random random = new Random();
        double price = random.nextInt(1500);
        int productsCount = random.nextInt(100);

        ProductOfSeller productOfSeller = new ProductOfSeller(name, barcode, price);
        ProductOfConsumer productOfConsumer = new ProductOfConsumer(name, barcode, price);

        top.productOfConsumer = productOfConsumer;
        top.productOfSeller = productOfSeller;
        top.productOfConsumerCount = productsCount;
        top.productOfSellerCount = productsCount;

        count--;


        return top;


    }


}
