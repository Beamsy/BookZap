package uk.co.beamsy.bookzap.bookzap.model;

/**
 * Created by jake on 15/11/17.
 */

public class Book {
    private String title;
    private Author author;
    private int ISBN;

    public Book(String title, Author author, int ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public int getISBN() {
        return ISBN;
    }
}
