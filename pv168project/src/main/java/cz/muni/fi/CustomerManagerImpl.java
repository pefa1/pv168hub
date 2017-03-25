package cz.muni.fi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * Created by Marek Pfliegler on 8.3.2017.
 * implementation of CustomerManager interface
 */
public class CustomerManagerImpl implements CustomerManager {

    private DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(CustomerManagerImpl.class);

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

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st1 = con.prepareStatement("select * from customer where email = ?")) {
                st1.setString(1, customer.getEmail());
                try (ResultSet rs = st1.executeQuery()) {
                    if (rs.next()) {
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
            log.error("Error when inserting customer into db");
            throw new ServiceFailureException(ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
        return customer;
    }

    @Override
    public void updateCustomer(Customer customer) throws ValidationException, SQLException, ServiceFailureException {
        validate(customer);
        log.debug("updateCustomer({})", customer);

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st1 = conn.prepareStatement("select * from customer where email = ?")) {
                st1.setString(1, customer.getEmail());
                try (ResultSet rs = st1.executeQuery()) {
                    rs.next();
                    if (rs.next()) {
                        throw new IllegalArgumentException("email is existing");
                    }
                }
            }
        }

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("update customer set fullName=?, email=? where id=?")) {
                st.setString(1, customer.getFullName());
                st.setString(2, customer.getEmail());
                st.setLong(3, customer.getId());
                int n = st.executeUpdate();
                if (n != 1) {
                    throw new IllegalArgumentException("not updated book with id " + customer.getId(), null);
                }
            }
        } catch (SQLException e) {
            log.error("cannot update books", e);
            throw new IllegalArgumentException("database update failed", e);
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        if (id < 0L) {
            throw new IllegalArgumentException("wrong id in delete");
        }

        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            st = conn.prepareStatement(
                    "SELECT id, fullName, email FROM customer WHERE id = ?");
            st.setLong(1, id);
            Customer customer =  executeQueryForSingleCustomer(st);
            if(customer == null){
                throw new IllegalArgumentException("could not find customer");
            }

            st = conn.prepareStatement(
                    "DELETE FROM customer WHERE id = ?");
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
    }

    @Override
    public List<Customer> listAllCustomers() throws SQLException {
        log.debug("getAllCustomers()");
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement st = con.prepareStatement("select * from customer")) {
                try (ResultSet rs = st.executeQuery()) {
                    List<Customer> customers = new ArrayList<>();
                    while (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getLong("id"));
                        customer.setFullName(rs.getString("fullName"));
                        customer.setEmail(rs.getString("email"));
                        customers.add(customer);
                    }
                    return customers;
                }
            }
        } catch (SQLException e) {
            log.error("cannot select books", e);
            throw new SQLException("database select failed", e);
        }
    }

    @Override
    public Customer getCustomerById(Long id) {
        if (id < 0L) {
            throw new IllegalArgumentException("wrong id in get");
        }

        checkDataSource();

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, fullName, email FROM customer WHERE id = ?");
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

    private static Customer executeQueryForSingleCustomer(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Customer result = idToCustomer(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more bodies with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Customer> executeQueryForMultipleCustomers(PreparedStatement st) throws SQLException {
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
