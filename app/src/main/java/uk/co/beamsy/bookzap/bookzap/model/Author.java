package uk.co.beamsy.bookzap.bookzap.model;

/**
 * Created by jake on 15/11/17.
 */

public class Author {
    private String firstName;
    private String lastName;
    private int authorId;

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

    public String getName() {
        return firstName+" "+lastName;
    }
}
