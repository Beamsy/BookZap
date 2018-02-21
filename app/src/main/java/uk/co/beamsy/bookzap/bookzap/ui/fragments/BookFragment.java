package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ActionMenuView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.FirestoreControl;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.Author;
import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;


public class BookFragment extends Fragment {
    private UserBook book = new UserBook("Blank", "No_one", 0,
            Uri.parse("android.resource://uk.co.beamsy.bookzap.bookzap/"
                    + R.drawable.ic_launcher_foreground), 1,
                "test", "TODO");

    private static BookFragment bookFragment;

    public BookFragment(){
        //Required empty constructor
    }

    public static BookFragment getInstance() {
        if (bookFragment == null){
            bookFragment = new BookFragment();
        }
        return bookFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_book, container, false);
        BookZap mainActivity = (BookZap) getActivity();
        Toolbar bookBar = (Toolbar) rootView.findViewById(R.id.book_toolbar);
        bookBar.inflateMenu(R.menu.book_toolbar_menu);
        bookBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_favourite:
                        Log.d("Menu: ", "favourite");
                        toggleFavourite();
                        return true;
                    default:
                        return false;
                }
            }
        });
        TextView bookTitle = (TextView) rootView.findViewById(R.id.book_title);
        bookTitle.setText(book.getTitle());
        TextView authorName = (TextView) rootView.findViewById(R.id.author_name);
        authorName.setText(book.getAuthor());
        ImageView bookCover = (ImageView) rootView.findViewById(R.id.book_cover);
        Glide
                .with(container.getContext())
                .load(book.getCoverUri())
                .apply(RequestOptions.fitCenterTransform())
                .into(bookCover);
        TextView progressText = (TextView)rootView.findViewById(R.id.progress_read_text);

        TextView isRead = (TextView)rootView.findViewById(R.id.is_read_text);
        if (book.isRead()) {
            isRead.setVisibility(View.VISIBLE);
        }

        ProgressBar progressRead = (ProgressBar)rootView.findViewById(R.id.progress_read);
        progressRead.setMax(((int) book.getPageCount()));
        progressRead.setProgress(((int) book.getReadTo()));
        progressText.setText(book.getReadTo()+"/"+book.getPageCount());

        mainActivity.changeDrawerBack(true);
        mainActivity.setTitle(book.getTitle());
        TextView description = (TextView) rootView.findViewById(R.id.book_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        description.setText(book.getDescription());
        return rootView;
    }


    public void toggleFavourite(){
        book.setFavourite(!book.isFavourite());
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).modifyUserBookData(book);

    }

    public void setBook(UserBook book) {
        this.book = book;
    }
}
