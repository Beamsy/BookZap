package uk.co.beamsy.bookzap.bookzap.model;

import android.net.Uri;

import java.time.Instant;
import java.util.Date;

public class UserBook extends Book {
    private boolean isRead = false, isFavourite = false, inLibrary = false;
    private long readTo;
    private Date lastRead;

    public UserBook() {
        
    }

    public UserBook(String title, String author, double ISBN, Uri coverUri, int pageCount,
                    String googleId, String description) {
        super(title, author, ISBN, coverUri, pageCount, googleId, description);
    }

    public UserBook(Book book){
        super(book.getTitle(), book.getAuthor(), book.getISBN(), book.getCoverUri(),
                book.getPageCount(), book.getGoogleBooksId(), book.getDescription());

    }

    public void setReadTo(long readTo) {
        if (readTo > getPageCount()){
            readTo = getPageCount();
        }
        this.readTo = readTo;
    }

    public void setLastReadToNow() {
        lastRead = Date.from(Instant.now());
    }

    public long getReadTo() {
        return readTo;
    }

    public boolean isRead() {
        return isRead;
    }

    public Date getLastRead () {
        return lastRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public boolean isInLibrary() {
        return inLibrary;
    }

    public void setInLibrary(boolean inLibrary) {
        this.inLibrary = inLibrary;
    }
}
