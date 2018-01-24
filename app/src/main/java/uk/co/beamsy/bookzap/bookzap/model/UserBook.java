package uk.co.beamsy.bookzap.bookzap.model;

/**
 * Created by BEA17007261 on 24/01/2018.
 */

public class UserBook {
    private Book book;
    private boolean isRead = false, isFavourite = false;
    private int readTo;

    public UserBook(Book book) {
        this.book = book;
    }

    public void setReadTo(int readTo) {
        if (readTo > book.getPageCount()){
            readTo = book.getPageCount();
        }
        this.readTo = readTo;
    }

    public int getReadTo() {
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
