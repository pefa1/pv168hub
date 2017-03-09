package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Marek Pfliegler on 8.1.2017.
 */
public class CustomerManagerImplTest {

    CustomerManager customerManager;

    @Before
    public void setUp() throws Exception {
        customerManager = new CustomerManagerImpl(); // medzi testami ostava iba to co je tu, pred kazdym testom sa spusti setup
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

        customer.setId(1L);
        customer1.setId(1L);

        assertTrue("customer and customer1 are the same", customer.equals(customer1));

        customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex){
        }

        customer1.setFullName("tento");

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
        }

        customer1.setFullName("dilino");
        customer1.setEmail("mail@mail.com");

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
        }

        customer1.setEmail("email@email.com");
        customer1.setId(2L);

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
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
        customer.setId(1L);
        customerManager.createCustomer(customer);

        assertEquals("customer should be in the manager", customer, customerManager.getCustomerById(1L));

        assertEquals("name should be blabla", "blabla", customerManager.getCustomerById(1L).getFullName());
        customerManager.getCustomerById(1L).setFullName("zmena");

        assertEquals("name should be zmena", "zmena", customerManager.getCustomerById(1L).getFullName());

        assertEquals("email should be email@mail.sk", "email@mail.sk", customerManager.getCustomerById(1L).getEmail());
        customerManager.getCustomerById(1L).setEmail("mail@email.cz");
        customerManager.updateCustomer(customer);
        assertEquals("email should be email@mail.sk", "mail@email.cz", customerManager.getCustomerById(1L).getEmail());


        assertEquals("id should be 1L", 1L, customer.getId());
        customerManager.getCustomerById(1L).setId(2L);
        customerManager.updateCustomer(customer);
        assertEquals("id should be 2L", 2L, customer.getId());
    }

    @Test
    public void updateCustomerOnExisting() throws Exception {
        Customer customer = new Customer("neviem", "email@email.com");
        Customer customer1 = new Customer("tento", "email@mail.com");
        customer.setId(1L);
        customer1.setId(2L);

        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer1);
        
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