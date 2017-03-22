package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.assertj.core.api.Assertions.*;


/**
 * Created by Marek Pfliegler on 8.1.2017.
 * Tests on class CustomerManagerImpl
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

    /**
     * builder for a customer with default id, email and name
     * @return customer
     */
    private CustomerBuilder sampleCustomer1() {
        return new CustomerBuilder()
                .id(0L) //then change to null, it will automatically change in createCustomer method
                .email("mail@mail.com") //then change to null, it will automatically change in createCustomer method
                .fullName("full name"); //then change to null, it will automatically change in createCustomer method
    }

    /**
     * builder for a customer with different default id, email and name than sampleCustomer1
     * @return customer
     */
    private CustomerBuilder sampleCustomer2() {
        return new CustomerBuilder()
                .id(1L) //then change to null, it will automatically change in createCustomer method
                .email("email@email.com") //then change to null, it will automatically change in createCustomer method
                .fullName("name"); //then change to null, it will automatically change in createCustomer method
    }

    /**
     * creates null customer, should throw exception
     * @throws Exception customer should not be created
     */
    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() throws Exception {
        customerManager.createCustomer(null);
    }

    /**
     * creates 2 customers with same email, second should throw exception
     * @throws Exception customer1 should not be created
     */
    @Test
    public void createSameEmailCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().email("mail@mail.com").build();

        assertThat(customer.getId()).isNotEqualTo(customer1.getId());

        customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            Assertions.fail("customers have same email" + ex);
        }
    }

    /**
     * customer should be created correctly
     * @throws Exception exception should not be thrown
     */
    @Test
    public void createCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);
        assertThat(result).isNotNull();

        Customer resultGBI = customerManager.getCustomerById(result.getId());
        assertThat(result).isEqualToComparingFieldByField(resultGBI);
    }

    @Test
    public void createSameIdCustomer() throws Exception {
        Customer customer1 = sampleCustomer1().build();
        Customer customer2 = sampleCustomer2().id(0L).build();

        customerManager.createCustomer(customer1);
        try {
            customerManager.createCustomer(customer2);
        } catch (IllegalArgumentException ex) {
            Assertions.fail("customers have same id" + ex);
        }
    }

    /**
     * test for searching a customer by his id correctly
     * @throws Exception exception should not be thrown
     */
    @Test
    public void getCustomerById() throws Exception {
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);

        assertThat(customerManager.getCustomerById(result.getId())).isEqualToComparingFieldByField(result);
    }

    /**
     * searching for a not existing customer, for example negative id
     * @throws Exception should throw exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingCustomerById() throws Exception {
        customerManager.getCustomerById(-1L);
    }

    /**
     * correctly updates customer's name and email
     * @throws Exception exception should not be thrown
     */
    @Test
    public void updateCustomer() throws Exception {
        Customer customer = sampleCustomer2().build();
        Customer result = customerManager.createCustomer(customer);

        customerManager.getCustomerById(result.getId()).setFullName("zmena");
        assertThat(customerManager.getCustomerById(result.getId()).getFullName()).isEqualTo("zmena");

        customerManager.getCustomerById(result.getId()).setEmail("mail@mail.sk");
        customerManager.updateCustomer(customer);
        assertThat(customerManager.getCustomerById(result.getId()).getFullName()).isEqualTo("mail@mail.sk");
    }

    /**
     * updates customer's email on already existing email
     * @throws Exception exception should be thrown
     */
    @Test
    public void updateCustomerOnExisting() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertThat(customerManager.getCustomerById(result.getId())).isNotEqualTo(customerManager.getCustomerById(result1.getId()));

        try {
            customer1.setEmail("mail@mail.com");
            customerManager.updateCustomer(customer1);
        } catch (IllegalArgumentException ex) {
            Assertions.fail("customer with that email is existing, you cannot update on it" + ex);
        }
    }

    /**
     * tries to update null customer
     * @throws Exception exception should be thrown
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() throws Exception {
        customerManager.updateCustomer(null);
    }

    /**
     * deletes only customer
     * @throws Exception exception should be thrown, when it tries to find the customer
     */
    @Test
    public void deleteOnlyCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);

        customerManager.deleteCustomer(result.getId());

        assertThat(customerManager).isNull();

        try {
            customerManager.getCustomerById(result.getId());
        } catch (IllegalArgumentException ex) {
            Assertions.fail("customer should be deleted" + ex);
        }
    }

    /**
     * deletes one of two customers
     * @throws Exception exception should be thrown, when it tries to find the customer
     */
    @Test
    public void deleteCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();

        Customer result = customerManager.createCustomer(customer);
        Customer result1 = customerManager.createCustomer(customer1);

        assertThat(customer).isEqualToComparingFieldByField(customerManager.getCustomerById(result.getId()));
        assertThat(customer1).isEqualToComparingFieldByField(customerManager.getCustomerById(result1.getId()));
        assertThat(customerManager.getCustomerById(result.getId())).isNotEqualTo(customerManager.getCustomerById(result.getId()));

        customerManager.deleteCustomer(result.getId());

        assertThat(customerManager).isNotNull();
        assertThat(customer1).isEqualToComparingFieldByField(customerManager.getCustomerById(result1.getId()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCustomerByWrongId() throws Exception {
        customerManager.deleteCustomer(-1L);
    }

    /**
     * test whether the customers were correctly put into list
     * @throws Exception exception should not be thrown
     */
    @Test
    public void listAllCustomers() throws Exception {
        assertThat(customerManager.listAllCustomers()).isEmpty();

        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);

        assertThat(customerManager.listAllCustomers()).usingFieldByFieldElementComparator().containsOnly(customer);

        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer1);

        assertThat(customerManager.listAllCustomers().size()).isEqualTo(2);
        assertThat(customerManager.listAllCustomers()).usingFieldByFieldElementComparator().containsOnly(customer, customer1);
    }

}