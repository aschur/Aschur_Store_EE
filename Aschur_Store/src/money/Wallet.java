package money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Aschur on 14.11.2016.
 */
public class Wallet {

    private static ArrayList<Double> getWallet(double cash){

        ArrayList<Double> wallet = new ArrayList<Double>();
        Random rand = new Random();

        int banknots[] = {50, 100, 500, 1000, 5000};

        int count = (int) cash;
        double fractionalPart = cash - count;
        while (count >= 50){

            int i = 0;
            if (count >= 5000)
                i = 5;
            else if (count < 5000 && count >= 1000)
                i = 4;
            else if (count < 1000 && count >= 500)
                i = 3;
            else if (count < 500 && count >= 100)
                i = 2;
            else
                i = 1;

            int banknote = banknots[rand.nextInt(i)];
            wallet.add((double) banknote);

            count -= banknote;

        }

        double remainder = roundDouble((double) count + fractionalPart);

        if (remainder > 0)
            wallet.add(remainder);

        Collections.sort(wallet, Collections.reverseOrder());

        return wallet;


    }

    public static double getMoneyForPayment(double cash, double payment){

        double mon = 0;

        if (cash < payment)
            return 0;

        ArrayList<Double> wallet = getWallet(cash);

        for (Double banknote :
                wallet) {

            if (mon >= payment){
                break;
            }

            mon += banknote;

        }

        return mon;

    }

    public static double roundDouble(double sum){

        return new BigDecimal((double) sum).setScale(2, RoundingMode.UP).doubleValue();

    }

}
