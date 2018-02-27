package uk.co.beamsy.bookzap.bookzap.model;

import android.net.Uri;


public class Book {
    private String title;
    private String author;
    private long pageCount = 0;
    private double ISBN;
    private Uri coverUri;
    private String coverUriString;
    private String googleBooksId;
    private String description;

    //Necessary blank constructor for object creation using Firestore document to object conversion
    public Book() {}

    public Book(String title, String author, double ISBN, Uri coverUri, long pageCount,
                String googleId, String description) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.coverUri = coverUri;
        this.pageCount = pageCount;
        this.googleBooksId = googleId;
        this.description = description;
    }

    public void setCoverUri(String coverUri) {
        Uri.parse(coverUri);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getISBN() {
        return ISBN;
    }

    public long getPageCount() {
        return pageCount;
    }

    public Uri getCoverUri() {
        if(coverUri == null) {
            coverUri = Uri.parse(coverUriString);
        }
        return coverUri;
    }

    public String getCoverUriString() {
        return coverUriString;
    }

    public String getGoogleBooksId() {
        return googleBooksId;
    }

    public String getDescription () {
        return description;
    }

}
