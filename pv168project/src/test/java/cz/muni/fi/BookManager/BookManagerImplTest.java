package cz.muni.fi.BookManager;
import static org.junit.Assert.*;
import cz.muni.fi.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by xkosta on 8.3.17.
 */
public class BookManagerImplTest {

    BookManager bookManager;

    @Before
    public void setUp() throws Exception {
        bookManager = new BookManagerImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createBookCorrectInput(){
        Book book = new Book("Ja", "nevim");
        Book result = bookManager.createBook(book);
        assertNotNull( "createBook returns null", result);
        assertEquals("Incorrect title", result.getTitle(), "nevim");
        assertEquals("Incorrect author", result.getAuthor(), "Ja");

        Book resultFromDb = bookManager.getBookById(result.getId());
        assertEquals("Could not get the book", resultFromDb, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookNullInput() throws Exception {
        bookManager.createBook(null);
    }

    @Test
    public void getBookById() throws Exception {
        Book book = new Book("Ja", "nevim");
        Book result = bookManager.createBook(book);
        Book resultFromDb = bookManager.getBookById(result.getId());
        assertNotNull( "createBook returns null", result);
        assertNotNull( "returns null", resultFromDb);

        assertEquals("Could not get the book", resultFromDb, result);
        assertNotSame("Result should not be same", resultFromDb, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBookByIdBookNegativeId() throws Exception {
        bookManager.getBookById(-10L);
    }

    @Test
    public void getBookByIdBookDoesNotExist() throws Exception {
        assertNull("Should return null", bookManager.getBookById(10L));
    }

    @Test
    public void updateBookCorrectInput() throws Exception {
        Book book = new Book("Ja", "nevim");
        book.setAuthor("Ty");
        bookManager.updateBook(book);
        book = bookManager.getBookById(book.getId());
        assertNotNull( "returns null", book);

        assertEquals("Author did not change", book.getAuthor(), "Ty");
        assertNotEquals("Id changed", book.getId(), 3L);
        assertNotEquals("Title changed", book.getTitle(), "nevim");

        book.setTitle("nevim2");
        bookManager.updateBook(book);
        assertEquals("Title did not change", book.getTitle(), "nevim2");
        assertEquals("Author changed", book.getAuthor(), "Ty");
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateBookDoesNotExist() throws Exception {
        Book book = new Book("Ja", "nevim");
        bookManager.updateBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateBookNull() throws Exception {
        bookManager.updateBook(null);
    }

    @Test
    public void deleteBook() throws Exception {
        Book book = new Book("Ja", "nevim");
        Book result = bookManager.createBook(book);
        bookManager.deleteBook(result.getId());
        assertNull("Book should not exist", bookManager.getBookById(result.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteBookDoesNotExist() throws Exception {
        bookManager.deleteBook(10L);
    }

    @Test
    public void listAllBooks() throws Exception {

    }

    @Test
    public void listBooksByTitle() throws Exception {

    }

}