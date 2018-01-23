package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.Author;
import uk.co.beamsy.bookzap.bookzap.model.Book;


public class BookFragment extends Fragment {
    private Book book = new Book("Blank", new Author("No", "One", 0), 0, R.drawable.ic_launcher_foreground, 1);

    public BookFragment(){
        //Required empty constructor
    }

    public static BookFragment getInstance() {
        return new BookFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_book, container, false);
        BookZap mainActivity = (BookZap) getActivity();
        Toolbar bookBar = (Toolbar) rootView.findViewById(R.id.book_toolbar);
        bookBar.inflateMenu(R.menu.book_toolbar_menu);
        TextView bookTitle = (TextView) rootView.findViewById(R.id.book_title);
        bookTitle.setText(book.getTitle());
        TextView authorName = (TextView) rootView.findViewById(R.id.author_name);
        authorName.setText(book.getAuthor().authorName(null));
        ImageView bookCover = (ImageView) rootView.findViewById(R.id.book_cover);
        bookCover.setImageBitmap(BitmapFactory.decodeResource(bookTitle.getContext().getResources(), book.getCoverId()));
        TextView progressText = (TextView)rootView.findViewById(R.id.progress_read_text);
        ProgressBar progressRead = (ProgressBar)rootView.findViewById(R.id.progress_read);
        TextView isRead = (TextView)rootView.findViewById(R.id.is_read_text);
        if (book.isRead()) {
            isRead.setVisibility(View.VISIBLE);
        }
        progressRead.setProgress(((book.getReadTo()/book.getPageCount())*100));
        progressText.setText(book.getReadTo()+"/"+book.getPageCount());
        mainActivity.changeDrawerBack(true);
        mainActivity.setTitle(book.getTitle());
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_toolbar_menu, menu);
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
