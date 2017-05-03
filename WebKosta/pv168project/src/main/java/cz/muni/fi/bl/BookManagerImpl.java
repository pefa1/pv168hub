package cz.muni.fi.bl;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Created by xkosta on 8.3.17.
 */
public class BookManagerImpl implements BookManager {
    private DataSource dataSource;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BookManagerImpl.class);

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        logger.debug("Checking data source");
        if (dataSource == null) {
            logger.error("Data source is null");
            throw new IllegalStateException("DataSource is not set");
        }
        logger.debug("Data source OK");
    }

    @Override
    public void createBook(Book book) {
        logger.debug("Creating book...");
        checkDataSource();
        validate(book);
        if (book.getId() != null) {
            logger.error("Book id is already set");
            throw new IllegalEntityException("book id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Book (author,title) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, book.getAuthor());
            st.setString(2, book.getTitle());


            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            book.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting book into db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Book created successfully");
    }

    @Override
    public void updateBook(Book book) {
        logger.debug("Updating book...");
        checkDataSource();
        validate(book);

        if (book.getId() == null) {
            logger.error("Book id is null");
            throw new IllegalEntityException("book id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Book SET author = ?, title = ? WHERE id = ?");
            st.setString(1, book.getAuthor());
            st.setString(2, book.getTitle());
            st.setLong(3, book.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating book in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Book updated successfully");
    }

    @Override
    public void deleteBook(Long id) {
        logger.debug("Deleting book...");
        checkDataSource();
        if (id == null) {
            logger.error("Id is null");
            throw new IllegalEntityException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            st = conn.prepareStatement(
                    "SELECT id, author, title FROM Book WHERE id = ?");
            st.setLong(1, id);
            Book book =  executeQueryForSingleBook(st);
            if(book == null){
                logger.error("Could not find book");
                throw new IllegalEntityException("could not find book");
            }

            st = conn.prepareStatement(
                    "DELETE FROM Book WHERE id = ?");
            st.setLong(1, id);

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, book, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting book from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Book deleted successfully");
    }

    @Override
    public List<Book> listAllBooks() {
        logger.debug("Listing all books...");
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, title FROM Book");
            return executeQueryForMultipleBooks(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all books from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> listBooksByTitle(String title) {
        logger.debug("Listing books by title...");
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, title FROM Book WHERE title = ?");
            st.setString(1, title);
            return executeQueryForMultipleBooks(st);
        } catch (SQLException ex) {
            String msg = "Error when getting books with specific title from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Book getBookById(Long id) {
        logger.debug("Getting book by id...");
        checkDataSource();

        if (id == null) {
            logger.error("Id is null");
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            st = conn.prepareStatement(
                    "SELECT id, author, title FROM Book WHERE id = ?");
            st.setLong(1, id);
            Book book =  executeQueryForSingleBook(st);
            return book;

        } catch (SQLException ex) {
            String msg = "Error when getting book with id = " + id + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);

        }
    }

    public static Book executeQueryForSingleBook(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Book result = rowToBook(rs);
            if (rs.next()) {
                logger.error("Internal integrity error: more books with the same id found!");
                throw new ServiceFailureException(
                        "Internal integrity error: more books with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Book> executeQueryForMultipleBooks(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Book> result = new ArrayList<Book>();
        while (rs.next()) {
            result.add(rowToBook(rs));
        }
        return result;
    }

    static private Book rowToBook(ResultSet rs) throws SQLException {
        Book result = new Book();
        result.setId(rs.getLong("id"));
        result.setAuthor(rs.getString("author"));
        result.setTitle(rs.getString("title"));
        return result;
    }

    private void validate(Book book) {
        logger.debug("Validating book...");
        if (book == null) {
            logger.error("Book is null");
            throw new IllegalArgumentException("book is null");
        }
        if (book.getAuthor() == null) {
            logger.error("Author is null");
            throw new ValidationException("author is null");
        }
        if (book.getTitle() == null) {
            logger.error("Title is null");
            throw new ValidationException("title is null");
        }
        logger.debug("Book is OK");
    }
}
