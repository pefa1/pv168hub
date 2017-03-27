package cz.muni.fi;

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
        if (rent.getBook() == null) {
            throw new IllegalArgumentException("book is null");
        }
        if (rent.getId() == null) {
            throw new IllegalEntityException("rent id is null");
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

        if(!checkBookIsAvailable(rent.getBook().getId())){
            throw new IllegalArgumentException("book is not available");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            //adding rent to database
            st = conn.prepareStatement(
                    "INSERT INTO Rent (rentTime, expectedReturnTime, returnTime) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setDate(1, toSqlDate(LocalDate.now()));
            st.setDate(2, toSqlDate(rent.getExpectedReturnTime()));
            st.setDate(3, toSqlDate(rent.getReturnTime()));


            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, rent, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            rent.setId(id);
            conn.commit();

            st = conn.prepareStatement(
                    "UPDATE Rent SET bookId = ? WHERE id = ? AND bookId IS NULL");
            st.setLong(1, rent.getBook().getId());
            st.setLong(2, rent.getId());
            st = conn.prepareStatement(
                    "UPDATE Rent SET customerId = ? WHERE id = ? AND customerId IS NULL");
            st.setLong(1, rent.getCustomer().getId());
            st.setLong(2, rent.getId());
            count = st.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException(
                        "Rent " + rent + " not found or it has customer and book set");
            }
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
    public void updateRent(Rent rent) {

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
                    "SELECT Book.id, title, author " +
                            "FROM Book JOIN Rent ON Book.id = Rent.bookId " +
                            "WHERE Rent.id = ?");
            st.setLong(1, rent.getId());
            return CustomerManagerImpl.executeQueryForSingleCustomer(st);
        } catch (SQLException ex) {
            String msg = "Error when getting book of specific rent " + rent;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public LocalDate ReturnBook(Rent rent) {
        return null;
    }

    @Override
    public void deleteRent(Long id) {

    }

    @Override
    public Rent getRentById(Long id) {
        return null;
    }

    @Override
    public List<Rent> listAllRents() {
        return null;
    }

    @Override
    public List<Rent> listRentsByCustomer(Long customer_id) {
        return null;
    }

    @Override
    public List<Rent> listRentsByBook(Long book_id) {
        return null;
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
                    "SELECT id, rentTime, expectedReturnTime, returnTime FROM Rent WHERE bookId = ?");
            st.setLong(1, bookId);

            List<Rent> rents =  executeQueryForMultipleRents(st);

            for(Rent rent : rents){
                if(rent.getRentTime() == null){
                    isAvailable = false;
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


    static Rent executeQueryForSingleRent(PreparedStatement st) throws SQLException, ServiceFailureException {
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

    static List<Rent> executeQueryForMultipleRents(PreparedStatement st) throws SQLException {
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

}
