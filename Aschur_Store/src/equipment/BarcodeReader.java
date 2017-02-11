package equipment;
import product.Product;

import java.util.Observable;

/**
 * Created by Aschur on 28.10.2016.
 */
public class BarcodeReader extends Observable implements Equipment{

    private static int count;
    private int id;
    private String barcode;

    public BarcodeReader() {
        id = count++;
    }

    public void maintenance() {
        System.out.println("maintenance made!");
    }

    public int getID() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void readBarcode(Product product){
        barcode = product.getBarcode();
        setChanged();
        notifyObservers();
    }

}
