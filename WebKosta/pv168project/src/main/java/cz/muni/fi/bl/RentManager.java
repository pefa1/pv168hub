package cz.muni.fi.bl;


import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public interface RentManager {

    void createRent(Rent rent);

    void updateRent(Long id, LocalDate newExpectedReturnTime);

    void deleteRent(Long id);

    Rent getRentById(Long id);

    List<Rent> listAllRents();

    List<Rent> listRentsByCustomer(Long customer_id);

    List<Rent> listRentsByBook(Long book_id);

    void setDataSource(DataSource ds);

    Book bookOfRent(Rent rent);

    Customer customerOfRent(Rent rent);

    boolean ReturnBook(Rent rent);

}
