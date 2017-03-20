package cz.muni.fi.BookManager;
import static org.junit.Assert.*;
import cz.muni.fi.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;
import java.time.*;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;

/**
 * Created by xkosta on 8.3.17.
 */
public class BookManagerImplTest {

    BookManagerImpl bookManager;
    private javax.sql.DataSource ds;

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
        ds.setDatabaseName("memory:bookmgr-test");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws Exception {
        bookManager = new BookManagerImpl();
        ds = prepareDataSource();
        bookManager.setDataSource(ds);

    }

    @After
    public void tearDown() throws Exception {

    }


    private BookBuilder sampleBookBuilder() {
        return new BookBuilder()
                .author("Joe")
                .title("nevim");
    }

    private BookBuilder sample2BookBuilder() {
        return new BookBuilder()
                .author("James")
                .title("title");
    }

    @Test
    public void createBookCorrectInput(){
        Book book = sampleBookBuilder().build();
        bookManager.createBook(book);

        long bookId = book.getId();
        assertNotEquals("Book id is 0", bookId, 0L);
        Book result = bookManager.createBook(book);

        assertEquals("Book should be equal", bookManager.getBookById(bookId), book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createBookNullInput() throws Exception {
        bookManager.createBook(null);
    }

    @Test
    public void getBookById() throws Exception {

        Book anotherBook = sample2BookBuilder().build();
        Book book = sampleBookBuilder().build();

        bookManager.createBook(book);
        bookManager.createBook(anotherBook);

        assertNotNull( "getBookById returns null", bookManager.getBookById(book.getId()));

        assertEquals("Could not get the book", bookManager.getBookById(book.getId()), book);
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
        Book book1 = sampleBookBuilder().build();
        Book book2 = sample2BookBuilder().build();

        bookManager.createBook(book1);
        bookManager.createBook(book2);

        book1.setAuthor("Ty");
        bookManager.updateBook(book1);

        assertEquals("Author did not change", book1, bookManager.getBookById(book1.getId()));
        assertEquals("Author of not changed book changed", book2, bookManager.getBookById(book2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateBookDoesNotExist() throws Exception {
        Book book = sampleBookBuilder().build();
        bookManager.updateBook(book);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateBookNull() throws Exception {
        bookManager.updateBook(null);
    }

    @Test
    public void deleteBook() throws Exception {
        Book book = sampleBookBuilder().build();

        bookManager.createBook(book);
        assertEquals("Book is not created", bookManager.getBookById(book.getId()), book);

        bookManager.deleteBook(book.getId());

        assertNull("Book should not exist", bookManager.getBookById(book.getId()));
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