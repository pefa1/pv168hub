package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.Rent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Pepa on 08.05.2017.
 */
public class RentTableModel extends AbstractTableModel {

    private Locale locale = Locale.forLanguageTag("");
    private ResourceBundle bundle = ResourceBundle.getBundle("localization", locale);
    private List<Rent> rents = new ArrayList<Rent>();
    @Override
    public int getRowCount() {
        return rents.size();
    }

    @Override
    public int getColumnCount() {
        return 10;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Rent rent = rents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rent.getId();
            case 1:
                return rent.getBook().getId();
            case 2:
                return rent.getBook().getAuthor();
            case 3:
                return rent.getBook().getTitle();
            case 4:
                return rent.getCustomer().getId();
            case 5:
                return rent.getCustomer().getFullName();
            case 6:
                return rent.getCustomer().getEmail();
            case 7:
                return rent.getRentTime();
            case 8:
                return rent.getExpectedReturnTime();
            case 9:
                if(rent.getRentTime() == null){
                    return "Not returned yet";
                }
                return rent.getReturnTime();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void changeResourceBundle(String baseName, Locale locale){
        if(baseName != null && locale != null){
            this.locale = locale;
            this.bundle = ResourceBundle.getBundle(baseName, locale);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return bundle.getString("bookId");
            case 2:
                return bundle.getString("authorOfBook");
            case 3:
                return bundle.getString("titleOfBook");
            case 4:
                return bundle.getString("customerId");
            case 5:
                return bundle.getString("fullNameOfCustomer");
            case 6:
                return bundle.getString("emailOfCustomer");
            case 7:
                return bundle.getString("rentTime");
            case 8:
                return bundle.getString("expectedReturnTimeLabel");
            case 9:
                return bundle.getString("returnTime");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addRents(List<Rent>  list){
        rents = list;
        fireTableDataChanged();
    }
}
