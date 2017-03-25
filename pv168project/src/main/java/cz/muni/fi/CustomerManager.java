package cz.muni.fi;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 * interface for a customer manager
 */
public interface CustomerManager {

    /**
     * method for creating customer, automatically assigns id to a customer, adds customer to a list, checks whether the customer is already in list
     * @param customer input customer
     * @return customer if it was correctly created
     */
    Customer createCustomer(Customer customer) throws SQLException, ValidationException;

    /**
     * updates customers email and name
     * @param customer input customer
     */
    void updateCustomer(Customer customer) throws ValidationException, SQLException;

    /**
     * deletes customer from list based on his id
     * @param id customer's id
     */
    void deleteCustomer(Long id);

    /**
     * lists all customers
     * @return list of customers
     */
    List<Customer> listAllCustomers() throws SQLException;

    /**
     * looks for a customer based on his id
     * @param id customer's id
     * @return found customer
     */
    Customer getCustomerById(Long id);

    void setDataSource(DataSource ds);
}
