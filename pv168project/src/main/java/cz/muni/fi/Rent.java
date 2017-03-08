package cz.muni.fi;

import java.util.Date;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public class Rent {
    private int id;
    private Customer customer;
    private Book book;
    private Date rentTime;
    private Date expectedReturnTime;
    private Date returnTime;

    public Rent(int id, Customer customer, Book book, Date rentTime, Date expectedReturnTime, Date returnTime) {
        this.id = id;
        this.customer = customer;
        this.book = book;
        this.rentTime = rentTime;
        this.expectedReturnTime = expectedReturnTime;
        this.returnTime = returnTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getRentTime() {
        return rentTime;
    }

    public void setRentTime(Date rentTime) {
        this.rentTime = rentTime;
    }

    public Date getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(Date expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rent rent = (Rent) o;

        if (id != rent.id) return false;
        if (customer != null ? !customer.equals(rent.customer) : rent.customer != null) return false;
        if (book != null ? !book.equals(rent.book) : rent.book != null) return false;
        if (rentTime != null ? !rentTime.equals(rent.rentTime) : rent.rentTime != null) return false;
        if (expectedReturnTime != null ? !expectedReturnTime.equals(rent.expectedReturnTime) : rent.expectedReturnTime != null)
            return false;
        return returnTime != null ? returnTime.equals(rent.returnTime) : rent.returnTime == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (book != null ? book.hashCode() : 0);
        result = 31 * result + (rentTime != null ? rentTime.hashCode() : 0);
        result = 31 * result + (expectedReturnTime != null ? expectedReturnTime.hashCode() : 0);
        result = 31 * result + (returnTime != null ? returnTime.hashCode() : 0);
        return result;
    }
}
