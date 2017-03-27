package cz.muni.fi;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Month;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentManagerImplTest {

    private RentManagerImpl rentManager;
    private CustomerManagerImpl customerManager;
    private BookManagerImpl bookManager;
    private DataSource ds;
    //private final static LocalDate NOW = LocalDate.of(2016, Month.FEBRUARY, 29);

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:rentmgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws Exception {
        rentManager = new RentManagerImpl();
        customerManager = new CustomerManagerImpl();
        bookManager = new BookManagerImpl();
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,RentManager.class.getResource("createTables.sql"));
        rentManager.setDataSource(ds);
        customerManager.setDataSource(ds);
        bookManager.setDataSource(ds);
    }

    @After
    public void tearDown() throws Exception {
        DBUtils.executeSqlScript(ds,RentManager.class.getResource("dropTables.sql"));
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

    private BookBuilder sampleBookBuilder() {
        return new BookBuilder()
                .author("Joe")
                .title("nevim");
    }

    private BookBuilder sample2BookBuilder() {
        return new BookBuilder()
                .author("James")
                .title("title");
    }

    private RentBuilder sampleRent() {
        return new RentBuilder()
                .id(null)
                .customer(null)
                .book(null)
                .rentTime(null)
                .expectedReturnTime(LocalDate.of(2017, Month.APRIL, 22))
                .returnTime(null);
    }

    @Test
    public void createRentWithWrongTime() throws Exception {
        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();
        customerManager.createCustomer(customer);
        bookManager.createBook(book);
        Rent rent = sampleRent().customer(customer).book(book).expectedReturnTime(LocalDate.of(2017,Month.FEBRUARY, 22)).build();
        try {
            rentManager.createRent(rent);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullRent() throws Exception {
        rentManager.createRent(null);
    }

    @Test
    public void createRentWithSameId() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();
        Book book = sampleBookBuilder().id(null).build();
        Book book1 =sample2BookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer1);
        bookManager.createBook(book);
        bookManager.createBook(book1);
        Rent rent = sampleRent().customer(customer).book(book).build();
        Rent rent1 = sampleRent().customer(customer1).book(book1).build();

        rentManager.createRent(rent);
        try {
            rent1.setId(rent.getId());
            rentManager.createRent(rent1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void createRentWithSameBook() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        customerManager.createCustomer(customer1);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        Rent rent1 = sampleRent().customer(customer1).book(book).build();

        rentManager.createRent(rent);
        try {
            rentManager.createRent(rent1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void createRent() throws Exception {
        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        assertThat(rent.getId()).isNotNull();
    }

    @Test
    public void getRentById() throws Exception {
        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        assertThat(rentManager.getRentById(rent.getId())).isNotNull();
        assertThat(rentManager.getRentById(rent.getId())).isEqualToComparingFieldByField(rent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRentByWrongId() throws Exception {
        rentManager.getRentById(-1L);
    }

    @Test
    public void updateRent() throws Exception {
        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        rent.setExpectedReturnTime(LocalDate.of(2017, Month.MAY, 22));
        rentManager.updateRent(rent);

        assertThat(rentManager.getRentById(rent.getId())).isEqualToComparingFieldByField(rent);
        assertThat(rentManager.getRentById(rent.getId()).getExpectedReturnTime()).isAfterOrEqualTo(rentManager.getRentById(rent.getId()).getExpectedReturnTime());

        rent.setReturnTime(LocalDate.of(2017, Month.MAY, 5));
        rentManager.updateRent(rent);

        assertThat(rentManager.getRentById(rent.getId())).isEqualToComparingFieldByField(rent);
        assertThat(rentManager.getRentById(rent.getId()).getReturnTime()).isBeforeOrEqualTo(rentManager.getRentById(rent.getId()).getExpectedReturnTime());

        rent.setReturnTime(LocalDate.of(2017, Month.MAY, 28));
        rentManager.updateRent(rent);

        assertThat(rentManager.getRentById(rent.getId())).isEqualToComparingFieldByField(rent);
        assertThat(rentManager.getRentById(rent.getId()).getReturnTime()).isAfterOrEqualTo(rentManager.getRentById(rent.getId()).getExpectedReturnTime());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullRent() throws Exception {
        rentManager.updateRent(null);
    }

    @Test
    public void updateRentOnExisting() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer);

        Book book = sampleBookBuilder().id(null).build();
        Book book1 = sample2BookBuilder().id(null).build();
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        Rent rent1 = sampleRent().customer(customer1).book(book1).build();

        rentManager.createRent(rent);
        rentManager.createRent(rent1);

        try {
            rent1.setBook(book);
            rentManager.updateRent(rent1);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNonExistingRent() throws Exception {
        rentManager.deleteRent(-1L);
    }

    @Test
    public void deleteOnlyRent() throws Exception {
        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        rentManager.deleteRent(rent.getId());

        try {
            rentManager.getRentById(rent.getId());
        } catch (IllegalArgumentException ex) {
            Assertions.fail("rent should not exist" + ex);
        }
    }

    @Test
    public void deleteRent() throws Exception {
        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer);

        Book book = sampleBookBuilder().id(null).build();
        Book book1 = sample2BookBuilder().id(null).build();
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        Rent rent1 = sampleRent().customer(customer1).book(book1).build();

        rentManager.createRent(rent);
        rentManager.createRent(rent1);

        rentManager.deleteRent(rent.getId());

        try {
            rentManager.getRentById(rent.getId());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        assertThat(rentManager.getRentById(rent1.getId())).isEqualToComparingFieldByField(rent1);
    }

    @Test
    public void listAllRents() throws Exception {
        assertThat(rentManager.listAllRents()).isNullOrEmpty();

        Customer customer = sampleCustomer1().build();
        Customer customer1 = sampleCustomer2().build();
        customerManager.createCustomer(customer);

        Book book = sampleBookBuilder().id(null).build();
        Book book1 = sample2BookBuilder().id(null).build();
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        assertThat(rentManager.listAllRents()).usingFieldByFieldElementComparator().contains(rent);
        assertThat(rentManager.listAllRents().size()).isEqualTo(1);

        Rent rent1 = sampleRent().customer(customer1).book(book1).build();
        rentManager.createRent(rent);

        assertThat(rentManager.listAllRents()).usingFieldByFieldElementComparator().contains(rent, rent1);
        assertThat(rentManager.listAllRents().size()).isEqualTo(2);
    }

    @Test
    public void listRentsByCustomer() throws Exception {
        assertThat(rentManager.listAllRents()).isNullOrEmpty();

        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();
        Book book1 = sample2BookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);
        bookManager.createBook(book1);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        assertThat(rentManager.listRentsByCustomer(rent.getCustomer().getId()).size()).isEqualTo(1);
        assertThat(rentManager.listRentsByCustomer(rent.getCustomer().getId())).usingFieldByFieldElementComparator().contains(rent);

        Rent rent1 = sampleRent().customer(customer).book(book1).build();
        rentManager.createRent(rent1);

        assertThat(rentManager.listRentsByCustomer(rent1.getCustomer().getId()).size()).isEqualTo(2);
        assertThat(rentManager.listRentsByCustomer(rent1.getCustomer().getId())).usingFieldByFieldElementComparator().contains(rent, rent1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listRentsByWrongIdCustomer() throws Exception {
        rentManager.listRentsByCustomer(-1L);
    }

    @Test
    public void listRentsByBook() throws Exception {
        assertThat(rentManager.listAllRents()).isNullOrEmpty();

        Customer customer = sampleCustomer1().build();
        Book book = sampleBookBuilder().id(null).build();

        customerManager.createCustomer(customer);
        bookManager.createBook(book);

        Rent rent = sampleRent().customer(customer).book(book).build();
        rentManager.createRent(rent);

        assertThat(rentManager.listRentsByBook(rent.getBook().getId())).usingFieldByFieldElementComparator().contains(rent);
        assertThat(rentManager.listRentsByBook(rent.getBook().getId()).size()).isEqualTo(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listRentsByWrongIdBook() throws Exception {
        rentManager.listRentsByBook(-1L);
    }
}