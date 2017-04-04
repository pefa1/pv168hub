package cz.muni.fi.bl;

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
    private static final Logger logger = Logger.getLogger(
            CustomerManagerImpl.class.getName());

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createBook(Book book) {
        checkDataSource();
        validate(book);
        if (book.getId() != null) {
            throw new IllegalEntityException("body id is already set");
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateBook(Book book) {
        checkDataSource();
        validate(book);

        if (book.getId() == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteBook(Long id) {
        checkDataSource();
        if (id == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> listAllBooks() {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Book> listBooksByTitle(String title) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Book getBookById(Long id) {

        checkDataSource();

        if (id == null) {
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
            logger.log(Level.SEVERE, msg, ex);
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
        if (book == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (book.getAuthor() == null) {
            throw new ValidationException("author is null");
        }
        if (book.getTitle() == null) {
            throw new ValidationException("title is null");
        }
    }
}
