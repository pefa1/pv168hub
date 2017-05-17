package cz.muni.fi.bl;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Marek Pfliegler on 8.3.2017.
 */
public class Main {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() throws IOException {
        /*EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
        ds.setDatabaseName("memory:book-rents");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        try{
            DBUtils.tryCreateTables(ds,BookManager.class.getResource("createTables.sql"));

        } catch (SQLException e){

        }
        return ds;*/

        Properties myconf = new Properties();
        myconf.load(Main.class.getResourceAsStream("db.properties"));

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));

        return ds;
    }

    public static void main(String[] args) throws IllegalEntityException, IllegalArgumentException {

    }
}
