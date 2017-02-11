package person;

import money.Wallet;
import product.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

import static person.StatesConsumer.*;

/**
 * Created by Aschur on 03.11.2016.
 */
public class Consumer extends Person{

    private ConsumersQueueTeller consumers;
    private Map<String, Integer> procurementPlan;
    private Map<Product, Integer> mapProducts = new HashMap<Product, Integer>();
    private ShelfOfConsumer shelfOfConsumer;
    private StatesConsumer state = WAITING_STORE;
    private boolean canGiveMoney = false;
    private double cash = 0;
    private double payment = 0;


    public Consumer(ConsumersQueueTeller consumers, ShelfOfConsumer shelfOfConsumer) {

        this.consumers = consumers;
        this.shelfOfConsumer = shelfOfConsumer;
        procurementPlan = RandomGeneratorProcurementPlan.getPlan();

        Random random = new Random();
        cash = Wallet.roundDouble(random.nextInt(5000));

    }

    public void setState(StatesConsumer state) {
        this.state = state;
    }

    @Override
    public void run() {

        while (state != GO_HOME){

            if (state == WAITING_STORE){

                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    System.out.println(e.fillInStackTrace() + "\n" + state.toString() + "\n" + getName());
                    state = GO_HOME;
                }


            }else if (state == TAKE){

                takeProducts();

                if (mapProducts.isEmpty()){

                    System.out.println("no products for" + " " + getName());
                    state = GO_HOME;

                }else {

                    consumers.add(this);
                    state = STANDING_IN_LINE;

                }


            }else if (state == STANDING_IN_LINE){

                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    System.out.println(e.fillInStackTrace() + "\n" + state.toString() + "\n" + getName());
                    state = GO_HOME;

                }

            }else if (state == SERVED){

                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    System.out.println(e.fillInStackTrace() + "\n" + state.toString() + "\n" + getName());
                    state = GO_HOME;
                }


            }else if (state == GO_HOME){

                break;

            }


        }




    }

    private void takeProducts(){

        for (Map.Entry<String, Integer> entry: procurementPlan.entrySet()){

            String name = entry.getKey();
            int count = entry.getValue();

            ArrayList<Product> products = shelfOfConsumer.getProductsByName(name);

            int takeAmount = count;

            while (takeAmount != 0){

                for (Product prod :
                        products) {


                    int countProduct = shelfOfConsumer.getCountProduct(prod);
                    if (countProduct == 0)
                        continue;

                    int takeAmountLocal = StrictMath.min(takeAmount, countProduct);

                    mapProducts.put(prod, takeAmountLocal);
                    shelfOfConsumer.reduceProduct(prod, takeAmountLocal);

                    takeAmount = takeAmount - takeAmountLocal;


                }

                break;

            }
        }

    }

    public Map<Product, Integer> getMapProducts(){

//        System.out.println(getName() +":"+ mapProducts);

        Map<Product, Integer> mapProductsCopy = new HashMap<Product, Integer>();
        mapProductsCopy.putAll(mapProducts);

        mapProducts = null;

        return mapProductsCopy;

    }

    public boolean makeMoneyRequest(double moneyCount){

        payment = moneyCount;
        boolean canGiveMoneyVar = checkAmountMoney(moneyCount);
        if (canGiveMoneyVar){
            canGiveMoney = true;
        }else{
            canGiveMoney = false;

            System.out.println(getName() + " can t give money");

            state = GO_HOME;

        }

        return canGiveMoney;

    }

    private boolean checkAmountMoney(double moneyCount){

        if (moneyCount <= cash)
            return true;
        else
            return false;

    }

    public double giveMoney(){

        double money = 0;
        if (canGiveMoney){

            money = Wallet.getMoneyForPayment(cash, payment);

        }

        return money;

    }

    public void takeChange(double change){

    }

    public void takeCheck(String check){

//        System.out.println(getName());
        System.out.println(check);

    }


    public void setPaidProducts(Map<Product, Integer> products){

        mapProducts = products;

//        System.out.println(getName() + ":" + mapProducts);

    }
}


