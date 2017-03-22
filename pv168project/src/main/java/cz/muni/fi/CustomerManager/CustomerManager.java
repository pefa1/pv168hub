package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;

import javax.sql.DataSource;
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
    Customer createCustomer(Customer customer);

    /**
     * updates customers email and name
     * @param customer input customer
     */
    void updateCustomer(Customer customer);

    /**
     * deletes customer from list based on his id
     * @param id customer's id
     */
    void deleteCustomer(long id);

    /**
     * lists all customers
     * @return list of customers
     */
    List<Customer> listAllCustomers();

    /**
     * looks for a customer based on his id
     * @param id customer's id
     * @return found customer
     */
    Customer getCustomerById(long id);

    void setDataSource(DataSource ds);
}
