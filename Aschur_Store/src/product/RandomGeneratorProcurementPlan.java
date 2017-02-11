package product;

import java.io.*;
import java.util.*;

/**
 * Created by Aschur on 16.10.2016.
 * example:
 * <Brot,
    Apple red,
    Banana,
    Fisch,
    Apple yellow,
    Coffee black>
 */
public class RandomGeneratorProcurementPlan {

    private static List<String> diffProoducts = new ArrayList<String>();
    private static int size = 0;

    public static void readAllStringFromFile(String pathName) {

        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();

        try {

            ClassLoader myCL = RandomGeneratorProcurementPlan.class.getClassLoader();
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
        List<String> list = Arrays.asList(allString.split(","));




        for (int i = 0; i < list.size(); i++) {

            String nameProduct = list.get(i);
            list.set(i, nameProduct.trim());

        }



        HashSet<String> set = new HashSet<String>();
        set.addAll(list);

        diffProoducts.addAll(set);
        size = diffProoducts.size();

    }

    public static boolean hasProduct(){

        return size > 0;

    }

    public static Map<String, Integer> getPlan(){

        Map<String, Integer> procurementPlan = new HashMap<String, Integer>();

        int maxNumberDifferentProducts = 10;
        int minNumberDifferentProducts = 1;
        int maxCountProduct = 4;
        int minCountProduct = 1;




        Random random = new Random();

        int numberDifferentProducts = random.nextInt(maxNumberDifferentProducts);
        if (numberDifferentProducts == 0)
            numberDifferentProducts = minNumberDifferentProducts;


        if (numberDifferentProducts > size)
            numberDifferentProducts = size;

        Set<Integer> indexSet = new HashSet<Integer>();
        while (procurementPlan.size() != numberDifferentProducts){

            boolean hasIndex = false;
            int index = 0;
            while (!hasIndex){
                index = random.nextInt(size);
                if (!indexSet.contains(index)){
                    hasIndex = true;
                }

            }

            indexSet.add(index);
            String productName = diffProoducts.get(index);
            int countProduct = random.nextInt(maxCountProduct);
            if (countProduct == 0)
                countProduct = minCountProduct;
            if (procurementPlan.containsKey(productName)){
                continue;
            }

            procurementPlan.put(productName, countProduct);




        }


        return procurementPlan;

    }





}
