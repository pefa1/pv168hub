package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.Book;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Pepa on 07.05.2017.
 */
public class BookTableModel extends AbstractTableModel {

    private Locale locale = Locale.forLanguageTag("cs-CZ");
    private ResourceBundle bundle = ResourceBundle.getBundle("localization", locale);
    private List<Book> books = new ArrayList<Book>();

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return book.getId();
            case 1:
                return book.getAuthor();
            case 2:
                return book.getTitle();
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
                return bundle.getString("authorLabel");
            case 2:
                return bundle.getString("titleLabel");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addBooks(List<Book>  list){
        books = list;
        fireTableDataChanged();
    }
}
