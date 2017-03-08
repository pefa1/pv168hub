package cz.muni.fi.BookManager;

import cz.muni.fi.Book;

import java.util.List;

/**
 * Created by xkosta on 8.3.17.
 */
public interface BookManager {
    public Book createBook(Book book);
    public void updateBook(Book book);
    public void deleteBook(Long id);
    public List<Book> listAllBooks();
    public List<Book> listBooksByTitle(String title);
    public Book getBookById(Long id);
}
