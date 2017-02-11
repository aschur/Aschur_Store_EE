package gui;

import equipment.BarcodeReader;
import equipment.CashRegister;
import person.Consumer;
import person.ConsumersQueueTeller;
import person.StatesConsumer;
import product.Product;
import product.ProductSalesData;
import product.ProductSalesDataTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Aschur on 07.12.2016.
 */
public class TellerWorkingPlace extends JFrame implements Observer {

    private ConsumersQueueTeller consumers;
    private CashRegister cashRegister;
    private BarcodeReader barcodeReader;

    private Consumer consumer;
    private ProductSalesDataTable productsConsumer = new ProductSalesDataTable();
    private Map<Product, Integer> mapProductsConsumer;

    StatesTellerWorkingPlace state = StatesTellerWorkingPlace.WAITING;

    private AbstractTableModel productsSaleTable;
    private AbstractTableModel consumersTable;
    private AbstractTableModel productsConsumerTable;

    private JTextField paymentAmountField = new JTextField("0.00", 10);
    private JTextField receivedAmountField = new JTextField("0.00", 10);
    private JTextField changeField = new JTextField("0.00", 10);

    private JButton enterNewCheckButton = new JButton("Enter new check");
    private ActionListener enterNewCheckAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            cashRegister.enterNewCheck();

            productsConsumer.clear();
            consumer = null;


            productsConsumerTable.fireTableDataChanged();

            Consumer cons = consumers.poll();
            if (cons == null){
                System.out.println("No consumers in queue");
                return;
            }

            consumer = cons;
            mapProductsConsumer = consumer.getMapProducts();
            for (Map.Entry<Product, Integer> entry:
                    mapProductsConsumer.entrySet()){


                Product product = entry.getKey();
                int count = entry.getValue();

                productsConsumer.addProduct(product, count);

            }

            productsConsumerTable.fireTableDataChanged();

