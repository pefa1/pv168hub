package cz.muni.fi;


import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public interface RentManager {

    Rent createRent(Rent rent);

    void updateRent(Rent rent);

    void deleteRent(Long id);

    Rent getRentById(Long id);

    List<Rent> listAllRents();

    List<Rent> listRentsByCustomer(Long customer_id);

    List<Rent> listRentsByBook(Long book_id);

    void setDataSource(DataSource ds);
}
