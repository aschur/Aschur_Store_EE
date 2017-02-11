package gui;

import person.ConsumersQueueTeller;
import person.Person;
import product.ProductSalesDataTable;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Aschur on 09.12.2016.
 */
public class ProductsConsumerTable extends AbstractTableModel {

    private TellerWorkingPlace tellerWorkingPlace;

    public ProductsConsumerTable(TellerWorkingPlace tellerWorkingPlace) {
        this.tellerWorkingPlace = tellerWorkingPlace;
    }

    public int getRowCount() {

        ProductSalesDataTable psdt = tellerWorkingPlace.getProductsConsumer();
        return psdt.size();

    }

    public int getColumnCount() {

        return 2;

    }

    public Object getValueAt(int rowIndex, int columnIndex) {


        ProductSalesDataTable psdt = tellerWorkingPlace.getProductsConsumer();

        switch (columnIndex) {
            case 0:
                return psdt.get(rowIndex).getProduct().getName();
            case 1:
                return psdt.get(rowIndex).getCount();
            default:
                return "";
        }

    }

    @Override
    public String getColumnName(int column) {

        String columnName = "";
        if (column == 0) {
            columnName = "Product";
        }else if(column == 1){
            columnName = "Count";
        };

        return columnName;

    }
}