            state = StatesTellerWorkingPlace.READING;
            customizeGUIItems();

        }
    };

    private JButton readBarcodeButton = new JButton("Read barcode");
    private ActionListener readBarcodeAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            if (productsConsumer.size() == 0){
                System.out.println("No products for scan");
                return;
            }

            ProductSalesData psd = productsConsumer.get(0);

            int count = psd.getCount();
            if (count > 0){

                Product product = psd.getProduct();
                barcodeReader.readBarcode(product);
                count--;
                psd.setCount(count);

            }

            if (count == 0){
                productsConsumer.remove(0);
            }

            productsConsumerTable.fireTableDataChanged();

        }

    };

    private JButton payButton = new JButton("PAY");
    private ActionListener payAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

            if (consumer == null){
                return;
            }

            double payment = cashRegister.getPaymentAmount();


            if (payment == 0){
                consumer.setState(StatesConsumer.GO_HOME);
                System.out.println("payment == 0" + " " + consumer.getName());
                return;
            }

            boolean canGiveMoney = consumer.makeMoneyRequest(payment);
            if (!canGiveMoney)
                return;

            double money = consumer.giveMoney();
            cashRegister.setReceivedAmount(money);
            receivedAmountField.setText(Double.toString(money));

            double change = cashRegister.calculateChange();
            changeField.setText(Double.toString(change));

            consumer.takeChange(change);

            state = StatesTellerWorkingPlace.PAID;
            customizeGUIItems();

        }

    };

    private JButton breakCheckButton = new JButton("Break check");
    private ActionListener breakCheckAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {

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

            state = StatesTellerWorkingPlace.WAITING;
            customizeGUIItems();

        }

    };



    public TellerWorkingPlace(String title, ConsumersQueueTeller consumers, CashRegister cashRegister,
                              BarcodeReader barcodeReader) {
        super(title);

        this.consumers = consumers;
        this.cashRegister = cashRegister;
        this.barcodeReader = barcodeReader;

        consumers.addObserver(this);
        cashRegister.addObserver(this);


        setLayout(new BorderLayout());

        JPanel jPanelNorth = addPanel(BorderLayout.NORTH);
        jPanelNorth.setBackground(Color.gray);

        JPanel jPanelSouth = addPanel(BorderLayout.SOUTH);
        JPanel jPanelEast = addPanel(BorderLayout.EAST);
        JPanel jPanelWest = addPanel(BorderLayout.WEST);
        JPanel jPanelCenter = addPanel(BorderLayout.CENTER);


        // NORTH
        addButton(enterNewCheckButton, enterNewCheckAL, "icons/1483579041_plus.png", jPanelNorth);

        consumersTable = new ConsumersTable(consumers);
        addTableToPanel(consumersTable, new Dimension(250, 150), jPanelNorth);

        productsConsumerTable = new ProductsConsumerTable(this);
        addTableToPanel(productsConsumerTable, new Dimension(250, 150), jPanelNorth);

        addButton(readBarcodeButton, readBarcodeAL, "icons/Barcode-icon.png", jPanelNorth);

        // CENTER
        productsSaleTable = new ProductsSaleTable(cashRegister);
        addTableToPanel(productsSaleTable, new Dimension(250, 150), jPanelCenter);

        // EAST
        JConsole jConsole = new JConsole();
        JScrollPane jscrlp = new JScrollPane(jConsole);
        jscrlp.setPreferredSize(new Dimension(250, 150));
        jPanelEast.add(jscrlp);

        // SOUTH
        addTextField(paymentAmountField, jPanelSouth, "Payment");
        addTextField(receivedAmountField, jPanelSouth, "Received");
        addTextField(changeField, jPanelSouth, "Change");


        addButton(payButton, payAL, "icons/coins_4011.png", jPanelSouth);

        addButton(breakCheckButton, breakCheckAL, "icons/check-icon.png", jPanelSouth);



        //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setVisible(true);

        customizeGUIItems();
    }


    public void update(Observable o, Object arg) {

        if (o instanceof ConsumersQueueTeller) {

            consumersTable.fireTableDataChanged();

        }

        if (o instanceof CashRegister){

            if (arg.equals("productSalesDataTableChange")){

                productsSaleTable.fireTableDataChanged();
                paymentAmountField.setText(Double.toString(cashRegister.getPaymentAmount()));

            }

            if (arg.equals("enterNewCheck")){

                productsSaleTable.fireTableDataChanged();

                paymentAmountField.setText("0");
                receivedAmountField.setText("0");
                changeField.setText("0");

            }



        }

    }

    public ProductSalesDataTable getProductsConsumer() {
        return productsConsumer;
    }

    private JPanel addPanel(String borderLayout){

        JPanel jPanel = new JPanel();
        getContentPane().add(jPanel, borderLayout);
        jPanel.setLayout(new FlowLayout());

        return jPanel;
    }

    private void addTableToPanel(AbstractTableModel tableModel, Dimension dimension, JPanel jPanel){

        JTable jTabl = new JTable(tableModel);
        jTabl.setPreferredScrollableViewportSize(dimension);

        JScrollPane jscrlp = new JScrollPane(jTabl);

        jPanel.add(jscrlp);


    }

    private void addTextField(JTextField jTextField, JPanel jPanel, String title){

        jTextField.setEditable(false);

        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder(title));
        borderPanel.add(jTextField);


        jPanel.add(borderPanel);

    }

    private void addButton(JButton button, ActionListener al, String iconPath, JPanel panel){

        button.addActionListener(al);
        ImageIcon readBarcodeIcon = createIcon(iconPath);
        button.setIcon(readBarcodeIcon);
        panel.add(button);

    }

    private void customizeGUIItems(){

        switch (state){
            case WAITING:

                readBarcodeButton.setEnabled(false);
                payButton.setEnabled(false);
                breakCheckButton.setEnabled(false);

                break;

            case READING:

                readBarcodeButton.setEnabled(true);
                payButton.setEnabled(true);
                breakCheckButton.setEnabled(false);

                break;

            case PAID:

                readBarcodeButton.setEnabled(false);
                payButton.setEnabled(false);
                breakCheckButton.setEnabled(true);

                break;

            default:

                readBarcodeButton.setEnabled(false);
                payButton.setEnabled(false);
                breakCheckButton.setEnabled(false);

                break;

        }

    }

    protected static ImageIcon createIcon(String path) {
        URL imgURL = TellerWorkingPlace.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("File not found " + path);
            return null;
        }
    }

}
