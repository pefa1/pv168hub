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
        Customer customer = new Customer("dilino", "email@mail.com");
        Customer customer1 = new Customer("dilino", "email@mail.com");

        customer.setId(2L);
        customer1.setId(2L);

        assertTrue("customer and customer1 are the same", customer.equals(customer1));

        customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex){
        }
    }

    @Test
    public void createCustomer() throws Exception {
        Customer customer = new Customer("hajzel", "email@email.com");
        assertNotNull("customer is null", customer);

        customer.setId(0L);
        customerManager.createCustomer(customer);
        assertEquals("customer has not 0L id",0L, customerManager.getCustomerById(0L).getId());

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setEmail("mail@mail.com");
        customer1.setFullName("anton");

        assertNotNull("customer1 is null", customer1);
        assertEquals("customer1 has not 1L id", 1L, customer1.getId());

        customerManager.createCustomer(customer1);

        assertEquals("customer1 cannot be found by id", customer1, customerManager.getCustomerById(1L));
        assertEquals("customer cannot be found by id", customer, customerManager.getCustomerById(0L));
    }

    @Test
    public void updateCustomer() throws Exception {
        Customer customer = new Customer("blabla", "email@mail.sk");
        customer.setId(3L);
        customerManager.createCustomer(customer);

        assertEquals("customer should be in the manager", customer, customerManager.getCustomerById(3L));

        assertEquals("name should be blabla", "blabla", customerManager.getCustomerById(3L).getFullName());
        customerManager.getCustomerById(3L).setFullName("zmena");

        assertEquals("name should be zmena", "zmena", customerManager.getCustomerById(3L).getFullName());

        assertEquals("email should be email@mail.sk", "email@mail.sk", customerManager.getCustomerById(3L).getEmail());
        customerManager.getCustomerById(3L).setEmail("mail@email.cz");
        customerManager.updateCustomer(customer);
        assertEquals("email should be email@mail.sk", "mail@email.cz", customerManager.getCustomerById(3L).getEmail());


        assertEquals("id should be 3L", 3L, customer.getId());
        customerManager.getCustomerById(3L).setId(4L);
        customerManager.updateCustomer(customer);
        assertEquals("id should be 4L", 4L, customer.getId());
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