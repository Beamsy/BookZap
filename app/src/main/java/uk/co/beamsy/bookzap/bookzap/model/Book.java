package uk.co.beamsy.bookzap.bookzap.model;

import android.net.Uri;

import java.net.URI;

/**
 * Created by jake on 15/11/17.
 */

public class Book {
    private String title;
    private Author author;
    private int coverId, readTo, pageCount = 0;
    private double ISBN;
    private boolean isRead = false, isFavourite = false;
    private Uri coverUri;

    public Book() {}

    public Book(String title, Author author, double ISBN, int coverId, int pageCount) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.coverId = coverId;
        this.pageCount = pageCount;
    }

    public Book(String title, Author author, double ISBN, Uri coverUri, int pageCount) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.coverUri = coverUri;
        this.pageCount = pageCount;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public double getISBN() {
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

    public Uri getCoverUri() {
        return coverUri;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
