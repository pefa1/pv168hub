package cz.muni.fi.BookManager;
import static org.junit.Assert.*;
import cz.muni.fi.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Test(expected = IllegalArgumentException.class)
    public void createBook() throws Exception {
        Book book = new Book("JK", "LUL");
        assertNotNull( "book not returned", book);
        try{
            bookManager.createBook(null);
            bookManager.createBook(book);
            fail("expected exception");
        }catch (IllegalArgumentException e){
        }


    }

    @Test
    public void updateBook() throws Exception {

    }

    @Test
    public void deleteBook() throws Exception {

    }

    @Test
    public void listAllBooks() throws Exception {

    }

    @Test
    public void listBooksByTitle() throws Exception {

    }

    @Test
    public void getBookById() throws Exception {

    }

}