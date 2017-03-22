package cz.muni.fi.RentManager;

import cz.muni.fi.Book;
import cz.muni.fi.Customer;
import cz.muni.fi.Rent;

import java.util.Date;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentBuilder {

    private long id;
    private Customer customer;
    private Book book;
    private Date rentTime;
    private Date expectedReturnTime;
    private Date returnTime;

    public RentBuilder id(long id) {
        this.id = id;
        return this;
    }

    public RentBuilder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public RentBuilder book(Book book) {
        this.book = book;
        return this;
    }

    public RentBuilder rentTime(Date rentTime) {
        this.rentTime = rentTime;
        return this;
    }

    public RentBuilder expectedReturnTime(Date expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
        return this;
    }

    public RentBuilder returnTime(Date returnTime) {
        this.returnTime = returnTime;
        return this;
    }

    public Rent build() {
        Rent rent = new Rent();
        rent.setId(id);
        rent.setCustomer(customer);
        rent.setBook(book);
        rent.setRentTime(rentTime);
        rent.setExpectedReturnTime(expectedReturnTime);
        rent.setReturnTime(returnTime);
        return rent;
    }
}
