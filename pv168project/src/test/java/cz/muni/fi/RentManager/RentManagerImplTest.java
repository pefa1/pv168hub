package cz.muni.fi.RentManager;

import cz.muni.fi.Rent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Marek Pfliegler on 22.3.2017.
 */
public class RentManagerImplTest {

    private RentManager rentManager;
    @Before
    public void setUp() throws Exception {
        rentManager = new RentManagerImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createRent() throws Exception {
        Rent rent;
    }

    @Test
    public void updateRent() throws Exception {

    }

    @Test
    public void deleteRent() throws Exception {

    }

    @Test
    public void listAllRents() throws Exception {

    }

    @Test
    public void listRentsByCustomer() throws Exception {

    }

    @Test
    public void listRentsByBook() throws Exception {

    }

}