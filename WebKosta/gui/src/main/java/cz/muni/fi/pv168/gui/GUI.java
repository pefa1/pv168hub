package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by pefa1 on 3.5.2017.
 */
public class GUI {

    private JPanel panel1, Customer, Rent, Book;
    private JButton addRentButton, deleteRentButton, updateRentButton, returnBookButton;
    private JButton addCustomerButton, deleteCustomerButton, updateCustomerButton;
    private JButton addBookButton, deleteBookButton, updateBookButton;
    private JTable table1, table2, table3;
    private final static Logger log = LoggerFactory.getLogger(GUI.class);

    public GUI() {
        addRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        deleteRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        updateRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        updateCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        updateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater( ()-> { // zde použito funcionální rozhraní
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setTitle("GUI App");
                    frame.setVisible(true);
                }
        );
        prepareDB();

    }

    private static void prepareDB() {
        log.info("gui aplikace inicializována");
        DataSource dataSource = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.exit(1);
        }
        try {
            dataSource = Main.createMemoryDatabase();
            DBUtils.tryCreateTables(dataSource, Main.class.getResource("createTables.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        RentManagerImpl rentManager = new RentManagerImpl();
        rentManager.setDataSource(dataSource);

        CustomerManagerImpl customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(dataSource);

        BookManagerImpl bookManager = new BookManagerImpl();
        bookManager.setDataSource(dataSource);
        log.info("vytvořeny manažery a uloženy do atributů gui");
    }
}
