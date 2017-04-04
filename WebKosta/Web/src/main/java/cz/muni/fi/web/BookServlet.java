package cz.muni.fi.web;


import cz.muni.fi.bl.Book;
import cz.muni.fi.bl.BookManager;
import cz.muni.fi.bl.IllegalEntityException;
import cz.muni.fi.bl.ServiceFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Pepa on 03.04.2017.
 */
@WebServlet(BookServlet.URL_MAPPING + "/*")
public class BookServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    private static final String UPDATE_JSP = "/update.jsp";
    public static final String URL_MAPPING = "/book";

    private final static Logger log = LoggerFactory.getLogger(BookServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET ...");
        showBooksList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //support non-ASCII characters in form
        request.setCharacterEncoding("utf-8");
        //action specified by pathInfo
        String action = request.getPathInfo();
        log.debug("POST ... {}",action);
        switch (action) {
            case "/create":
                //getting POST parameters from form
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                //form data validity check
                if (title == null || title.length() == 0 || author == null || author.length() == 0) {
                    request.setAttribute("Error", "Values missing!");
                    log.debug("form data invalid");
                    showBooksList(request, response);
                    return;
                }
                //form data processing - storing to database
                try {
                    Book book = new Book();
                    book.setTitle(title);
                    book.setAuthor(author);
                    getBookManager().createBook(book);
                    //redirect-after-POST protects from multiple submission
                    log.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IllegalEntityException e) {
                    log.error("Cannot create book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot create book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getBookManager().deleteBook(id);
                    log.debug("redirecting after POST");
                    response.sendRedirect(request.getContextPath() + URL_MAPPING);
                    return;
                } catch (IllegalEntityException e) {
                    log.error("Cannot delete book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                } catch (ServiceFailureException e) {
                    log.error("Cannot delete book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
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
            case "/postUpdate":
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

            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);

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
    private void showBooksList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            log.debug("showing table of all books");
            request.setAttribute("books", getBookManager().listAllBooks());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            log.error("Cannot show books", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
