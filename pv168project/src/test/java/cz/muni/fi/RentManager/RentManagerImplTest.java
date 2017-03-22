package cz.muni.fi.RentManager;

import cz.muni.fi.BookManager.BookBuilder;
import cz.muni.fi.CustomerManager.CustomerBuilder;
import cz.muni.fi.Rent;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.time.Month;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentManagerImplTest {

    private RentManager rentManager;

    @Before
    public void setUp() throws Exception {
        rentManager = new RentManagerImpl();
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
                .id(0L)
                .customer(sampleCustomer1().build())
                .book(sampleBookBuilder().id(0L).build())
                .rentTime(LocalDate.of(2017, Month.MARCH, 22))
                .expectedReturnTime(LocalDate.of(2017, Month.APRIL, 22))
                .returnTime(null);
    }

    private RentBuilder sampleRent2() {
        return new RentBuilder()
                .id(1L)
                .customer(sampleCustomer2().build())
                .book(sample2BookBuilder().id(1L).build())
                .rentTime(LocalDate.of(2017, Month.MARCH, 22))
                .expectedReturnTime(LocalDate.of(2017, Month.APRIL, 22))
                .returnTime(null);
    }

    @Test
    public void createRentWithWrongTime() throws Exception {
        Rent rent = sampleRent().expectedReturnTime(LocalDate.of(2017,Month.FEBRUARY, 22)).build();
        try {
            rentManager.createRent(rent);
        } catch (IllegalArgumentException ex) {
            Assertions.fail("expected return time is before start time" + ex);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullRent() throws Exception {
        rentManager.createRent(null);
    }

    @Test
    public void createRent() throws Exception {
        Rent rent = sampleRent().build();

        assertThat(rent).isNotNull();

        Rent result = rentManager.createRent(rent);

        assertThat(result).isEqualToComparingFieldByField(rent);
    }

    @Test
    public void getRentById() throws Exception {

    }

    @Test
    public void updateRent() throws Exception {
        Rent rent = sampleRent().build();
        rentManager.createRent(rent);

        rent.setExpectedReturnTime(LocalDate.of(2017, Month.MAY, 22));
        rentManager.updateRent(rent);

        
    }

    @Test
    public void deleteRent() throws Exception {

    }

    @Test
    public void listAllRents() throws Exception {

    }

    @Test
    public void listRentsByCustomer() throws Exception {

    }

    @Test
    public void listRentsByBook() throws Exception {

    }

}