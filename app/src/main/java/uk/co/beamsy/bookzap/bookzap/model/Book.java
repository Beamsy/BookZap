package uk.co.beamsy.bookzap.bookzap.model;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by jake on 15/11/17.
 */

public class Book {
    private String title;
    private Author author;
    private int ISBN;
    private int coverId;

    public Book(String title, Author author, int ISBN, int coverId) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.coverId = coverId;
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

    public int getCoverId() {
        return coverId;
    }
}
