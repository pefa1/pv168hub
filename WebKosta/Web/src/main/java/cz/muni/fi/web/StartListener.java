package cz.muni.fi.web;

import cz.muni.fi.bl.BookManager;
import cz.muni.fi.bl.BookManagerImpl;
import cz.muni.fi.bl.CustomerManagerImpl;
import cz.muni.fi.bl.DBUtils;
import cz.muni.fi.bl.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.SQLException;

@WebListener
public class StartListener implements ServletContextListener {


    private final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        DataSource dataSource = Main.createMemoryDatabase();

        CustomerManagerImpl customerManager = new CustomerManagerImpl();

        customerManager.setDataSource(dataSource);
        servletContext.setAttribute("customerManager", customerManager);
        BookManagerImpl bookManager = new BookManagerImpl();

        bookManager.setDataSource(dataSource);
        servletContext.setAttribute("bookManager", bookManager);

        log.info("vytvoøeny manažery a uloženy do atributù servletContextu");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace konèí");
    }
}