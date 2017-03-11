package cz.muni.fi.BookManager;

import cz.muni.fi.Book;

import java.util.List;

/**
 * Created by xkosta on 8.3.17.
 */
public interface BookManager {
    Book createBook(Book book);
    void updateBook(Book book);
    void deleteBook(long id);
    List<Book> listAllBooks();
    List<Book> listBooksByTitle(String title);
    Book getBookById(long id);
}
