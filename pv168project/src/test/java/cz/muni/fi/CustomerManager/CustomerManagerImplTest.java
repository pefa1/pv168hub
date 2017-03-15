package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
//import static org.assertj.core.api.Assertions.*;

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

    @Test
    public void createNullCustomer() throws Exception {
        Customer customer = new Customer("full name", "email@email.com");
        customer.setId(2L);

        customerManager.createCustomer(customer);

        try {
            customerManager.createCustomer(null);
        } catch (IllegalArgumentException ex) {
            fail("creating null customer" + ex);
        }
    }

    @Test
    public void createSameCustomer() throws Exception {
        Customer customer = new Customer("trampam", "email@mail.com");
        Customer customer1 = new Customer("trampam", "email@mail.com");

        customer.setId(1L);
        customer1.setId(1L);

        assertTrue("customer and customer1 are the same", customer.equals(customer1));

        customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be created" + ex);
        }

        customer1.setFullName("tento");

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be created" + ex);
        }

        customer1.setFullName("trampam*");
        customer1.setEmail("mail@mail.com");

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be created" + ex);
        }

        customer1.setEmail("email@email.com");
        customer1.setId(2L);

        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be created" + ex);
        }
    }

    @Test
    public void createCustomer() throws Exception {
        Customer customer = new Customer("bam", "email@email.com");

        customer.setId(0L);
        customerManager.createCustomer(customer);
        assertNotNull("customer is null", customerManager.getCustomerById(0L));

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
    public void getCustomerById() throws Exception {
        Customer customer = new Customer("tuc", "email@email.com");
        customer.setId(0L);

        customerManager.createCustomer(customer);

        assertEquals("customer should exist", customer, customerManager.getCustomerById(0L));
        assertSame("customer should exist", customer, customerManager.getCustomerById(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingCustomerById() throws Exception {
        customerManager.getCustomerById(0L);
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

        assertNotEquals("customer and customer1 are not the same", customerManager.getCustomerById(1L), customerManager.getCustomerById(2L));

        try {
            customer1.setId(1L);
            customerManager.updateCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be updated" + ex);
        }

        try {
            customer1.setFullName("neviem");
            customerManager.updateCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be updated" + ex);
        }

        try {
            customer1.setEmail("email@email.com");
            customerManager.updateCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            fail("customer1 should not be updated" + ex);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() throws Exception {
        customerManager.updateCustomer(null);

    }

    @Test
    public void updateNonExistingCustomer() throws Exception {
        Customer customer = new Customer("fullName", "email@email.com");
        customer.setId(2L);

        try {
            customerManager.updateCustomer(customer);
        } catch (IllegalArgumentException ex) {
            fail("updating non-existing customer" + ex);
        }

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

        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer1);

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