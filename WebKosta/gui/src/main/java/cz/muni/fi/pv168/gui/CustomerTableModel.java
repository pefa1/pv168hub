package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Pepa on 08.05.2017.
 */
public class CustomerTableModel extends AbstractTableModel {

    private Locale locale = Locale.forLanguageTag("cs-CZ");
    private ResourceBundle bundle = ResourceBundle.getBundle("localization", locale);
    private List<Customer> customers = new ArrayList<Customer>();
    @Override
    public int getRowCount() {
        return customers.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return customer.getId();
            case 1:
                return customer.getFullName();
            case 2:
                return customer.getEmail();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return bundle.getString("fullNameLabel");
            case 2:
                return bundle.getString("emailLabel");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addCustomers(List<Customer>  list){
        customers = list;
        fireTableDataChanged();
    }
}
