package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marek Pfliegler on 8.3.2017.
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
    public void createSameCustomer() throws Exception {

    }

    @Test
    public void createCustomer() throws Exception {
        Customer customer = new Customer("hajzel", "email@email.com");
        assertNotNull("customer is null", customer);
        customer.setId(0L);
        customerManager.createCustomer(customer);
        assertEquals(0L, customerManager.getCustomerById(0L));
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