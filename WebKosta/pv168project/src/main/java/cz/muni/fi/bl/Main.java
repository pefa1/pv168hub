package cz.muni.fi.bl;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public class Main {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
        ds.setDatabaseName("memory:book-rents");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        try{
            DBUtils.tryCreateTables(ds,BookManager.class.getResource("createTables.sql"));

        } catch (SQLException e){

        }
        return ds;
    }

    public static void main(String[] args) throws IllegalEntityException, IllegalArgumentException {

    }
}
