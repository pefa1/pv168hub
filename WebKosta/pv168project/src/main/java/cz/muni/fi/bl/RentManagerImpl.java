package cz.muni.fi.bl;

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
    public void createRent(Rent rent) {
        checkDataSource();
        validate(rent);

        if(rent.getExpectedReturnTime() == null || !LocalDate.now().isBefore(rent.getExpectedReturnTime())){
            throw new IllegalEntityException("expected return date should be after present");
        }

        if(!checkBookIsAvailable(rent.getBook().getId())){
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateRent(Long id, LocalDate newExpectedReturnTime) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("rent id is null");
        }

        if (newExpectedReturnTime == null) {
            throw new IllegalArgumentException("expected return time is null");
        }

        if (newExpectedReturnTime.isBefore(LocalDate.now())) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Book bookOfRent(Rent rent) {
        checkDataSource();
        if (rent == null) {
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Customer customerOfRent(Rent rent) {
        checkDataSource();
        if (rent == null) {
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public boolean ReturnBook(Rent rent) {
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
            String msg = "Error when updating book in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteRent(Long id) {
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
                    "SELECT id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE id = ?");
            st.setLong(1, id);
            Rent rent =  executeQueryForSingleRent(st);
            if(rent == null){
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Rent getRentById(Long id) {
        checkDataSource();

        if (id == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listAllRents() {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listRentsByCustomer(Long customer_id) {
        checkDataSource();

        if (customer_id == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Rent> listRentsByBook(Long book_id) {
        checkDataSource();

        if (book_id == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private boolean checkBookIsAvailable(Long bookId){
        checkDataSource();

        if (bookId == null) {
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
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
        return isAvailable;
    }


    public static Rent executeQueryForSingleRent(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Rent result = rowToRent(rs);
            if (rs.next()) {
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
        if(rent == null){
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getId() == null) {
            throw new IllegalEntityException("rent id is null");
        }
        if(rent.getReturnTime() != null){
            throw new IllegalEntityException("return time should be null");
        }

    }

    private void validate(Rent rent){
        if(rent == null){
            throw new IllegalArgumentException("rent is null");
        }
        if (rent.getBook() == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (rent.getCustomer() == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (rent.getCustomer().getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        if (rent.getBook().getId() == null) {
            throw new IllegalArgumentException("book id is null");
        }
        if(rent.getRentTime() != null){
            throw new IllegalEntityException("rent time should be null");
        }
        if(rent.getReturnTime() != null){
            throw new IllegalEntityException("return time should be null");
        }
        if(rent.getReturnTime() != null){
            throw new IllegalEntityException("return time should be null");
        }

        if(rent.getExpectedReturnTime() == null || !LocalDate.now().isBefore(rent.getExpectedReturnTime())){
            throw new IllegalEntityException("expected return date should be after present");
        }
    }
}
