package cz.muni.fi;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        if(!checkBookIsAvailabel(rent.getBook().getId())){
            throw new IllegalArgumentException("book is not available");
        }
        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            updateSt = conn.prepareStatement(
                    "UPDATE Rent SET bookId = ? WHERE id = ? AND bookId IS NULL");
            updateSt.setLong(1, rent.getBook().getId());
            updateSt.setLong(2, rent.getId());
            updateSt = conn.prepareStatement(
                    "UPDATE Rent SET customerId = ? WHERE id = ? AND customerId IS NULL");
            updateSt.setLong(1, rent.getCustomer().getId());
            updateSt.setLong(2, rent.getId());
            int count = updateSt.executeUpdate();
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
            DBUtils.closeQuietly(conn, updateSt);
        }
    }

    @Override
    public void updateRent(Rent rent) {

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

    private boolean checkBookIsAvailabel(Long bookId){
        return false;
    }
}
