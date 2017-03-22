package cz.muni.fi.CustomerManager;

import cz.muni.fi.Customer;

import javax.sql.DataSource;
import java.util.List;


/**
 * Created by Marek Pfliegler on 8.3.2017.
 * implementation of CustomerManager interface
 */
public class CustomerManagerImpl implements CustomerManager{

    private DataSource dataSource;

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return null;
    }

    @Override
    public void updateCustomer(Customer customer) {

    }

    @Override
    public void deleteCustomer(long id) {

    }

    @Override
    public List<Customer> listAllCustomers() {
        return null;
    }

    @Override
    public Customer getCustomerById(long id) {
        return null;
    }
}
