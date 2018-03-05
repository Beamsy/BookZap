package uk.co.beamsy.bookzap.model;

import android.support.annotation.Nullable;

/**
 * Created by jake on 15/11/17.
 */

public class Author {
    private String firstName;
    private String lastName;
    private int authorId;

    public Author() {

    }

    public Author(String firstName, String lastName, int authorId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorId = authorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String authorName (@Nullable String key) {
        return firstName + " " + lastName;
    }
}
