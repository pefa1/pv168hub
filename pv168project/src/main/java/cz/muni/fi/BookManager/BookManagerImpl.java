package cz.muni.fi.BookManager;

import cz.muni.fi.Book;

import java.util.List;
import javax.sql.DataSource;

/**
 * Created by xkosta on 8.3.17.
 */
public class BookManagerImpl implements BookManager {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Book createBook(Book book) {
        return null;
    }

    @Override
    public void updateBook(Book book) {

    }

    @Override
    public void deleteBook(long id) {

    }

    @Override
    public List<Book> listAllBooks() {
        return null;
    }

    @Override
    public List<Book> listBooksByTitle(String title) {
        return null;
    }

    @Override
    public Book getBookById(long id) {
        return null;
    }
}
