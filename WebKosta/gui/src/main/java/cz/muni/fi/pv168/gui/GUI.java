package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import javax.xml.bind.ValidationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by pefa1 on 3.5.2017.
 */
public class GUI {

    private Locale locale = Locale.forLanguageTag("cs-CS");
    private ResourceBundle bundle = ResourceBundle.getBundle("localization", locale);
    private JPanel panel1, Customer, Rent, Book;
    private JButton addRentButton, deleteRentButton, updateRentButton, returnBookButton;
    private JButton addCustomerButton, deleteCustomerButton, updateCustomerButton;
    private JButton addBookButton, deleteBookButton, updateBookButton;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private JTabbedPane tab;
    private JButton czLanguage;
    private JButton skLanguage;
    private JButton enLanguage;
    private BookManagerImpl bookManager;
    private RentManagerImpl rentManager;
    private CustomerManagerImpl customerManager;
    private BookTableModel bookModel;
    private CustomerTableModel customerModel;
    private RentTableModel rentModel;
    private DataSource ds;
    private String configFilePath = "config.properties";

    public void setProps(Properties props) {
        this.props = props;
    }

    private Properties props;
    private final static Logger log = LoggerFactory.getLogger(GUI.class);

    public GUI() {
        addRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookTableModel availableBooks = new BookTableModel();
                availableBooks.addBooks(rentManager.listAvailableBooks(bookManager.listAllBooks()));
                JTable books = new JTable(availableBooks);

                JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                errorMsg.setForeground(Color.RED);
                errorMsg.setVisible(false);
                JTable customers = new JTable(customerModel);
                JTextField expectedReturnTime = new JTextField(LocalDate.now().toString());

                Object[] message = {
                        errorMsg,
                        bundle.getString("availableBooks") + ":", books,
                        bundle.getString("customers") + ": ", customers,
                        bundle.getString("expectedReturnTimeLabel") + ": ", expectedReturnTime
                };
                int option = JOptionPane.NO_OPTION;
                while(option == JOptionPane.NO_OPTION){
                    option = JOptionPane.showConfirmDialog(null, message, bundle.getString("addRentButton"), JOptionPane.OK_CANCEL_OPTION);

                    if (option == JOptionPane.OK_OPTION) {
                        if(expectedReturnTime.getText() != null && !expectedReturnTime.getText().isEmpty()){
                        Rent rent = new Rent();
                        if(books.getSelectedRow() == -1){
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(bundle.getString("selectBookEr"));
                            errorMsg.setVisible(true);
                            continue;
                        }
                        if(customers.getSelectedRow() == -1){
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(bundle.getString("selectCustomerEr"));
                            errorMsg.setVisible(true);
                            continue;
                        }
                        rent.setBook(bookManager.getBookById((Long) books.getValueAt(books.getSelectedRow(), 0)));
                        rent.setCustomer(customerManager.getCustomerById((Long) customers.getValueAt(customers.getSelectedRow(), 0)));
                        try{
                            rent.setExpectedReturnTime(LocalDate.parse(expectedReturnTime.getText()));
                        } catch (DateTimeParseException e1){
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(e1.getMessage());
                            errorMsg.setVisible(true);
                        }
                        try{
                            rentManager.createRent(rent);
                            rentModel.addRents(rentManager.listAllRents());
                        } catch (IllegalEntityException e1){
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(e1.getMessage());
                            errorMsg.setVisible(true);
                        }
                    } else{
                        option = JOptionPane.NO_OPTION;
                        errorMsg.setText(bundle.getString("defaultError"));
                        errorMsg.setVisible(true);
                    }

                    } else {

                    }
                }

            }
        });

        deleteRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table3.getSelectedRow() != -1){
                    rentManager.deleteRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));
                    rentModel.addRents(rentManager.listAllRents());
                }
            }
        });

        updateRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Rent rent = rentManager.getRentById((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));

                JTextField expectedReturnTime = new JTextField(rent.getExpectedReturnTime().toString());

                JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                errorMsg.setForeground(Color.RED);
                errorMsg.setVisible(false);
                Object[] message = {
                        errorMsg,
                        bundle.getString("expectedReturnTimeLabel")+ ": ", expectedReturnTime
                };
                if(table3.getSelectedRow() == -1){
                    return;
                }
                int option = JOptionPane.NO_OPTION;
                while(option == JOptionPane.NO_OPTION){
                    option = JOptionPane.showConfirmDialog(null, message, bundle.getString("addRentButton"), JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if(expectedReturnTime.getText() != null && !expectedReturnTime.getText().isEmpty()){
                            try{
                                LocalDate newDate;
                                try{
                                    newDate = LocalDate.parse(expectedReturnTime.getText());
                                } catch (DateTimeParseException e1){
                                    option = JOptionPane.NO_OPTION;
                                    errorMsg.setText(e1.getMessage());
                                    errorMsg.setVisible(true);
                                    continue;
                                }
                                rentManager.updateRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0), newDate);
                                rentModel.addRents(rentManager.listAllRents());
                            } catch(IllegalArgumentException e1){
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(e1.getMessage());
                                errorMsg.setVisible(true);
                            }
                        } else{
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(bundle.getString("defaultError"));
                            errorMsg.setVisible(true);
                        }

                    } else {

                    }
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
                JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                errorMsg.setForeground(Color.RED);
                errorMsg.setVisible(false);
                JTextField email = new JTextField();
                Object[] message = {
                        errorMsg,
                        bundle.getString("fullNameLabel") + ":", fullName,
                        bundle.getString("emailLabel") + ":", email
                };
                int option = JOptionPane.NO_OPTION;
                while(option == JOptionPane.NO_OPTION){
                    option = JOptionPane.showConfirmDialog(null, message, bundle.getString("addCustomerButton"), JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if(fullName.getText() != null && !fullName.getText().isEmpty() && email.getText() != null && !email.getText().isEmpty()){
                            Customer customer = new Customer();
                            customer.setFullName(fullName.getText());
                            customer.setEmail(email.getText());
                            try {
                                customerManager.createCustomer(customer);
                            } catch (SQLException e1) {
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(e1.getMessage());
                                errorMsg.setVisible(true);
                            } catch (ValidationException e1) {
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(e1.getMessage());
                                errorMsg.setVisible(true);
                            } catch (IllegalArgumentException e1){
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(e1.getMessage());
                                errorMsg.setVisible(true);
                            }
                            try {
                                customerModel.addCustomers(customerManager.listAllCustomers());
                            } catch (SQLException e1) {
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(e1.getMessage());
                                errorMsg.setVisible(true);
                            }
                        }
                        else{
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(bundle.getString("defaultError"));
                            errorMsg.setVisible(true);
                        }
                    } else {

                    }
                }
            }
        });

        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table2.getSelectedRow() != -1){
                    customerManager.deleteCustomer((Long) table2.getModel().getValueAt(table2.getSelectedRow(), 0));
                    try {
                        customerModel.addCustomers(customerManager.listAllCustomers());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        updateCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table2.getSelectedRow() != -1){
                    Customer customer = customerManager.getCustomerById((Long) table2.getModel().getValueAt(table2.getSelectedRow(), 0));
                    JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                    errorMsg.setForeground(Color.RED);
                    errorMsg.setVisible(false);
                    JTextField fullName = new JTextField(customer.getFullName());
                    JTextField email = new JTextField(customer.getEmail());
                    Object[] message = {
                            errorMsg,
                            bundle.getString("fullNameLabel") + ":", fullName,
                            bundle.getString("emailLabel") + ":", email
                    };

                    int option = JOptionPane.NO_OPTION;
                    while(option ==JOptionPane.NO_OPTION){
                        option = JOptionPane.showConfirmDialog(null, message, "Update customer", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            if(fullName.getText() != null && !fullName.getText().isEmpty() && email.getText() != null && !email.getText().isEmpty()){
                                customer.setFullName(fullName.getText());
                                customer.setEmail(email.getText());
                                try {
                                    customerManager.updateCustomer(customer);
                                } catch (SQLException e1) {
                                    option = JOptionPane.NO_OPTION;
                                    errorMsg.setText(e1.getMessage());
                                    errorMsg.setVisible(true);
                                } catch (ValidationException e1) {
                                    option = JOptionPane.NO_OPTION;
                                    errorMsg.setText(e1.getMessage());
                                    errorMsg.setVisible(true);
                                }
                                catch (IllegalArgumentException e1){
                                    option = JOptionPane.NO_OPTION;
                                    errorMsg.setText(e1.getMessage());
                                    errorMsg.setVisible(true);
                                }
                                try {
                                    customerModel.addCustomers(customerManager.listAllCustomers());
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            else{
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(bundle.getString("defaultError"));
                                errorMsg.setVisible(true);
                            }

                        } else {

                        }
                    }
                }
            }
        });

        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                errorMsg.setForeground(Color.RED);
                errorMsg.setVisible(false);
                JTextField author = new JTextField();
                JTextField title = new JTextField();
                Object[] message = {
                        errorMsg,
                        bundle.getString("authorLabel") + ":", author,
                        bundle.getString("titleLabel") + ":", title
                };
                int option = JOptionPane.NO_OPTION;
                while(option == JOptionPane.NO_OPTION){
                    option = JOptionPane.showConfirmDialog(null, message, bundle.getString("addBookButton"), JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if(author.getText() != null && !author.getText().isEmpty() && title.getText() != null && !title.getText().isEmpty()){
                            Book book = new Book();
                            book.setAuthor(author.getText());
                            book.setTitle(title.getText());
                            bookManager.createBook(book);
                            bookModel.addBooks(bookManager.listAllBooks());
                        } else{
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setVisible(true);
                        }
                    } else {
                    }
                }

            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table1.getSelectedRow() != -1){
                    bookManager.deleteBook((Long) table1.getModel().getValueAt(table1.getSelectedRow(), 0));
                    bookModel.addBooks(bookManager.listAllBooks());
                }
                else{
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table1.getSelectedRow() != -1){
                    Book book = bookManager.getBookById((Long) table1.getModel().getValueAt(table1.getSelectedRow(), 0));
                    JLabel errorMsg = new JLabel(bundle.getString("defaultError"));
                    errorMsg.setForeground(Color.RED);
                    errorMsg.setVisible(false);
                    JTextField author = new JTextField(book.getAuthor());
                    JTextField title = new JTextField(book.getTitle());
                    Object[] message = {
                            errorMsg,
                            bundle.getString("authorLabel") + ":", author,
                            bundle.getString("titleLabel") + ":", title
                    };
                    int option = JOptionPane.NO_OPTION;
                    while(option == JOptionPane.NO_OPTION){
                        option = JOptionPane.showConfirmDialog(null, message, bundle.getString("updateBookButton"), JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            if(author.getText() != null && !author.getText().isEmpty() && title.getText() != null && !title.getText().isEmpty()){
                                book.setAuthor(author.getText());
                                book.setTitle(title.getText());
                                bookManager.updateBook(book);
                                bookModel.addBooks(bookManager.listAllBooks());
                            } else{
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setVisible(true);
                            }
                        } else {
                        }
                    }

                }
                else{
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        czLanguage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeResourceBundle("localization", Locale.forLanguageTag("cs-CS"));
                props.setProperty("language", "cs-CS");
                File configFile = new File(configFilePath);
                FileWriter writer = null;
                try {
                    writer = new FileWriter(configFile);
                    props.store(writer, "language");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        skLanguage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeResourceBundle("localization", Locale.forLanguageTag("sk-SK"));
                props.setProperty("language", "sk-SK");
                File configFile = new File(configFilePath);
                FileWriter writer = null;
                try {
                    writer = new FileWriter(configFile);
                    props.store(writer, "language");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        enLanguage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeResourceBundle("localization", Locale.forLanguageTag("en-US"));

                props.setProperty("language", "en-US");
                File configFile = new File(configFilePath);
                FileWriter writer = null;
                try {
                    writer = new FileWriter(configFile);
                    props.store(writer, "language");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void changeResourceBundle(String baseName, Locale locale){
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle(baseName, locale);
        this.addBookButton.setText(bundle.getString("addBookButton"));
        this.addCustomerButton.setText(bundle.getString("addCustomerButton"));
        this.addRentButton.setText(bundle.getString("addRentButton"));
        this.deleteBookButton.setText(bundle.getString("deleteBookButton"));
        this.deleteCustomerButton.setText(bundle.getString("deleteCustomerButton"));
        this.deleteRentButton.setText(bundle.getString("deleteRentButton"));
        this.updateBookButton.setText(bundle.getString("updateBookButton"));
        this.updateCustomerButton.setText(bundle.getString("updateCustomerButton"));
        this.updateRentButton.setText(bundle.getString("updateRentButton"));
        this.returnBookButton.setText(bundle.getString("returnBookButton"));
        this.czLanguage.setText(bundle.getString("czLanguage"));
        this.enLanguage.setText(bundle.getString("enLanguage"));
        this.skLanguage.setText(bundle.getString("skLanguage"));
        this.tab.setTitleAt(0, bundle.getString("BookTab"));
        this.tab.setTitleAt(1, bundle.getString("CustomerTab"));
        this.tab.setTitleAt(2, bundle.getString("RentTab"));
        this.tab.setTitleAt(3, bundle.getString("languageTab"));
        bookModel.changeResourceBundle("localization", locale);
        int skip = 0;
        for(int i = 0; i < bookModel.getColumnCount(); i++){
            if(bookModel.getColumnName(i).toLowerCase().contains("id")){
                skip++;
                continue;
            }
            table1.getTableHeader().getColumnModel().getColumn(i - skip).setHeaderValue(bookModel.getColumnName(i));
        }

        customerModel.changeResourceBundle("localization", locale);
        skip = 0;
        for(int i = 0; i < customerModel.getColumnCount(); i++){
            if(customerModel.getColumnName(i).toLowerCase().contains("id")){
                skip++;
                continue;
            }
            table2.getTableHeader().getColumnModel().getColumn(i - skip).setHeaderValue(customerModel.getColumnName(i));
        }

        rentModel.changeResourceBundle("localization", locale);
        skip = 0;
        for(int i = 0; i < rentModel.getColumnCount(); i++){
            if(rentModel.getColumnName(i).toLowerCase().contains("id")){
                skip++;
                continue;
            }
            table3.getTableHeader().getColumnModel().getColumn(i - skip).setHeaderValue(rentModel.getColumnName(i));
        }
    }

    public static void main(String[] args){
        EventQueue.invokeLater( ()-> { // zde použito funcionální rozhran
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            GUI panel = new GUI();
            File configFile = new File("config.properties");
            FileReader reader = null;
            try {
                reader = new FileReader(configFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Properties props = new Properties();
            try {
                props.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }

            panel.setProps(props);
            panel.changeResourceBundle("localization", Locale.forLanguageTag(props.getProperty("language")));
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setContentPane(panel.panel1);
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

    private void createUIComponents(){
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
