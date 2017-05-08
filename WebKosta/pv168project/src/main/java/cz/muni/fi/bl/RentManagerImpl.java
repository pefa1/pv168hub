package cz.muni.fi.bl;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentManagerImpl implements RentManager {

    private DataSource dataSource;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RentManagerImpl.class);

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        logger.debug("Checking data source");
        if (dataSource == null) {
            logger.error("Data source is not set");
            throw new IllegalStateException("DataSource is not set");
        }
        logger.debug("Data source is OK");
    }

    @Override
    public void createRent(Rent rent) {
        logger.debug("Creating rent...");
        checkDataSource();
        validate(rent);

        if(rent.getExpectedReturnTime() == null || !LocalDate.now().isBefore(rent.getExpectedReturnTime())){
            logger.error("Expected return date should be after present");
            throw new IllegalEntityException("expected return date should be after present");
        }

        if(!checkBookIsAvailable(rent.getBook().getId())){
            logger.error("Book is not available");
            throw new IllegalArgumentException("book is not available");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            //adding rent to database
            LocalDate now = LocalDate.now();
            st = conn.prepareStatement(
                    "INSERT INTO Rent (rentTime, expectedReturnTime, returnTime, bookId, customerId) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setDate(1, toSqlDate(now));
            st.setDate(2, toSqlDate(rent.getExpectedReturnTime()));
            st.setDate(3, toSqlDate(rent.getReturnTime()));
            st.setLong(4, rent.getBook().getId());
            st.setLong(5, rent.getCustomer().getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            rent.setId(id);
            rent.setRentTime(now);
            conn.commit();

            DBUtils.checkUpdatesCount(count, rent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when creating rent";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Rent created successfully");
    }

    @Override
    public void updateRent(Long id, LocalDate newExpectedReturnTime) {
        logger.debug("Updating rent...");
        checkDataSource();

        if (id == null) {
            logger.error("Rent id is null");
            throw new IllegalArgumentException("rent id is null");
        }

        if (newExpectedReturnTime == null) {
            logger.error("Expected return time is null");
            throw new IllegalArgumentException("expected return time is null");
        }

        if (newExpectedReturnTime.isBefore(LocalDate.now())) {
            logger.error("Expected return time has to be in future");
            throw new IllegalArgumentException("expected return time has to be in future");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            st = conn.prepareStatement(
                    "Select id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE id = ?");
            st.setLong(1, id);
            Rent result = executeQueryForSingleRent(st);
            result.setExpectedReturnTime(newExpectedReturnTime);
            st = conn.prepareStatement(
                    "UPDATE Rent SET rentTime = ?, expectedReturnTime = ?, returnTime = ?, bookId = ?, customerId = ? WHERE id = ?");
            st.setDate(1, toSqlDate(result.getRentTime()));
            st.setDate(2, toSqlDate(result.getExpectedReturnTime()));
            st.setDate(3, toSqlDate(result.getReturnTime()));
            st.setLong(4, bookOfRent(result).getId());
            st.setLong(5, customerOfRent(result).getId());
            st.setLong(6, id);
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, result, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating rent in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Rent updated successfully");
    }

    @Override
    public Book bookOfRent(Rent rent) {
        logger.debug("Book of rent...");
        checkDataSource();
        if (rent == null) {
            logger.error("Rent is null");
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
            logger.error("Rent id is null");
            throw new IllegalEntityException("rent id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;

        try {

            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Book.id, title, author " +
                            "FROM Book JOIN Rent ON Book.id = Rent.bookId " +
                            "WHERE Rent.id = ?");
            st.setLong(1, rent.getId());
            return BookManagerImpl.executeQueryForSingleBook(st);
        } catch (SQLException ex) {
            String msg = "Error when getting book of specific rent " + rent;
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Customer customerOfRent(Rent rent) {
        logger.debug("Customer of rent");
        checkDataSource();
        if (rent == null) {
            logger.error("Rent is null");
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
            logger.error("Rent id is null");
            throw new IllegalEntityException("rent id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Customer.id, fullName, email " +
                            "FROM Customer JOIN Rent ON Customer.id = Rent.customerId " +
                            "WHERE Rent.id = ?");
            st.setLong(1, rent.getId());
            return CustomerManagerImpl.executeQueryForSingleCustomer(st);
        } catch (SQLException ex) {
            String msg = "Error when getting customer of specific rent " + rent;
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public boolean ReturnBook(Rent rent) {
        logger.debug("Returning book...");
        checkDataSource();
        validateForReturn(rent);

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "Select id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE id = ?");
            st.setLong(1, rent.getId());
            Rent result = executeQueryForSingleRent(st);

            result.setReturnTime(LocalDate.now());
            st = conn.prepareStatement(
                    "UPDATE Rent SET returnTime = ? WHERE id = ?");
            st.setDate(1, toSqlDate(result.getReturnTime()));
            st.setLong(2, rent.getId());


            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, false);

            conn.commit();
            if(result.getReturnTime().isBefore(result.getExpectedReturnTime())){
                return true;
            }
            return false;

        } catch (SQLException ex) {
            String msg = "Error when returning the book in the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteRent(Long id) {
        logger.debug("Deleting rent...");
        checkDataSource();
        if (id == null) {
            logger.error("Id of rent is null");
            throw new IllegalEntityException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            st = conn.prepareStatement(
                    "SELECT id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE id = ?");
            st.setLong(1, id);
            Rent rent =  executeQueryForSingleRent(st);
            if(rent == null){
                logger.error("Could not find rent");
                throw new IllegalArgumentException("could not find rent");
            }

            st = conn.prepareStatement(
                    "DELETE FROM Rent WHERE id = ?");
            st.setLong(1, id);

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting rent from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Rent deleted successfully");
    }

    @Override
    public Rent getRentById(Long id) {
        logger.debug("Getting rent by id");
        checkDataSource();

        if (id == null) {
            logger.error("Id of rent is null");
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "Select id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE id = ?");
            st.setLong(1, id);
            Rent result = executeQueryForSingleRent(st);

            result.setBook(bookOfRent(result));
            result.setCustomer(customerOfRent(result));

            return result;

        } catch (SQLException ex) {
            String msg = "Error when getting rent from the db";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listAllRents() {
        logger.debug("Listing all rents...");
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, rentTime, expectedReturnTime, returnTime FROM Rent");
            List<Rent> list =  executeQueryForMultipleRents(st);
            for(Rent rent : list){
                rent.setCustomer(customerOfRent(rent));
                rent.setBook(bookOfRent(rent));
            }
            return list;
        } catch (SQLException ex) {
            String msg = "Error when getting all rents from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listRentsByCustomer(Long customer_id) {
        logger.debug("Listing rents by customer...");
        checkDataSource();

        if (customer_id == null) {
            logger.error("Id of customer is null");
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, fullName, email FROM Customer WHERE id = ?");
            st.setLong(1, customer_id);
            if(null == CustomerManagerImpl.executeQueryForSingleCustomer(st)){
                logger.error("Could not find customer");
                throw new IllegalArgumentException("could not find customer");
            }

            st = conn.prepareStatement(
                    "SELECT Rent.id, rentTime, expectedReturnTime, returnTime " +
                            "FROM Rent JOIN Customer ON Rent.customerId = Customer.Id " +
                            "WHERE Customer.Id = ?");
            st.setLong(1, customer_id);
            List<Rent> list =  executeQueryForMultipleRents(st);
            for(Rent rent : list){
                rent.setCustomer(customerOfRent(rent));
                rent.setBook(bookOfRent(rent));
            }
            return list;
        } catch (SQLException ex) {
            String msg = "Error when getting all rents of customer from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listRentsByBook(Long book_id) {
        logger.debug("Listing rents by id");
        checkDataSource();

        if (book_id == null) {
            logger.error("Book id is null");
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, author, title FROM Book WHERE id = ?");
            st.setLong(1, book_id);
            if(null == BookManagerImpl.executeQueryForSingleBook(st)){
                logger.error("Could not find book");
                throw new IllegalArgumentException("could not find book");
            }

            st = conn.prepareStatement(
                    "SELECT Rent.id, rentTime, expectedReturnTime, returnTime " +
                            "FROM Rent JOIN Book ON Rent.bookId = Book.Id " +
                            "WHERE Book.Id = ?");
            st.setLong(1, book_id);
            List<Rent> list =  executeQueryForMultipleRents(st);
            for(Rent rent : list){
                rent.setCustomer(customerOfRent(rent));
                rent.setBook(bookOfRent(rent));
            }
            return list;
        } catch (SQLException ex) {
            String msg = "Error when getting all rents of book from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private boolean checkBookIsAvailable(Long bookId){
        logger.debug("Checking book whether is available");
        checkDataSource();

        if (bookId == null) {
            logger.error("Book id is null");
            return false;
        }
        boolean isAvailable = true;

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            st = conn.prepareStatement(
                    "SELECT Rent.id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE bookId = ?");
            st.setLong(1, bookId);

            List<Rent> rents =  executeQueryForMultipleRents(st);

            for(Rent rent : rents){
                if(rent.getReturnTime() == null){
                    isAvailable = false;
                    break;
                }
            }

        } catch (SQLException ex) {
            String msg = "Error when getting book with id = " + bookId + " from DB";
            logger.error(msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
        logger.debug("Book checked successfully");
        return isAvailable;
    }

    public List<Book> listAvailableBooks(List<Book> books){
        List<Book> result = new ArrayList<>();
        for(Book book : books){
            if(checkBookIsAvailable(book.getId())){
                result.add(book);
            }
        }
        return result;
    }


    public static Rent executeQueryForSingleRent(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Rent result = rowToRent(rs);
            if (rs.next()) {
                logger.error("Internal integrity error: more rents with the same id found!");
                throw new ServiceFailureException(
                        "Internal integrity error: more rents with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Rent> executeQueryForMultipleRents(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Rent> result = new ArrayList<Rent>();
        while (rs.next()) {
            result.add(rowToRent(rs));
        }
        return result;
    }

    static private Rent rowToRent(ResultSet rs) throws SQLException {
        Rent result = new Rent();
        result.setId(rs.getLong("id"));
        result.setExpectedReturnTime(toLocalDate(rs.getDate("expectedReturnTime")));
        result.setRentTime(toLocalDate(rs.getDate("rentTime")));
        result.setReturnTime(toLocalDate(rs.getDate("returnTime")));
        return result;
    }


    private static Date toSqlDate(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private void validateForReturn(Rent rent){
        logger.debug("Validating for return...");
        if(rent == null){
            logger.error("Rent is null");
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
            logger.error("Rent id is null");
            throw new IllegalEntityException("rent id is null");
        }
        if(rent.getReturnTime() != null){
            logger.error("Return time should be null");
            throw new IllegalEntityException("return time should be null");
        }
        logger.debug("Rent is OK");
    }

    private void validate(Rent rent){
        logger.debug("Validating rent...");
        if(rent == null){
            logger.error("Rent is null");
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getBook() == null) {
            logger.error("Book is null");
            throw new IllegalArgumentException("book is null");
        }
        if (rent.getCustomer() == null) {
            logger.error("Customer is null");
            throw new IllegalArgumentException("customer is null");
        }
        if (rent.getCustomer().getId() == null) {
            logger.error("Customer id is null");
            throw new IllegalArgumentException("customer id is null");
        }
        if (rent.getBook().getId() == null) {
            logger.error("Book id is null");
            throw new IllegalArgumentException("book id is null");
        }
        if(rent.getRentTime() != null){
            logger.error("Rent time should be null");
            throw new IllegalEntityException("rent time should be null");
        }
        if(rent.getReturnTime() != null){
            logger.error("Return time should be null");
            throw new IllegalEntityException("return time should be null");
        }
        if(rent.getExpectedReturnTime() == null || !LocalDate.now().isBefore(rent.getExpectedReturnTime())){
            logger.error("Expected return date should be after present");
            throw new IllegalEntityException("expected return date should be after present");
        }
        logger.debug("Rent is OK");
    }
}
