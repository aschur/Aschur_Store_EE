package product;

import java.util.Random;

/**
 * Created by Aschur on 14.10.2016.
 */
public class BarcodeGenerator {

    static String generate(){

        Random random = new Random();
        int barcode = random.nextInt(1000000);

        return Integer.toString(barcode);

    }

}
