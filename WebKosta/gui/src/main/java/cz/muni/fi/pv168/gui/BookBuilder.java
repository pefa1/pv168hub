package cz.muni.fi.pv168.gui;

import cz.muni.fi.bl.Book;

/**
 * Created by Josef Ko�ta on 15.3.2017.
 * builder for a default customer
 */
public class BookBuilder {

    private Long id;
    private String author;
    private String title;

    public BookBuilder id(Long id){
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