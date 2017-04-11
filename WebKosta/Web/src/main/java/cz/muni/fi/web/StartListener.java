package cz.muni.fi.web;

import cz.muni.fi.bl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class StartListener implements ServletContextListener {


    private final static Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializov�na");
        ServletContext servletContext = ev.getServletContext();
        DataSource dataSource = Main.createMemoryDatabase();

        CustomerManagerImpl customerManager = new CustomerManagerImpl();

        customerManager.setDataSource(dataSource);
        servletContext.setAttribute("customerManager", customerManager);
        BookManagerImpl bookManager = new BookManagerImpl();

        bookManager.setDataSource(dataSource);
        servletContext.setAttribute("bookManager", bookManager);
        RentManagerImpl rentManager = new RentManagerImpl();
        rentManager.setDataSource(dataSource);
        servletContext.setAttribute("rentManager", rentManager);

        log.info("vytvo�eny mana�ery a ulo�eny do atribut� servletContextu");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace kon��");
    }
}