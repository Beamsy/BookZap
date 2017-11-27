package uk.co.beamsy.bookzap.bookzap.model;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by jake on 15/11/17.
 */

public class Book {
    private String title;
    private Author author;
    private int ISBN, coverId;
    private int readTo, pageCount = 0;
    private boolean isRead = false;

    public Book(String title, Author author, int ISBN, int coverId, int pageCount) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.coverId = coverId;
        this.pageCount = pageCount;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }

    public int getReadTo() {
        return readTo;
    }

    public void setReadTo(int readTo) {
        if (readTo > pageCount){
            readTo = pageCount;
        }
        this.readTo = readTo;
    }
}
