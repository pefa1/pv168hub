package cz.muni.fi;

import java.util.Date;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public class Customer {
    private long id;
    private String fullName;
    private String email;
    private Date dateOfBirth;

    public Customer(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (fullName != null ? !fullName.equals(customer.fullName) : customer.fullName != null) return false;
        if (email != null ? !email.equals(customer.email) : customer.email != null) return false;
        return dateOfBirth != null ? dateOfBirth.equals(customer.dateOfBirth) : customer.dateOfBirth == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        return result;
    }
}
