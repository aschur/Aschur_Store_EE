package person;

import equipment.BarcodeReader;
import equipment.CashRegister;
import product.Product;

import java.util.Map;

import static person.StatesTeller.*;

/**
 * Created by Aschur on 21.11.2016.
 */
public class Teller extends Person{

    private ConsumersQueueTeller consumers;
    private CashRegister cashRegister;
    private BarcodeReader barcodeReader;

    private StatesTeller state = RESTING;

    public Teller(ConsumersQueueTeller consumers, CashRegister cashRegister, BarcodeReader barcodeReader) {
        this.consumers = consumers;
        this.cashRegister = cashRegister;
        this.barcodeReader = barcodeReader;
    }

    public void setState(StatesTeller state) {
        this.state = state;
    }

    @Override
    public void run() {

        while (state.equals(WORKS)){

            Consumer consumer = consumers.poll();
            if (consumer == null){

                try {
                    Thread.sleep(1000);
                    continue;
                }catch (InterruptedException e){

                    System.out.println(e.fillInStackTrace() + "\n" + state.toString() + "\n" + getName());

                    state = RESTING;
                    break;
                }

            }

            cashRegister.enterNewCheck();

            Map<Product, Integer> mapProductsConsumer = consumer.getMapProducts();

            useBarcodeReader(mapProductsConsumer);

            double payment = cashRegister.getPaymentAmount();

            if (payment == 0){
                consumer.setState(StatesConsumer.GO_HOME);
                System.out.println("payment = 0" + " " + consumer.getName());
                continue;
            }

            boolean canGiveMoney = consumer.makeMoneyRequest(payment);
            if (!canGiveMoney)
                continue;

            double money = consumer.giveMoney();
            cashRegister.setReceivedAmount(money);
            double change = cashRegister.calculateChange();
            consumer.takeChange(change);

            String check = cashRegister.breakCheck();
            consumer.takeCheck(check);

            Map<Product, Integer> delayProductsConsmer = cashRegister.getDelayProducts(mapProductsConsumer);
            if (!delayProductsConsmer.isEmpty()){
                cashRegister.delayProducts(delayProductsConsmer);
            }

            Map<Product, Integer> productsConsumer = cashRegister.getProductsConsumer(mapProductsConsumer);
            consumer.setPaidProducts(productsConsumer);


            consumer.setState(StatesConsumer.GO_HOME);

            cashRegister.setCash();

        }

    }

    private void useBarcodeReader(Map<Product, Integer> mapProductsConsumer){

        for (Map.Entry<Product, Integer> entry :
                mapProductsConsumer.entrySet()) {

            Product product = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {

                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){

                    System.out.println(e.fillInStackTrace() + "\n" + state.toString() + "\n" + getName());

                    state = RESTING;
                    break;
                }

                barcodeReader.readBarcode(product);

            }

        }

    }

}
