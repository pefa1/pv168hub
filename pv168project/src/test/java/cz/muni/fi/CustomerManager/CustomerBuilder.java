package cz.muni.fi.CustomerManager;


import cz.muni.fi.Customer;

/**
 * Created by Marek Pfliegler on 15.3.2017.
 */
public class CustomerBuilder {

    private long id;
    private String fullName;
    private String email;

    public CustomerBuilder id(long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public CustomerBuilder email(String email) {
        this.email = email;
        return this;
    }

    public Customer build(){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFullName(fullName);
        customer.setEmail(email);
        return customer;
    }
}
