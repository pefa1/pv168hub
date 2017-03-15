package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


/**
 * Created by Marek Pfliegler on 8.1.2017.
 */
public class CustomerManagerImplTest {

    private CustomerManager customerManager;

    @Before
    public void setUp() throws Exception {
        customerManager = new CustomerManagerImpl(); // medzi testami ostava iba to co je tu, pred kazdym testom sa spusti setup

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() throws Exception {
        customerManager.createCustomer(null);
    }

    @Test
    public void createSameCustomer() throws Exception {
        Customer customer = new Customer("trampam", "email@mail.com");
        Customer customer1 = new Customer("trampam", "email@mail.com");

        assertTrue("customer and customer1 are the same", customer.equals(customer1));

        customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 and customer have same email" + ex);
        }
    }

    @Test
    public void createCustomer() throws Exception {
        Customer customer = new Customer("bam", "email@email.com");
        customer.setId(0L);

        Customer result = customerManager.createCustomer(customer);
        assertNotNull("customer is null", result);

        Customer resultGBI = customerManager.getCustomerById(result.getId());
        assertEquals("customer is not created correctly", result, resultGBI);
    }

    @Test
    public void getCustomerById() throws Exception {
        Customer customer = new Customer("tuc", "email@email.com");
        customer.setId(0L);

        Customer result = customerManager.createCustomer(customer);

        assertEquals("customer should exist", customer, result);
        assertSame("customer should exist", customer, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingCustomerById() throws Exception {
        customerManager.getCustomerById(0L);
    }

    @Test
    public void updateCustomer() throws Exception {
        Customer customer = new Customer("blabla", "email@mail.sk");
        customer.setId(1L);
        Customer result = customerManager.createCustomer(customer);

        assertEquals("customer should be in the manager", customer, customerManager.getCustomerById(1L));

        assertEquals("name should be blabla", "blabla", customerManager.getCustomerById(1L).getFullName());
        customerManager.getCustomerById(1L).setFullName("zmena");

        assertEquals("name should be zmena", "zmena", customerManager.getCustomerById(1L).getFullName());

        assertEquals("email should be email@mail.sk", "email@mail.sk", customerManager.getCustomerById(1L).getEmail());
        customerManager.getCustomerById(1L).setEmail("mail@email.cz");
        customerManager.updateCustomer(customer);
        assertEquals("email should be email@mail.sk", "mail@email.cz", customerManager.getCustomerById(1L).getEmail());
    }

    @Test
    public void updateCustomerOnExisting() throws Exception {
        Customer customer = new Customer("neviem", "email@email.com");
        Customer customer1 = new Customer("tento", "mail@mail.com");
        customer.setId(1L);
        customer1.setId(2L);

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertNotEquals("customer and customer1 are not the same", customerManager.getCustomerById(1L), customerManager.getCustomerById(2L));

        try {
            customer1.setEmail("email@email.com");
            customerManager.updateCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 email should not be updated" + ex);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() throws Exception {
        customerManager.updateCustomer(null);

    }

    @Test
    public void deleteOnlyCustomer() throws Exception {
        Customer customer = new Customer("full name", "email@email.com");
        customer.setId(0L);

        customerManager.createCustomer(customer);
        assertNotNull("customer should exist", customerManager.getCustomerById(0L));

        customerManager.deleteCustomer(0L);

        try {
            customerManager.getCustomerById(0L);
        } catch (IllegalArgumentException ex) {
            fail("customer with that id does not exist");
        }
    }

    @Test
    public void deleteCustomer() throws Exception {
        Customer customer = new Customer("name", "mail@mail.com");
        Customer customer1 = new Customer("another name", "email@email.com");
        customer.setId(0L);
        customer1.setId(1L);

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertEquals("customer should exist", customer, customerManager.getCustomerById(0L));
        assertEquals("customer1 should exist", customer1, customerManager.getCustomerById(1L));
        assertNotEquals("customer is not the same as customer1", customerManager.getCustomerById(0L), customerManager.getCustomerById(1L));

        customerManager.deleteCustomer(0L);

        try {
            customerManager.getCustomerById(0L);
        } catch (IllegalArgumentException ex) {
            fail("customer with that id does not exist");
        }

        assertNotNull(customerManager);
        assertEquals("customer1 should exist", customer1, customerManager.getCustomerById(1L));
        assertSame("customer1 should exist", customer1, customerManager.getCustomerById(1L));
    }

    @Test
    public void listAllCustomers() throws Exception {
        Customer customer = new Customer("name", "email@email.com");
        customerManager.createCustomer(customer);

        assertThat("customer should be in the set", customerManager.listAllCustomers(), hasItem(customer));

        Customer customer1 = new Customer("full name", "mail@mail.com");
        customerManager.createCustomer(customer1);

        assertEquals("set has different size", 2, customerManager.listAllCustomers().size());
        assertThat("set should have both customers", customerManager.listAllCustomers(), hasItems(customer,customer1));

        customer.setId(0L);
        Customer customer2 = new Customer("name", "email@email.com");
        customer2.setId(0L);

        try {
            customerManager.createCustomer(customer2);
        } catch (IllegalArgumentException ex) {
            fail("customer2 should not be created");
        }

        assertThat("set should have just 2 customers", customerManager.listAllCustomers(), hasItems(customer, customer1));
    }

}