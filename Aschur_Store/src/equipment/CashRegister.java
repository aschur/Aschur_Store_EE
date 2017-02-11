package equipment;

import money.Wallet;
import product.*;

import java.util.*;

/**
 * Created by Aschur on 28.10.2016.
 */
public class CashRegister extends Observable implements Equipment, Observer{

    private static int count;
    private int id;
    private double paymentAmount;
    private double receivedAmount;
    private double cash;
    private int checkNumber;

    private List<Observable> observableList = new ArrayList<Observable>();
    private ProductRegister productDataBase;
    private ProductRegister delaysProducts;

    private ProductSalesDataTable productSalesDataTable = new ProductSalesDataTable();

    public CashRegister(List<Observable> observableList, ProductRegister productDataBase, ProductRegister delaysProducts) {

        id = count++;

        this.observableList = observableList;
        for (Observable obs :
                observableList) {
            obs.addObserver(this);
        }

        this.productDataBase = productDataBase;
        this.delaysProducts = delaysProducts;

    }

    public void maintenance() {
        System.out.println("maintenance made!");
    }

    public int getID() {
        return id;
    }

    public void update(Observable o, Object arg) {

        if (o instanceof BarcodeReader){

            String barcode = ((BarcodeReader) o).getBarcode();
            Product product = productDataBase.getProductByBarcode(barcode);
            if (product == null) {
                System.out.println("this product barcode " + barcode + " was not found in the database");
                return;
            }

            productSalesDataTable.addProduct(product, 1);

            paymentAmount += product.getPrice();
            paymentAmount = Wallet.roundDouble(paymentAmount);

            setChanged();
            notifyObservers("productSalesDataTableChange");


        }

    }



    public double getPaymentAmount(){
        return paymentAmount;
    }

    public void enterNewCheck(){

        productSalesDataTable.clear();

        paymentAmount = 0;
        receivedAmount = 0;

        checkNumber++;

        setChanged();
        notifyObservers("enterNewCheck");

    }

    public void setReceivedAmount(double receivedAmount){
        this.receivedAmount = receivedAmount;
    }

    public double calculateChange(){

        return Wallet.roundDouble(receivedAmount - paymentAmount);

    }

    public String breakCheck(){

        reduceCountProducts();

        StringBuffer sb = new StringBuffer();

        sb.append("Check #"+checkNumber+":");
        sb.append("\n");

        for (Object o : productSalesDataTable){

            ProductSalesData productSalesData = (ProductSalesData) o;
            Product product = productSalesData.getProduct();
            int count = productSalesData.getCount();

            String name = product.getName();
            double price = productSalesData.getPrice();

            sb.append(name + " " + count + " " + price);
            sb.append("\n");

        }

        sb.append("Total: " + Wallet.roundDouble(paymentAmount));
        sb.append("\n");
        sb.append("Received: " + Wallet.roundDouble(receivedAmount));
        sb.append("\n");
        sb.append("Change: " + Wallet.roundDouble(calculateChange()));

        return sb.toString();

    }

    public void reduceCountProducts(){

        for (Object o : productSalesDataTable){

            ProductSalesData productSalesData = (ProductSalesData) o;
            Product product = productSalesData.getProduct();
            int count = productSalesData.getCount();

            productDataBase.reduceProduct(product, count);
        }

    }

    public void setCash() {
        this.cash += getPaymentAmount();
    }

    public ProductSalesDataTable getProductSalesDataTable() {
        return productSalesDataTable;
    }

    public Map<Product, Integer> getDelayProducts(Map<Product, Integer> productsConsumer){

        Map<Product, Integer> delayProducts = new HashMap<Product, Integer>();

        for (Map.Entry<Product, Integer> entry :
                productsConsumer.entrySet()) {

            Product productConsumer = entry.getKey();
            int countConsumer = entry.getValue();

            String barcode = productConsumer.getBarcode();

            boolean hasProductSeller = false;

            for (ProductSalesData row :
                    productSalesDataTable) {

                Product productSeller = row.getProduct();
                int countSeller = row.getCount();

                if (!barcode.equals(productSeller.getBarcode())){
                    continue;
                }

                hasProductSeller = true;

                int countDelay = countConsumer - countSeller;

                if (countDelay > 0){
                    delayProducts.put(productConsumer, countDelay);
                }

                break;

            }

            if (!hasProductSeller){
                delayProducts.put(productConsumer, countConsumer);
            }

        }

        return delayProducts;

    }

    public void delayProducts(Map<Product, Integer> delayProducts){

        for (Map.Entry<Product, Integer> entry :
                delayProducts.entrySet()) {

            this.delaysProducts.put(entry.getKey(), entry.getValue());


        }

    }

    public Map<Product, Integer> getProductsConsumer(Map<Product, Integer> mapProductsConsumer){


//        System.out.println(productSalesDataTable);

        Map<Product, Integer> productsConsumer = new HashMap<Product, Integer>();

        for (ProductSalesData row :
                productSalesDataTable) {

            Product productSeller = row.getProduct();
            int countSeller = row.getCount();

            String barcodeSeller = productSeller.getBarcode();

            for (Map.Entry<Product, Integer> entry :
                    mapProductsConsumer.entrySet()) {

                Product productConsumer = entry.getKey();
                int countConsumer = entry.getValue();

                if (!barcodeSeller.equals(productConsumer.getBarcode())){
                    continue;
                }


                productsConsumer.put(productConsumer, countSeller);


            }
        }

        return productsConsumer;
    }
}
