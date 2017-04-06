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

/**
 * Created by Pepa on 03.04.2017.
 */
@WebServlet(BookServlet.URL_MAPPING + "/*")
public class BookServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    private static final String UPDATE_JSP = "/update.jsp";
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
                //TODO
                return;
            case "/addRent":
                addRent(request, response);
                break;
            case "/deleteRent":
                deleteRent(request, response);
                break;
            case "/updateRent":
                //TODO
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
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

    private void deleteRent(HttpServletRequest request, HttpServletResponse response) {
        /*try {
            Long id = Long.valueOf(request.getParameter("id"));
            getCustomerManager().deleteCustomer(id);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete customer", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }*/
    }

    private void addRent(HttpServletRequest request, HttpServletResponse response) {
        /*//getting POST parameters from form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        //form data validity check
        if (fullName == null || fullName.length() == 0 || email == null || email.length() == 0) {
            request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty v customerovi !");
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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }*/
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long id = Long.valueOf(request.getParameter("id"));
            getCustomerManager().deleteCustomer(id);
            log.debug("redirecting after POST");
            response.sendRedirect(request.getContextPath()+URL_MAPPING);
        } catch (IllegalArgumentException e) {
            log.error("Cannot delete customer", e);
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
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (IllegalArgumentException e) {
            log.error("Cannot show books and customers", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
