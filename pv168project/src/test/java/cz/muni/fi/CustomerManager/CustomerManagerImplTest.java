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

    private CustomerBuilder sampleCustomer1() {
        return new CustomerBuilder()
                .id(0L) //then change to null, it will automatically change in createCustomer method
                .email("mail@mail.com")
                .fullName("full name");
    }

    private CustomerBuilder sampleCustomer2() {
        return new CustomerBuilder()
                .id(1L) //then change to null, it will automatically change in createCustomer method
                .email("email@email.com")
                .fullName("full name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() throws Exception {
        customerManager.createCustomer(null);
    }

    @Test
    public void createSameCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();

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
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);
        assertNotNull("customer is null", result);

        Customer resultGBI = customerManager.getCustomerById(result.getId());
        assertEquals("customer is not created correctly", result, resultGBI);
    }

    @Test
    public void getCustomerById() throws Exception {
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);

        assertEquals("customer should exist", customer, result);
        assertSame("customer should exist", customer, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingCustomerById() throws Exception {
        customerManager.getCustomerById(-1L);
    }

    @Test
    public void updateCustomer() throws Exception {
        Customer customer = sampleCustomer2().build();
        Customer result = customerManager.createCustomer(customer);

        customerManager.getCustomerById(result.getId()).setFullName("zmena");
        assertEquals("name should be zmena", "zmena", customerManager.getCustomerById(result.getId()).getFullName());

        customerManager.getCustomerById(result.getId()).setEmail("mail@mail.sk");
        customerManager.updateCustomer(customer);
        assertEquals("email should be mail@mail.sk", "mail@mail.sk", customerManager.getCustomerById(result.getId()).getEmail());
    }

    @Test
    public void updateCustomerOnExisting() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertNotEquals("customer and customer1 are not the same", customerManager.getCustomerById(result.getId()), customerManager.getCustomerById(result1.getId()));

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
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);

        customerManager.deleteCustomer(result.getId());

        try {
            customerManager.getCustomerById(result.getId());
        } catch (IllegalArgumentException ex) {
            fail("customer with that id does not exist");
        }
    }

    @Test
    public void deleteCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertEquals("customer should exist", customer, customerManager.getCustomerById(result.getId()));
        assertEquals("customer1 should exist", customer1, customerManager.getCustomerById(result1.getId()));
        assertNotEquals("customer is not the same as customer1", customerManager.getCustomerById(result.getId()), customerManager.getCustomerById(result1.getId()));

        customerManager.deleteCustomer(result.getId());

        try {
            customerManager.getCustomerById(result.getId());
        } catch (IllegalArgumentException ex) {
            fail("customer with that id does not exist");
        }

        assertNotNull(customerManager);
        assertEquals("customer1 should exist", customer1, customerManager.getCustomerById(result1.getId()));
        assertSame("customer1 should exist", customer1, customerManager.getCustomerById(result1.getId()));
    }

    @Test
    public void listAllCustomers() throws Exception {
        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);

        assertThat("customer should be in the set", customerManager.listAllCustomers(), hasItem(customer));

        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer1);

        assertEquals("list has different size", 2, customerManager.listAllCustomers().size());
        assertThat("list should have both customers", customerManager.listAllCustomers(), hasItems(customer,customer1));
    }

}