package gui;

import person.ConsumersQueueTeller;
import person.Person;

import javax.swing.table.AbstractTableModel;

/**
 * Created by Aschur on 09.12.2016.
 */
public class ConsumersTable extends AbstractTableModel {

    private ConsumersQueueTeller consumersQueueTeller;

    public ConsumersTable(ConsumersQueueTeller consumersQueueTeller) {

        this.consumersQueueTeller = consumersQueueTeller;

    }

    public int getRowCount() {

        return consumersQueueTeller.size();

    }

    public int getColumnCount() {

        return 1;

    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        Object[] consumers = consumersQueueTeller.getConsumersArray();

        Person p = (Person) consumers[rowIndex];

        String name = p.getName();

        return name;


    }

    @Override
    public String getColumnName(int column) {

        String columnName = "";
        if (column == 0) {
            columnName = "Consumers";
        }

        return columnName;

    }
}
