package cz.muni.fi;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Marek Pfliegler on 8.3.2017.
 * implementation of CustomerManager interface
 */
public class CustomerManagerImpl implements CustomerManager {

    private DataSource dataSource;
    private static final Logger logger = Logger.getLogger(
            CustomerManagerImpl.class.getName());

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Customer createCustomer(Customer customer) throws SQLException, ValidationException {
        checkDataSource();
        validate(customer);
        if (customer.getId() != null) {
            throw new IllegalEntityException("customer id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO customer (fullName,email) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, customer.getFullName());
            st.setString(2, customer.getEmail());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            customer.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting grave into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) throws ValidationException {
        validate(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (id < 0L) {
            throw new IllegalArgumentException("wrong id in delete");
        }
    }

    @Override
    public List<Customer> listAllCustomers() {
        return null;
    }

    @Override
    public Customer getCustomerById(Long id) {
        if (id < 0L) {
            throw new IllegalArgumentException("wrong id in get");
        }
        return null;
    }

    private void validate(Customer customer) throws ValidationException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getFullName() == null) {
            throw new ValidationException("name is null");
        }
        if (customer.getEmail() == null) {
            throw new ValidationException("email is null");
        }
    }
}
