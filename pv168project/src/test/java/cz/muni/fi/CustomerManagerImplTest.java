package cz.muni.fi;

import java.sql.SQLException;

import static org.mockito.Mockito.*;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.sql.DataSource;
import javax.xml.bind.ValidationException;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by Marek Pfliegler on 8.1.2017.
 * Tests on class CustomerManagerImpl
 */
public class CustomerManagerImplTest {

    private CustomerManager customerManager;
    private DataSource ds;

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:customermgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws Exception {
        customerManager = new CustomerManagerImpl();
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,CustomerManager.class.getResource("createTables.sql"));
        customerManager.setDataSource(ds); // medzi testami ostava iba to co je tu, pred kazdym testom sa spusti setup
    }

    @After
    public void tearDown() throws Exception {
        DBUtils.executeSqlScript(ds,CustomerManager.class.getResource("dropTables.sql"));
    }

    /**
     * builder for a customer with default id, email and name
     * @return customer
     */
    private CustomerBuilder sampleCustomer1() {
        return new CustomerBuilder()
                .id(null) //then change to null, it will automatically change in createCustomer method
                .email("mail@mail.com") //then change to null, it will automatically change in createCustomer method
                .fullName("full name"); //then change to null, it will automatically change in createCustomer method
    }

    /**
     * builder for a customer with different default id, email and name than sampleCustomer1
     * @return customer
     */
    private CustomerBuilder sampleCustomer2() {
        return new CustomerBuilder()
                .id(null) //then change to null, it will automatically change in createCustomer method
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
     * customer should be created correctly
     * @throws Exception exception should not be thrown
     */
    @Test
    public void createCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();

        Customer result = customerManager.createCustomer(customer);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }

    /**
     * creates 2 customers with same email, second should throw exception
     * @throws Exception customer1 should not be created
     */
    @Test
    public void createSameEmailCustomer() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().email("mail@mail.com").build();

        assertThat(customer.getId()).isNull();
        assertThat(customer1.getId()).isNull();

        Customer result = customerManager.createCustomer(customer);
        try {
            customerManager.createCustomer(customer1);
        } catch (IllegalArgumentException ex) {
        }
        assertThat(result.getId()).isNotNull();
    }

    /**
     * tries to create customer with existing id
     * @throws Exception exception should be thrown
     */
    @Test
    public void createSameIdCustomer() throws Exception {
        Customer customer1 = sampleCustomer1().build();
        Customer customer2 = sampleCustomer2().build();

        Customer result = customerManager.createCustomer(customer1);
        try {
            customer2.setId(result.getId());
            customerManager.createCustomer(customer2);
        } catch (IllegalEntityException ex) {
        }
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
     * correctly updates customer's name and email
     * @throws Exception exception should not be thrown
     */
    @Test
    public void updateCustomer() throws Exception {
        Customer customer = sampleCustomer2().build();
        Customer result = customerManager.createCustomer(customer);

        Customer result1 = customerManager.getCustomerById(result.getId());
        result1.setFullName("zmena");
        customerManager.updateCustomer(result1);
        assertThat(customerManager.getCustomerById(result.getId()).getFullName()).isEqualTo("zmena");

        Customer result2 = customerManager.getCustomerById(result.getId());
        result2.setEmail("mail@mail.sk");
        customerManager.updateCustomer(result2);
        assertThat(customerManager.getCustomerById(result.getId()).getEmail()).isEqualTo("mail@mail.sk");
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

        try {
            customerManager.getCustomerById(result.getId());
        } catch (IllegalArgumentException ex) {
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
        assertThat(customerManager.getCustomerById(result1.getId())).isNotEqualTo(customerManager.getCustomerById(result.getId()));

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
        assertThat(customerManager.listAllCustomers()).isNullOrEmpty();

        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);

        assertThat(customerManager.listAllCustomers()).usingFieldByFieldElementComparator().containsOnly(customer);

        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer1);

        assertThat(customerManager.listAllCustomers().size()).isEqualTo(2);
        assertThat(customerManager.listAllCustomers()).usingFieldByFieldElementComparator().containsOnly(customer, customer1);
    }

    /*@Test
    public void createBodyWithSqlExceptionThrown() throws SQLException {
        // Create sqlException, which will be thrown by our DataSource mock
        // object to simulate DB operation failure
        SQLException sqlException = new SQLException();
        // Create DataSource mock object
        DataSource failingDataSource = mock(DataSource.class);
        // Instruct our DataSource mock object to throw our sqlException when
        // DataSource.getConnection() method is called.
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        // Configure our manager to use DataSource mock object
        customerManager.setDataSource(failingDataSource);

        // Create Body instance for our test
        Customer customer = sampleCustomer1().build();

        // Try to call Manager.createBody(Body) method and expect that exception
        // will be thrown
        assertThatThrownBy(() -> customerManager.createCustomer(customer))
                // Check that thrown exception is ServiceFailureException
                .isInstanceOf(ServiceFailureException.class)
                // Check if cause is properly set
                .hasCause(sqlException);
    }

    // Now we want to test also other methods of BodyManager. To avoid having
    // couple of method with lots of duplicit code, we will use the similar
    // approach as with testUpdateBody(Operation) method.

    @FunctionalInterface
    private interface Operation<T> {
        void callOn(T subjectOfOperation) throws SQLException;
    }

    private void testExpectedServiceFailureException(Operation<CustomerManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        customerManager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(customerManager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void updateBodyWithSqlExceptionThrown() throws SQLException, ValidationException {
        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);
        testExpectedServiceFailureException((customerManager) -> {
            try {
                customerManager.updateCustomer(customer);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void getBodyWithSqlExceptionThrown() throws SQLException, ValidationException {
        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);
        testExpectedServiceFailureException((customerManager) -> customerManager.getCustomerById(customer.getId()));
    }

    @Test
    public void deleteBodyWithSqlExceptionThrown() throws SQLException, ValidationException {
        Customer customer = sampleCustomer1().build();
        customerManager.createCustomer(customer);
        testExpectedServiceFailureException((bodyManager) -> bodyManager.deleteCustomer(customer.getId()));
    }

    @Test
    public void findAllBodiesWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException(CustomerManager::listAllCustomers);
    }*/
}