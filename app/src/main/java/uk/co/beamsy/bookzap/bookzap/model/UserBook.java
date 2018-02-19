package uk.co.beamsy.bookzap.bookzap.model;

import android.net.Uri;
import android.util.Log;

/**
 * Created by BEA17007261 on 24/01/2018.
 */

public class UserBook extends Book {
    private boolean isRead = false, isFavourite = false;
    private long readTo;

    public UserBook() {
        
    }

    public UserBook(String title, String author, double ISBN, Uri coverUri, int pageCount,
                    String googleId) {
        super(title, author, ISBN, coverUri, pageCount, googleId);
    }

    public UserBook(Book book){
        super(book.getTitle(), book.getAuthor(), book.getISBN(), book.getCoverUri(),
                book.getPageCount(), book.getGoogleBooksId());

    }

    public void setReadTo(long readTo) {
        if (readTo > getPageCount()){
            readTo = getPageCount();
        }
        this.readTo = readTo;
    }

    public long getReadTo() {
        return readTo;
    }

    public boolean isRead() {
        return isRead;
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
}
