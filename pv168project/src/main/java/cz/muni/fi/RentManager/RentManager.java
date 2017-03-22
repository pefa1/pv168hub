package cz.muni.fi.RentManager;

import cz.muni.fi.Rent;

import java.util.List;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public interface RentManager {

    public Rent createRent(Rent rent);

    public void updateRent(Rent rent);

    public void deleteRent(long id);

    public List<Rent> listAllRents();

    public List<Rent> listRentsByCustomer(long customer_id);

    public List<Rent> listRentsByBook(long book_id);
}
