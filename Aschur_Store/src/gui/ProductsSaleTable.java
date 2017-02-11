package gui;

import equipment.CashRegister;
import product.Product;
import product.ProductSalesDataTable;

import javax.swing.table.AbstractTableModel;
import java.util.Map;

/**
 * Created by Aschur on 12.12.2016.
 */
public class ProductsSaleTable extends AbstractTableModel {

    private CashRegister cashRegister;

    public ProductsSaleTable(CashRegister cashRegister) {
        this.cashRegister = cashRegister;
    }

    public int getRowCount() {

        ProductSalesDataTable productSalesDataTable = cashRegister.getProductSalesDataTable();
        return productSalesDataTable.size();

    }

    public int getColumnCount() {
        return 4;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        ProductSalesDataTable productSalesDataTable = cashRegister.getProductSalesDataTable();

        switch (columnIndex) {
            case 0:
                return productSalesDataTable.get(rowIndex).getProduct().getName();
            case 1:
                return productSalesDataTable.get(rowIndex).getCount();
            case 2:
                return productSalesDataTable.get(rowIndex).getPrice();
            case 3:
                return productSalesDataTable.get(rowIndex).getTotal();
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Product";
            case 1:
                return "Count";
            case 2:
                return "Price";
            case 3:
                return "Total";
            default:
                return "";
        }
    }
}
