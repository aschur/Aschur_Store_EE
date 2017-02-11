package management;

import equipment.BarcodeReader;
import equipment.CashRegister;
import person.*;
import gui.TellerWorkingPlace;
import product.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Aschur on 22.11.2016.
 */
public class Manager {

    public static void main(String[] args) throws Exception{

        String[] settings;
        if (args.length > 0)
            settings = args;
        else
            settings = SettingsLoader.getSettings();

        if (settings.length != 6){
            System.out.println("the number of input parameters must equal 6");
            return;
        }

        String fileNameProcurementPlan = settings[0];
        String fileNameProducts = settings[1];
        int countConsumers = Integer.parseInt(settings[2]);
        int maxCountEnterStore = Integer.parseInt(settings[3]);
        int timeWorkStore = Integer.parseInt(settings[4]);
        String usageMode = settings[5];

        ArrayBlockingQueue<Consumer> waitingStoreConsumers = new ArrayBlockingQueue<Consumer>(countConsumers);
        final ConsumersQueueTeller standingLineConsumers = new ConsumersQueueTeller(countConsumers);

        ProductsGeneratorFromTextFile.readAllString(fileNameProducts);
        RandomGeneratorProcurementPlan.readAllStringFromFile(fileNameProcurementPlan);

        ShelfOfConsumer shelfOfConsumer = new ShelfOfConsumer();
        ProductsDataBaseOfSeller productsDataBaseOfSeller = new ProductsDataBaseOfSeller();
        DelayProducts delayProducts = new DelayProducts();

        while (ProductsGeneratorFromTextFile.hasNext()){
            TupleOfProducts tupleOfProducts = ProductsGeneratorFromTextFile.next();
            shelfOfConsumer.put(tupleOfProducts.productOfConsumer, tupleOfProducts.productOfConsumerCount);
            productsDataBaseOfSeller.put(tupleOfProducts.productOfSeller, tupleOfProducts.productOfSellerCount);
        }


        if (shelfOfConsumer.isEmpty() || productsDataBaseOfSeller.isEmpty()){

            System.out.println("failed to load products from a file");
            return;

        }

        ArrayList<Consumer> consumerArrayList = new ArrayList<Consumer>();

        for (int i = 0; i < countConsumers ; i++) {
            Consumer consumer = new Consumer(standingLineConsumers, shelfOfConsumer);
            Thread consumerThread = new Thread(consumer, consumer.getName());
            waitingStoreConsumers.add(consumer);

            consumerArrayList.add(consumer);

            consumerThread.start();
        }


        final BarcodeReader barcodeReader = new BarcodeReader();

        ArrayList<Observable> observables = new ArrayList<Observable>();
        observables.add(barcodeReader);

        final CashRegister cashRegister = new CashRegister(observables, productsDataBaseOfSeller, delayProducts);



        Teller teller = null;
        Thread tellerThread = null;

        if (usageMode.equals("handMode")){


            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    new TellerWorkingPlace("TellerWorkingPlace",
                            standingLineConsumers, cashRegister, barcodeReader);

                }

            });



        }else{

            teller = new Teller(standingLineConsumers, cashRegister, barcodeReader);
            tellerThread = new Thread(teller, teller.getName());

        }



        Date beginWork = new Date();

        if (usageMode.equals("automaticMode")){

            teller.setState(StatesTeller.WORKS);
            tellerThread.start();

        }



        int countcon = countConsumers;
        while (countcon != 0){

            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                stopWork(teller, standingLineConsumers, waitingStoreConsumers);
                return;
            }

            int maxCountEnterStore1 = StrictMath.min(countcon, maxCountEnterStore);

            Random rand = new Random();
            int countEnterStore = rand.nextInt(maxCountEnterStore1 + 1);

            int countEnterStoreGood = 0;

            for (int i = 0; i < countEnterStore; i++) {
                Consumer consumer = waitingStoreConsumers.poll();
                if (consumer != null){

                    consumer.setState(StatesConsumer.TAKE);
                    countEnterStoreGood++;

                }

            }

            countcon -= countEnterStoreGood;



        }



        while (true){

            Date currentDate = new Date();
            long elapsedTime = currentDate.getTime() - beginWork.getTime();
            if (elapsedTime < (long) timeWorkStore){

                try {
                    Thread.sleep(1000);
                    continue;
                }catch (InterruptedException e){
                    stopWork(teller, standingLineConsumers, waitingStoreConsumers);
                    return;
                }

            }else {

                stopWork(teller, standingLineConsumers, waitingStoreConsumers);
                return;
            }

        }

    }


    public static void stopWork(Teller teller, ConsumersQueueTeller standingLineConsumers,
                                            ArrayBlockingQueue<Consumer> waitingStoreConsumers){

        if (teller != null){
            teller.setState(StatesTeller.RESTING);
        }

        while (!standingLineConsumers.isEmpty()){

            Consumer consumer = standingLineConsumers.poll();
            if (consumer == null)
                continue;

            consumer.setState(StatesConsumer.GO_HOME);

        }

        while (!waitingStoreConsumers.isEmpty()){

            Consumer consumer = waitingStoreConsumers.poll();
            if (consumer == null)
                continue;

            consumer.setState(StatesConsumer.GO_HOME);

        }

    }

}
