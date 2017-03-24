package cz.muni.fi;

import java.time.LocalDate;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public class Rent {
    private Long id;
    private Customer customer;
    private Book book;
    private LocalDate rentTime;
    private LocalDate expectedReturnTime;
    private LocalDate returnTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDate getRentTime() {
        return rentTime;
    }

    public void setRentTime(LocalDate rentTime) {
        this.rentTime = rentTime;
    }

    public LocalDate getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(LocalDate expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public LocalDate getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDate returnTime) {
        this.returnTime = returnTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rent rent = (Rent) o;

        if (id != null ? !id.equals(rent.id) : rent.id != null) return false;
        if (customer != null ? !customer.equals(rent.customer) : rent.customer != null) return false;
        if (book != null ? !book.equals(rent.book) : rent.book != null) return false;
        if (rentTime != null ? !rentTime.equals(rent.rentTime) : rent.rentTime != null) return false;
        if (expectedReturnTime != null ? !expectedReturnTime.equals(rent.expectedReturnTime) : rent.expectedReturnTime != null)
            return false;
        return returnTime != null ? returnTime.equals(rent.returnTime) : rent.returnTime == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (customer != null ? customer.hashCode() : 0);
        result = 31 * result + (book != null ? book.hashCode() : 0);
        result = 31 * result + (rentTime != null ? rentTime.hashCode() : 0);
        result = 31 * result + (expectedReturnTime != null ? expectedReturnTime.hashCode() : 0);
        result = 31 * result + (returnTime != null ? returnTime.hashCode() : 0);
        return result;
    }
}
