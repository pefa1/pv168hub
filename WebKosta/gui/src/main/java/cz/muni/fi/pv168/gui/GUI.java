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
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by pefa1 on 3.5.2017.
 */
public class GUI {

    private Locale locale = Locale.forLanguageTag("en-US");
    private ResourceBundle bundle = ResourceBundle.getBundle("localization", locale);
    private JPanel panel1, Book, Customer, Rent;
    private JButton addRentButton, deleteRentButton, updateRentButton, returnBookButton;
    private JButton addCustomerButton, deleteCustomerButton, updateCustomerButton;
    private JButton addBookButton, deleteBookButton, updateBookButton;
    private JTable table1, table2, table3;
    private JTabbedPane tab;
    private JButton czLanguage, skLanguage, enLanguage;
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
                        try{
                            rent.setExpectedReturnTime(LocalDate.parse(expectedReturnTime.getText()));
                        } catch (DateTimeParseException e1){
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setText(e1.getMessage());
                            errorMsg.setVisible(true);
                        }
                        try{
                            AddRentSwing swing = new AddRentSwing();
                            swing.setRent(rent);
                            swing.setBook((Long) books.getValueAt(books.getSelectedRow(), 0));
                            swing.setCustomer((Long) customers.getValueAt(customers.getSelectedRow(), 0));
                            swing.execute();
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
                    }
                }

            }
        });

        deleteRentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table3.getSelectedRow() != -1){
                    DeleteRentSwing swing = new DeleteRentSwing();
                    swing.setRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));
                    swing.execute();
                }  else {
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
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
                                UpdateRentSwing swing = new UpdateRentSwing();
                                swing.setRent((Long) table3.getModel().getValueAt(table3.getSelectedRow(), 0));
                                swing.setReturnTime(newDate);
                                swing.execute();
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
                                AddCustomerSwing swing = new AddCustomerSwing();
                                swing.setCustomer(customer);
                                swing.execute();
                            } catch (IllegalArgumentException e1) {
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
                    }
                }
            }
        });

        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table2.getSelectedRow() != -1){
                    DeleteCustomerSwing swing = new DeleteCustomerSwing();
                    swing.setCustomer((Long) table2.getModel().getValueAt(table2.getSelectedRow(), 0));
                    swing.execute();
                } else {
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
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
                                    UpdateCustomerSwing swing = new UpdateCustomerSwing();
                                    swing.setCustomer(customer);
                                    swing.execute();
                                } catch (IllegalArgumentException e1) {
                                    option = JOptionPane.NO_OPTION;
                                    errorMsg.setText(e1.getMessage());
                                    errorMsg.setVisible(true);
                                }
                            }else{
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setText(bundle.getString("defaultError"));
                                errorMsg.setVisible(true);
                            }

                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
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
                            //bookManager.createBook(book);
                            bookModel.addBooks(bookManager.listAllBooks());
                            AddBookSwing swing = new AddBookSwing();
                            swing.setBook(book);
                            swing.execute();
                        } else{
                            option = JOptionPane.NO_OPTION;
                            errorMsg.setVisible(true);
                        }
                    }
                }

            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table1.getSelectedRow() != -1){
                    DeleteBookSwing swing = new DeleteBookSwing();
                    swing.setBook((Long) table1.getModel().getValueAt(table1.getSelectedRow(), 0));
                    swing.execute();
                }
                else {
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
                                UpdateBookSwing swing = new UpdateBookSwing();
                                swing.setBook(book);
                                swing.execute();
                            } else{
                                option = JOptionPane.NO_OPTION;
                                errorMsg.setVisible(true);
                            }
                        }
                    }

                }
                else {
                    JOptionPane.showMessageDialog(null, bundle.getString("bookEr"), "Chyba",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        czLanguage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeResourceBundle("localization", Locale.forLanguageTag("cs-CS"));
                props.setProperty("language", "cs-CZ");
                File configFile = new File(configFilePath);
                FileWriter writer;
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
                FileWriter writer;
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
                FileWriter writer;
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

    private void prepareDB() throws IOException {
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

        bookManager = new BookManagerImpl();
        bookManager.setDataSource(ds);

        log.info("vytvořeny manažery a uloženy do atributů gui");
    }

    private void createUIComponents() throws IOException {
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

    private class AddBookSwing extends SwingWorker<Void, Void> {

        private Book book;
        public void setBook(Book book) {
            this.book = book;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while adding book", book, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("bookLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            bookManager.createBook(book);
            bookModel.addBooks(bookManager.listAllBooks());
            return null;
        }
    }

    private class AddCustomerSwing extends SwingWorker<Void, Void> {

        private Customer customer;
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while adding customer", customer, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("customerLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            customerManager.createCustomer(customer);
            customerModel.addCustomers(customerManager.listAllCustomers());
            return null;
        }
    }

    private class AddRentSwing extends SwingWorker<Void, Void> {

        private Rent rent;
        private Long book;
        private Long customer;
        public void setRent(Rent rent) {
            this.rent = rent;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while adding rent", rent, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("RentTab"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            rent.setBook(bookManager.getBookById(book));
            rent.setCustomer(customerManager.getCustomerById(customer));
            rentManager.createRent(rent);
            rentModel.addRents(rentManager.listAllRents());
            return null;
        }

        public void setBook(Long book) {
            this.book = book;
        }

        public void setCustomer(Long customer) {
            this.customer = customer;
        }
    }

    private class DeleteBookSwing extends SwingWorker<Void, Void> {

        private Long book;
        public void setBook(Long book) {
            this.book = book;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while deleting book", book, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("bookLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            bookManager.deleteBook(book);
            bookModel.addBooks(bookManager.listAllBooks());
            return null;
        }
    }

    private class UpdateBookSwing extends SwingWorker<Void, Void> {

        private Book book;
        public void setBook(Book book) {
            this.book = book;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while updating book", book, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("bookLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            bookManager.updateBook(book);
            bookModel.addBooks(bookManager.listAllBooks());
            return null;
        }
    }

    private class UpdateCustomerSwing extends SwingWorker<Void, Void> {

        private Customer customer;
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while updating customer", customer, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("customerLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            customerManager.updateCustomer(customer);
            customerModel.addCustomers(customerManager.listAllCustomers());
            return null;
        }
    }

    private class DeleteCustomerSwing extends SwingWorker<Void, Void> {

        private Long customer;
        public void setCustomer(Long customer) {
            this.customer = customer;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while deleting customer", customer, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("customerLabel"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            customerManager.deleteCustomer(customer);
            customerModel.addCustomers(customerManager.listAllCustomers());
            return null;
        }
    }

    private class UpdateRentSwing extends SwingWorker<Void, Void> {

        private Long rent;
        private LocalDate returnTime;
        public void setRent(Long rent) {
            this.rent = rent;
        }

        public void setReturnTime(LocalDate returnTime) {
            this.returnTime = returnTime;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while updating rent", rent, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("RentTab"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            rentManager.updateRent(rent, returnTime);
            rentModel.addRents(rentManager.listAllRents());
            return null;
        }
    }

    private class DeleteRentSwing extends SwingWorker<Void, Void> {

        private Long rent;
        public void setRent(Long rent) {
            this.rent = rent;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException e) {
                log.error("Error while deleting rent", rent, e.getCause());
                JOptionPane.showMessageDialog(panel1,
                        bundle.getString("defaultError"),
                        bundle.getString("RentTab"),
                        JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            rentManager.deleteRent(rent);
            rentModel.addRents(rentManager.listAllRents());
            return null;
        }
    }
}
