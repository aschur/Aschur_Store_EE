package product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aschur on 15.10.2016.
 */
public abstract class ProductRegister{

    private volatile Map<Product, Integer> map = new HashMap<Product, Integer>();

    public synchronized void put(Product product, int count){

        Integer i = map.get(product);
        if (i == null)
            map.put(product, count);
        else
            map.put(product, i + count);


    }

    @Override
    public String toString() {
        return "ProductRegister" + map;
    }

    public synchronized Product getProductByBarcode(String barcode){

        Product product = null;

        for (Map.Entry<Product, Integer> entry:
            map.entrySet()){

            Product prod = entry.getKey();
            if (prod.getBarcode().equals(barcode)){
                product = prod;
                break;
            }


        }

        return product;

    }

    public synchronized Product getProductByName(String name){

        Product product = null;

        for (Map.Entry<Product, Integer> entry:
                map.entrySet()){

            Product prod = entry.getKey();
            if (prod.getName().equals(name)){
                product = prod;
                break;
            }



        }

        return product;

    }

    public synchronized ArrayList<Product> getProductsByName(String name){

        ArrayList<Product> products = new ArrayList<Product>();
        for (Map.Entry<Product, Integer> entry:
                map.entrySet()){

            Product prod = entry.getKey();
            if (prod.getName().equals(name)){
                products.add(prod);
            }

        }

        return products;
    }

    public synchronized void reduceProduct(Product product, int count){
        if (count == 0)
            return;

        if (map.containsKey(product)){
            int oldCount = map.get(product);
            map.put(product, oldCount - count);
        }else {
            map.put(product, -count);
        }
    }

    public synchronized int getCountProduct(Product product){

        int countProduct = 0;

        if (map.containsKey(product))
            countProduct = map.get(product);

        return countProduct;

    }

    public synchronized boolean isEmpty(){
        return map.isEmpty();
    }

}
