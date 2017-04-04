package cz.muni.fi.bl;

import java.util.List;

/**
 * Created by xkosta on 8.3.17.
 */
public interface BookManager {
    void createBook(Book book);
    void updateBook(Book book);
    void deleteBook(Long id);
    List<Book> listAllBooks();
    List<Book> listBooksByTitle(String title);
    Book getBookById(Long id);
}
