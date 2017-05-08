package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.xml.bind.*;
import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pefa1 on 3.5.2017.
 */
public class GUI {

    private JPanel panel1, Customer, Rent, Book;
    private JButton addRentButton, deleteRentButton, updateRentButton, returnBookButton;
    private JButton addCustomerButton, deleteCustomerButton, updateCustomerButton;
    private JButton addBookButton, deleteBookButton, updateBookButton;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private BookManagerImpl bookManager;
    private RentManagerImpl rentManager;
    private CustomerManagerImpl customerManager;
    private BookTableModel bookModel;
    private CustomerTableModel customerModel;
    private RentTableModel rentModel;
    private DataSource ds;
    private final static Logger log = LoggerFactory.getLogger(GUI.class);

    public GUI() {
        addRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookTableModel availableBooks = new BookTableModel();
                availableBooks.addBooks(rentManager.listAvailableBooks(bookManager.listAllBooks()));
                JTable books = new JTable(availableBooks);

                JTable customers = new JTable(customerModel);
                JTextField expectedReturnTime = new JTextField();

                Object[] message = {
                        "Available books:", books,
                        "Customers: ", customers,
                        "Expected return time: ", expectedReturnTime
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Add rent", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    Rent rent = new Rent();
                    rent.setBook(bookManager.getBookById((Long) books.getValueAt(books.getSelectedRow(), 0)));
                    rent.setCustomer(customerManager.getCustomerById((Long) customers.getValueAt(customers.getSelectedRow(), 0)));
                    rent.setExpectedReturnTime(LocalDate.parse(expectedReturnTime.getText()));
                    rentManager.createRent(rent);
                    rentModel.addRents(rentManager.listAllRents());
                } else {

                }
            }
        });

        deleteRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rentManager.deleteRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));
                rentModel.addRents(rentManager.listAllRents());
            }
        });

        updateRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Rent rent = rentManager.getRentById((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));

                JTextField expectedReturnTime = new JTextField(rent.getExpectedReturnTime().toString());

                Object[] message = {
                        "Expected return time: ", expectedReturnTime
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Add rent", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    rentManager.updateRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0), LocalDate.parse(expectedReturnTime.getText()));
                    rentModel.addRents(rentManager.listAllRents());
                } else {

                }
            }
        });

        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rentManager.ReturnBook(rentManager.getRentById((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0)));
                rentModel.addRents(rentManager.listAllRents());
            }
        });

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField fullName = new JTextField();
                JTextField email = new JTextField();
                Object[] message = {
                        "FullName:", fullName,
                        "Email:", email
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Add customer", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    Customer customer = new Customer();
                    customer.setFullName(fullName.getText());
                    customer.setEmail(email.getText());
                    try {
                        customerManager.createCustomer(customer);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (ValidationException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        customerModel.addCustomers(customerManager.listAllCustomers());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } else {

                }
            }
        });

        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerManager.deleteCustomer((Long) table2.getModel().getValueAt(table2.getSelectedRow(), 0));
                try {
                    customerModel.addCustomers(customerManager.listAllCustomers());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        updateCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer customer = customerManager.getCustomerById((Long) table2.getModel().getValueAt(table2.getSelectedRow(), 0));
                JTextField fullName = new JTextField(customer.getFullName());
                JTextField email = new JTextField(customer.getEmail());
                Object[] message = {
                        "FullName:", fullName,
                        "Email:", email
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update customer", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    customer.setFullName(fullName.getText());
                    customer.setEmail(email.getText());
                    try {
                        customerManager.updateCustomer(customer);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (ValidationException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        customerModel.addCustomers(customerManager.listAllCustomers());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                } else {

                }
            }
        });

        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField author = new JTextField();
                JTextField title = new JTextField();
                Object[] message = {
                        "Author:", author,
                        "Title:", title
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Add book", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    Book book = new Book();
                    book.setAuthor(author.getText());
                    book.setTitle(title.getText());
                    bookManager.createBook(book);
                    bookModel.addBooks(bookManager.listAllBooks());
                } else {

                }
            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookManager.deleteBook((Long) table1.getModel().getValueAt(table1.getSelectedRow(), 0));
                bookModel.addBooks(bookManager.listAllBooks());
            }
        });

        updateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Book book = bookManager.getBookById((Long) table1.getModel().getValueAt(table1.getSelectedRow(), 0));
                JTextField author = new JTextField(book.getAuthor());
                JTextField title = new JTextField(book.getTitle());
                Object[] message = {
                        "Author:", author,
                        "Title:", title
                };

                int option = JOptionPane.showConfirmDialog(null, message, "Update book", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    book.setAuthor(author.getText());
                    book.setTitle(title.getText());
                    bookManager.updateBook(book);
                    bookModel.addBooks(bookManager.listAllBooks());
                } else {

                }
            }

        });

    }

    public static void main(String[] args) {
        EventQueue.invokeLater( ()-> { // zde použito funcionální rozhraní
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            frame.setContentPane(new GUI().panel1);
            frame.setTitle("GUI App");
            frame.setPreferredSize(new Dimension(800,600));

            frame.pack();
            frame.setVisible(true);
        });
    }

    private void prepareDB() {
        log.info("gui aplikace inicializována");
        ds = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            System.exit(1);
        }
        try {
            ds = Main.createMemoryDatabase();
            DBUtils.tryCreateTables(ds, Main.class.getResource("createTables.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rentManager = new RentManagerImpl();
        rentManager.setDataSource(ds);

        customerManager = new CustomerManagerImpl();
        customerManager.setDataSource(ds);

        try{
            customerManager.createCustomer(sampleCustomer1().build());
            customerManager.createCustomer(sampleCustomer2().build());
        } catch (javax.xml.bind.ValidationException e){

        } catch (SQLException e){

        }

        bookManager = new BookManagerImpl();
        bookManager.setDataSource(ds);

        bookManager.createBook(sampleBookBuilder().build());
        bookManager.createBook(sample2BookBuilder().build());

        log.info("vytvořeny manažery a uloženy do atributů gui");
    }

    private void createUIComponents() {
        prepareDB();

        bookModel = new BookTableModel();
        bookModel.addBooks(bookManager.listAllBooks());
        table1 = new JTable(bookModel);
        table1.removeColumn(table1.getColumnModel().getColumn(0));

        customerModel = new CustomerTableModel();
        try{
            customerModel.addCustomers(customerManager.listAllCustomers());
        } catch (SQLException e){
            e.printStackTrace();
        }
        table2 = new JTable(customerModel);
        table2.removeColumn(table2.getColumnModel().getColumn(0));

        rentModel = new RentTableModel();
        rentModel.addRents(rentManager.listAllRents());
        table3 = new JTable(rentModel);
        table3.removeColumn(table3.getColumnModel().getColumn(0));
        table3.removeColumn(table3.getColumnModel().getColumn(0));
        table3.removeColumn(table3.getColumnModel().getColumn(2));
    }

    private BookBuilder sampleBookBuilder() {
        return new BookBuilder()
                .author("Joe")
                .title("nevim");
    }

    private BookBuilder sample2BookBuilder() {
        return new BookBuilder()
                .author("James")
                .title("title");
    }


    /**
     * builder for a customer with default id, email and name
     * @return customer
     */
    private CustomerBuilder sampleCustomer1() {
        return new CustomerBuilder()
                .id(null) //then change to null, it will automatically change in createCustomer method
                .email("mail@mail.com") //then change to null, it will automatically change in createCustomer method
                .fullName("full name"); //then change to null, it will automatically change in createCustomer method
    }

    /**
     * builder for a customer with different default id, email and name than sampleCustomer1
     * @return customer
     */
    private CustomerBuilder sampleCustomer2() {
        return new CustomerBuilder()
                .id(null) //then change to null, it will automatically change in createCustomer method
                .email("email@email.com") //then change to null, it will automatically change in createCustomer method
                .fullName("name"); //then change to null, it will automatically change in createCustomer method
    }
}
