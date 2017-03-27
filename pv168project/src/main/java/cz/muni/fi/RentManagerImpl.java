package cz.muni.fi;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentManagerImpl implements RentManager {

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
    public Rent createRent(Rent rent) {
        return null;
    }

    @Override
    public void updateRent(Rent rent) {

    }

    @Override
    public void deleteRent(Long id) {

    }

    @Override
    public Rent getRentById(Long id) {
        return null;
    }

    @Override
    public List<Rent> listAllRents() {
        return null;
    }

    @Override
    public List<Rent> listRentsByCustomer(Long customer_id) {
        return null;
    }

    @Override
    public List<Rent> listRentsByBook(Long book_id) {
        return null;
    }
}
