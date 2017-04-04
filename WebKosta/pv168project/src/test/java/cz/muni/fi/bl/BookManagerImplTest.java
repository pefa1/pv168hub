package cz.muni.fi.bl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

/**
 * Created by xkosta on 8.3.17.
 */
public class BookManagerImplTest {

    private BookManagerImpl bookManager;
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
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,BookManager.class.getResource("createTables.sql"));
        bookManager = new BookManagerImpl();
        bookManager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,BookManager.class.getResource("dropTables.sql"));
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

        Long bookId = book.getId();
        assertNotNull("Book id is null", bookId);

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

    @Test
    public void getBookByIdBookNegativeId() throws Exception {
        assertNull("Should return null", bookManager.getBookById(-10L));
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

    @Test(expected = IllegalEntityException.class)
    public void updateBookDoesNotExist() throws Exception {
        Book book = sampleBookBuilder().build();
        bookManager.createBook(book);
        bookManager.deleteBook(book.getId());
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

    @Test(expected = IllegalEntityException.class)
    public void deleteBookDoesNotExist() throws Exception {
        Book book = sampleBookBuilder().build();

        bookManager.createBook(book);
        bookManager.deleteBook(book.getId());
        bookManager.deleteBook(book.getId());
    }

    @Test
    public void listAllBooks() throws Exception {
        assertThat(bookManager.listAllBooks()).isEmpty();

        Book book1 = sampleBookBuilder().build();
        Book book2 = sample2BookBuilder().build();

        bookManager.createBook(book1);
        bookManager.createBook(book2);

        assertThat(bookManager.listAllBooks())
                .usingFieldByFieldElementComparator()
                .containsOnly(book1, book2);
    }

    @Test
    public void listBooksByTitle() throws Exception {
        assertThat(bookManager.listBooksByTitle("wrong title")).isEmpty();

        Book book1 = sampleBookBuilder().build();
        Book book2 = sample2BookBuilder().build();
        Book book3 = sampleBookBuilder().build();
        book3.setAuthor("novy autor");

        bookManager.createBook(book1);
        bookManager.createBook(book2);

        assertThat(bookManager.listBooksByTitle(book1.getTitle()))
                .usingFieldByFieldElementComparator()
                .containsOnly(book1);

        bookManager.createBook(book3);
        assertThat(bookManager.listBooksByTitle(book1.getTitle()))
                .usingFieldByFieldElementComparator()
                .containsOnly(book1, book3);

        assertThat(bookManager.listAllBooks())
                .usingFieldByFieldElementComparator()
                .containsOnly(book1, book2, book3);

        assertThat(bookManager.listBooksByTitle("wrong title")).isEmpty();
    }

}