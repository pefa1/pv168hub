package cz.muni.fi.BookManager;


import cz.muni.fi.Book;
/**
 * Created by Josef Košta on 15.3.2017.
 * builder for a default customer
 */
public class BookBuilder {

    private long id;
    private String author;
    private String title;

    public BookBuilder id(long id){
        this.id = id;
        return this;
    }

    public BookBuilder author(String author){
        this.author = author;
        return this;
    }

    public BookBuilder title(String title){
        this.title = title;
        return this;
    }


    public Book build(){
        Book book = new Book();
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        return book;
    }
}