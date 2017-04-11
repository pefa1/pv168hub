package cz.muni.fi.web;


import cz.muni.fi.bl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Created by Pepa on 03.04.2017.
 */
@WebServlet(BookServlet.URL_MAPPING + "/*")
public class BookServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    private static final String UPDATE_JSP = "/updateBook.jsp";
    private static final String UPDATE_CUSTOMER_JSP = "/updateCustomer.jsp";
    private static final String UPDATE_RENT_JSP = "/updateRent.jsp";
    public static final String URL_MAPPING = "/sth";

    private final static Logger log = LoggerFactory.getLogger(BookServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        try {
            showList(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        log.debug("POST ... {}",action);
        switch (action) {
            case "/addBook":
                try {
                    addBook(request, response);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "/deleteBook":
                deleteBook(request, response);
                break;
            case "/updateBook":
                updateBook(request, response);
                break;
            case "/postUpdateBook":
                postUpdateBook(request, response);
                break;
            case  "/addCustomer":
                try {
                    addCustomer(request, response);
                } catch (SQLException | ValidationException e) {
                    e.printStackTrace();
                }
                break;
            case "/deleteCustomer":
                deleteCustomer(request, response);
                break;
            case "/updateCustomer":
                updateCustomer(request, response);
                break;
            case "/postUpdateCustomer":
                try {
                    postUpdateCustomer(request, response);
                } catch (ValidationException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "/addRent":
                try {
                    addRent(request, response);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "/deleteRent":
                deleteRent(request, response);
                break;
            case "/updateRent":
                updateRent(request, response);
                break;
            case "/postUpdateRent":
                postUpdateRent(request, response);
                break;
            case "/returnBook":
                returnBook(request, response);
                break;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private void postUpdateRent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            getRentManager().updateRent(Long.valueOf(request.getParameter("id")), LocalDate.parse(request.getParameter("expectedReturnTime")));

            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath() + URL_MAPPING);
        } catch (ServiceFailureException e) {
            log.error("Cannot update rent", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateRent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Rent rentForUpdate = new Rent();
            rentForUpdate.setId(Long.valueOf(request.getParameter("id")));
            rentForUpdate.setExpectedReturnTime(LocalDate.parse(request.getParameter("expectedReturnTime")));
            request.setAttribute("rent", rentForUpdate);

            log.debug("updating rent");
            request.getRequestDispatcher(UPDATE_RENT_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            log.error("Cannot update rent", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    private void returnBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getRentManager().ReturnBook(getRentManager().getRentById(id));
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot return book", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void postUpdateCustomer(HttpServletRequest request, HttpServletResponse response) throws ValidationException, SQLException, IOException, ServletException {
        try {
            int n = 0;
            Customer updatedCustomer = new Customer();
            updatedCustomer.setId(Long.valueOf(request.getParameter("id")));
            updatedCustomer.setFullName(String.valueOf(request.getParameter("fullName")));
            updatedCustomer.setEmail(String.valueOf(request.getParameter("email")));

            for (Customer customer : getCustomerManager().listAllCustomers()) {
                if (customer.getEmail().equals(updatedCustomer.getEmail())) {
                    n++;
                }
            }

            if (n<1) {
                getCustomerManager().updateCustomer(updatedCustomer);
            }

            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath() + URL_MAPPING);
        } catch (ServiceFailureException e) {
            log.error("Cannot update customer", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Customer customerForUpdate = new Customer();
            customerForUpdate.setId(Long.valueOf(request.getParameter("id")));
            customerForUpdate.setFullName(String.valueOf(request.getParameter("fullName")));
            customerForUpdate.setEmail(String.valueOf(request.getParameter("email")));
            request.setAttribute("customer", customerForUpdate);

            log.debug("updating customer");
            request.getRequestDispatcher(UPDATE_CUSTOMER_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            log.error("Cannot update customer", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void postUpdateBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Book updatedBook = new Book();
            updatedBook.setId(Long.valueOf(request.getParameter("id")));
            updatedBook.setAuthor(String.valueOf(request.getParameter("author")));
            updatedBook.setTitle(String.valueOf(request.getParameter("title")));

            getBookManager().updateBook(updatedBook);

            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath() + URL_MAPPING);
            return;
        } catch (ServiceFailureException e) {
            log.error("Cannot update book", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Book bookForUpdate = new Book();
            bookForUpdate.setId(Long.valueOf(request.getParameter("id")));
            bookForUpdate.setAuthor(String.valueOf(request.getParameter("author")));
            bookForUpdate.setTitle(String.valueOf(request.getParameter("title")));
            request.setAttribute("book", bookForUpdate);

            log.debug("updating book");
            request.getRequestDispatcher(UPDATE_JSP).forward(request, response);
            return;
        } catch (ServiceFailureException e) {
            log.error("Cannot update book", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    private void deleteRent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getRentManager().deleteRent(id);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete rent", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void addRent(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException, IOException {
        //getting POST parameters from form
        String customerId = request.getParameter("customers-option");
        String bookId = request.getParameter("books-option");
        String expectedReturnTime = request.getParameter("expectedReturnTime");
        //form data validity check
        if (expectedReturnTime == null || expectedReturnTime.length() == 0) {
            request.setAttribute("chyba2", "Je nutné vyplnit začiatok rentu !");
            log.debug("form data invalid");
            showList(request, response);
            return;
        }
        LocalDate ert = null;
        try{
             ert = LocalDate.parse(expectedReturnTime);
        } catch (DateTimeException e){
            request.setAttribute("chyba2", e.getMessage());
            log.debug("form data invalid");
            showList(request, response);
        }

        Long cId = Long.parseLong(customerId);
        Long bId = Long.parseLong(bookId);
        Customer customer = getCustomerManager().getCustomerById(cId);
        Book book = getBookManager().getBookById(bId);
        //form data processing - storing to database
        try {
            Rent rent = new Rent();
            rent.setId(null);
            rent.setCustomer(customer);
            rent.setBook(book);
            rent.setExpectedReturnTime(ert);
            getRentManager().createRent(rent);
            //redirect-after-POST protects from multiple submission
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath() + URL_MAPPING);
        } catch (IllegalEntityException e) {
            request.setAttribute("chyba2", e.getMessage());
            log.debug("form data invalid");
            showList(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("chyba2", e.getMessage());
            log.debug("form data invalid");
            showList(request, response);
        }
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getRentManager().deleteRent(id);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete rent", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, ValidationException {
        //getting POST parameters from form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        //form data validity check
        if (fullName == null || fullName.length() == 0 || email == null || email.length() == 0) {
            request.setAttribute("chyba1", "Je nutné vyplniť všetky hodnoty v customerovi !");
            log.debug("form data invalid");
            showList(request, response);
            return;
        }
        //form data processing - storing to database
        try {
            Customer customer = new Customer();
            customer.setId(null);
            customer.setFullName(fullName);
            customer.setEmail(email);
            getCustomerManager().createCustomer(customer);
            //redirect-after-POST protects from multiple submission
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot add customer", e);
            request.setAttribute("chyba1", "Je nutné zadať iný email !");
            showList(request, response);
        }
    }

    private CustomerManager getCustomerManager() {
        return (CustomerManager) getServletContext().getAttribute("customerManager");
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        //getting POST parameters from form
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        //form data validity check
        if (title == null || title.length() == 0 || author == null || author.length() == 0) {
            request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty v knihe!");
            log.debug("form data invalid");
            showList(request, response);
            return;
        }
        //form data processing - storing to database
        try {
            Book book = new Book();
            book.setId(null);
            book.setTitle(title);
            book.setAuthor(author);
            getBookManager().createBook(book);
            //redirect-after-POST protects from multiple submission
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot add book", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getBookManager().deleteBook(id);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete book", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Gets BookManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return BookManager instance
     */
    private BookManager getBookManager() {
        return (BookManager) getServletContext().getAttribute("bookManager");
    }

    /**
     * Stores the list of books to request attribute "books" and forwards to the JSP to display it.
     */
    private void showList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        try {
            log.debug("showing table of books and customers");
            request.setAttribute("books", getBookManager().listAllBooks());
            request.setAttribute("customers", getCustomerManager().listAllCustomers());
            request.setAttribute("rents", getRentManager().listAllRents());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (IllegalArgumentException e) {
            log.error("Cannot show books and customers", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private RentManager getRentManager() {
        return (RentManager) getServletContext().getAttribute("rentManager");
    }

}
