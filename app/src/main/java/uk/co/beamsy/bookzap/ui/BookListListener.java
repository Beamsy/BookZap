package uk.co.beamsy.bookzap.ui;

import java.util.List;
import uk.co.beamsy.bookzap.model.UserBook;

/**
 * Created by Jake on 31/01/2018.
 */

public interface BookListListener {
    void onBookListFetch(List<UserBook> userBooks);
}
