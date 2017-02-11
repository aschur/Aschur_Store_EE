package product;

import management.SettingsLoader;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aschur on 16.10.2016.
 */
public class ProductsGeneratorFromTextFile {

    private static List<String> list;
    private static int size = 0;
    private static int index = 0;

    public static void readAllString(String pathName) {

        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        try {

            ClassLoader myCL = ProductsGeneratorFromTextFile.class.getClassLoader();
            inputStream = myCL.getResourceAsStream(pathName);

            try {

                int c;
                while((c = inputStream.read()) != -1){

                    String s = Character.toString((char) c);
                    if (s.equals("\n") || s.equals("\r") ){
                        continue;
                    }


                    sb.append(Character.toString((char) c));

                }

            }finally {


                inputStream.close();}


        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        String allString = sb.toString().trim();

        list = Arrays.asList(allString.split(";"));
        size = list.size();
    }

    public static TupleOfProducts next(){

        TupleOfProducts top = null;

        String[] infoProduct = list.get(index).split(",");
        if (infoProduct.length != 4)
            System.out.println("error in reading file, index "+index);
        else
        {

            String name = infoProduct[0].trim();
            String barcode = infoProduct[1].trim();
            double price = Double.valueOf(infoProduct[2].trim());
            int productsCount = Integer.valueOf(infoProduct[3].trim());

            ProductOfSeller productOfSeller = new ProductOfSeller(name, barcode, price);
            ProductOfConsumer productOfConsumer = new ProductOfConsumer(name, barcode, price);


            top = new TupleOfProducts();

            top.productOfConsumer = productOfConsumer;
            top.productOfSeller = productOfSeller;
            top.productOfConsumerCount = productsCount;
            top.productOfSellerCount = productsCount;



        }

        index++;

        return top;

    }

    public static boolean hasNext(){

        boolean hasNext = (index < size);
        return hasNext;

    }

}
