package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.Rent;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pepa on 08.05.2017.
 */
public class RentTableModel extends AbstractTableModel {

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


    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return "bookId";
            case 2:
                return "Author of book";
            case 3:
                return "Title of book";
            case 4:
                return "customerId";
            case 5:
                return "Full name of customer";
            case 6:
                return "Email of customer";
            case 7:
                return "Rent time";
            case 8:
                return "Expected return time";
            case 9:
                return "Return time";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addRents(List<Rent>  list){
        rents = list;
        fireTableDataChanged();
    }
}
