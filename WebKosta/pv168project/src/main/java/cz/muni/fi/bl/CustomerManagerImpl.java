package cz.muni.fi.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Marek Pfliegler on 8.3.2017.
 * implementation of CustomerManager interface
 */
public class CustomerManagerImpl implements CustomerManager {

    private DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(CustomerManagerImpl.class);

    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        log.debug("Checking data source");
        if (dataSource == null) {
            log.error("Data source is not set");
            throw new IllegalStateException("DataSource is not set");
        }
        log.debug("Data source is OK");
    }

    @Override
    public Customer createCustomer(Customer customer) throws SQLException, ValidationException {
        log.debug("Creating customer...");
        checkDataSource();
        validate(customer);
        if (customer.getId() != null) {
            log.error("Customer id is already set");
            throw new IllegalEntityException("customer id is already set");
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st1 = con.prepareStatement("SELECT * FROM Customer WHERE email = ?")) {
                st1.setString(1, customer.getEmail());
                try (ResultSet rs = st1.executeQuery()) {
                    if (rs.next()) {
                        log.error("Email is existing");
                        throw new IllegalArgumentException("email is existing");
                    }
                }
            }
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Customer (fullName,email) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, customer.getFullName());
            st.setString(2, customer.getEmail());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            customer.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            log.error("Error when inserting customer into db");
            throw new ServiceFailureException(ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        log.debug("Customer created successfully");
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) throws ValidationException, SQLException, ServiceFailureException {
        validate(customer);
        log.debug("Updating customer({})", customer);

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st1 = conn.prepareStatement("SELECT * FROM Customer WHER email = ?")) {
                st1.setString(1, customer.getEmail());
                try (ResultSet rs = st1.executeQuery()) {
                    rs.next();
                    if (rs.next()) {
                        log.debug("Email is existing");
                        throw new IllegalArgumentException("email is existing");
                    }
                }
            }
        }

        Connection con = null;
        PreparedStatement st = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);

            st = con.prepareStatement("UPDATE Customer SET fullName=?, email=? WHERE id=?");

            st.setString(1, customer.getFullName());
            st.setString(2, customer.getEmail());
            st.setLong(3, customer.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
            con.commit();
        } catch (SQLException e) {
            log.error("cannot update customer", e);
            throw new IllegalArgumentException("database update failed", e);
        } finally {
            DBUtils.doRollbackQuietly(con);
            DBUtils.closeQuietly(con, st);
        }
        log.debug("Customer updated successfully");
    }

    @Override
    public void deleteCustomer(Long id) {
        log.debug("Deleting customer...");
        if (id < 0L) {
            log.error("Wrong id in deleteCustomer");
            throw new IllegalArgumentException("wrong id in delete");
        }

        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            st = conn.prepareStatement(
                    "SELECT id, fullName, email FROM Customer WHERE id = ?");
            st.setLong(1, id);
            Customer customer =  executeQueryForSingleCustomer(st);
            if(customer == null){
                log.error("Could not find customer");
                throw new IllegalArgumentException("could not find customer");
            }

            st = conn.prepareStatement(
                    "DELETE FROM Customer WHERE id = ?");
            st.setLong(1, id);

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, customer, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting customer from the db";
            log.error(msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        log.debug("Customer deleted successfully");
    }

    @Override
    public List<Customer> listAllCustomers() throws SQLException {
        log.debug("Listing all customers...");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("SELECT * FROM Customer")) {
                /*try (ResultSet rs = st.executeQuery()) {
                    List<Customer> customers = new ArrayList<>();
                    while (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getLong("id"));
                        customer.setFullName(rs.getString("fullName"));
                        customer.setEmail(rs.getString("email"));
                        customers.add(customer);
                    }
                    return customers;
                }*/
                return executeQueryForMultipleCustomers(st);
            }
        } catch (SQLException e) {
            log.error("cannot select customers", e);
            throw new SQLException("database select failed", e);
        }
    }

    @Override
    public Customer getCustomerById(Long id) {
        log.debug("Getting customer by id...");
        if (id < 0L) {
            log.error("Wrong id in getCustomer");
            throw new IllegalArgumentException("wrong id in get");
        }

        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, fullName, email FROM Customer WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleCustomer(st);
        } catch (SQLException ex) {
            String msg = "Error when getting body with id = " + id + " from DB";
            log.error(msg);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private void validate(Customer customer) throws ValidationException {
        log.debug("Validating customer...");
        if (customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getFullName() == null) {
            log.error("Customer name is null");
            throw new ValidationException("name is null");
        }
        if (customer.getEmail() == null) {
            log.error("Email is null");
            throw new ValidationException("email is null");
        }
        log.debug("Customer is OK");
    }

    public static Customer executeQueryForSingleCustomer(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Customer result = idToCustomer(rs);
            if (rs.next()) {
                log.error("Internal integrity error: more customers with the same id found!");
                throw new ServiceFailureException(
                        "Internal integrity error: more customers with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    public static List<Customer> executeQueryForMultipleCustomers(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Customer> result = new ArrayList<>();
        while (rs.next()) {
            result.add(idToCustomer(rs));
        }
        return result;
    }

    static private Customer idToCustomer(ResultSet rs) throws SQLException {
        Customer result = new Customer();
        result.setId(rs.getLong("id"));
        result.setFullName(rs.getString("fullName"));
        result.setEmail(rs.getString("email"));

        return result;
    }
}
