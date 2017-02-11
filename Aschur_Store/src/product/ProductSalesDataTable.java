package product;

import java.util.ArrayList;

/**
 * Created by Aschur on 13.12.2016.
 */
public class ProductSalesDataTable extends ArrayList<ProductSalesData>{

    public void addProduct(Product product, int count){

        boolean hasProduct = false;
        for (ProductSalesData psd :
                this) {
            Product prod = psd.getProduct();
            if (prod.equals(product)){
                psd.increment(count);
                hasProduct = true;
                break;
            }

        }

        if (!hasProduct){
            ProductSalesData psd = new ProductSalesData(product, count);
            this.add(psd);
        }

    }

}
