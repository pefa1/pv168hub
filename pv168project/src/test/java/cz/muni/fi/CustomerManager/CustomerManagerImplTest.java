package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pefa1 on 8.3.2017.
 */
public class CustomerManagerImplTest {

    CustomerManager customerManager;

    @Before
    public void setUp() throws Exception {
        customerManager = new CustomerManagerImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createNullCustomer() throws Exception {
        try {
            CustomerManager customerManager = new CustomerManagerImpl();
            customerManager.createCustomer(null);
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void createCustomer() throws Exception {
        Customer customer = new Customer("hajzel", "email@email.com");
        assertNotNull("customer is null", customer);
    }

    @Test
    public void updateCustomer() throws Exception {

    }

    @Test
    public void deleteCustomer() throws Exception {

    }

    @Test
    public void listAllCustomers() throws Exception {

    }

    @Test
    public void getCustomerById() throws Exception {

    }

}