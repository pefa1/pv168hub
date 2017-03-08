package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;

import java.util.List;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public interface CustomerManager {


    public void createCustomer(Customer customer);

    public void updateCustomer(Customer customer);

    public void deleteCustomer(long id);

    public List<Customer> listAllCustomers();

    public Customer getCustomerById(long id);

}
