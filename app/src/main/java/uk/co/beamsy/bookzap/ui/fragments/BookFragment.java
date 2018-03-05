package uk.co.beamsy.bookzap.ui.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.FirestoreControl;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.UpdateProgressDialog;


public class BookFragment extends Fragment implements UpdateProgressDialog.UpdateProgressListener {
    private UserBook book = new UserBook("Blank", "No_one", 0,
            Uri.parse("android.resource://uk.co.beamsy.bookzap.bookzap/"
                    + R.mipmap.ic_launcher), 1,
                "test", "TODO");

    private Toolbar bookBar;

    private static BookFragment bookFragment;
    private TextView progressText;
    private ProgressBar progressRead;
    private TextView isRead;

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
        bookBar = rootView.findViewById(R.id.book_toolbar);
        bookBar.inflateMenu(R.menu.book_toolbar_menu);

        if (!book.isInLibrary()) {
            bookBar.getMenu().removeItem(R.id.menu_favourite);
        } else {
            bookBar.getMenu().removeItem(R.id.menu_add);
            if (book.isFavourite()) {
                bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_white_36dp);
            } else {
                bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_border_white_36dp);
            }
        }



        bookBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_favourite:
                        Log.d("Menu: ", "favourite");
                        toggleFavourite();
                        return true;
                    case R.id.menu_add:
                        addToLibrary();
                    default:
                        return false;
                }
            }
        });
        TextView bookTitle = rootView.findViewById(R.id.book_title);
        bookTitle.setText(book.getTitle());
        TextView authorName = rootView.findViewById(R.id.author_name);
        authorName.setText(book.getAuthor());
        ImageView bookCover = rootView.findViewById(R.id.book_cover);
        Glide
                .with(container.getContext())
                .load(book.getCoverUri())
                .apply(RequestOptions.fitCenterTransform())
                .into(bookCover);
        isRead = rootView.findViewById(R.id.is_read_text);
        ConstraintLayout progressLayout = rootView.findViewById(R.id.progress_layout);
        progressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
            }
        });
        progressText = rootView.findViewById(R.id.progress_read_text);
        progressRead = rootView.findViewById(R.id.progress_read);
        progressRead.setMax(((int) book.getPageCount()));
        updateProgress(book.getReadTo());
        mainActivity.changeDrawerBack(true);
        mainActivity.setTitle(book.getTitle());
        TextView description = rootView.findViewById(R.id.book_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        description.setText(book.getDescription());
        return rootView;
    }

    private void showProgressDialog() {
        UpdateProgressDialog dialog = UpdateProgressDialog.getInstance(book.getReadTo(), book.getPageCount(), this);
        dialog.show(getFragmentManager(), "UpdateProgressDialogFragment");
    }


    public void updateProgress(long readTo) {
        book.setReadTo(readTo);
        if (readTo == book.getPageCount()) {
            book.setRead(true);
            isRead.setVisibility(View.VISIBLE);
        } else {
            book.setRead(false);
            isRead.setVisibility(View.GONE);
        }
        book.setLastReadToNow();
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).modifyUserBookData(book);
        progressRead.setProgress(((int) book.getReadTo()));
        String progressTextString = book.getReadTo()+"/"+book.getPageCount();
        progressText.setText(progressTextString);
    }

    private void toggleFavourite(){
        book.setFavourite(!book.isFavourite());
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).modifyUserBookData(book);
        if (book.isFavourite()) {
            bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_white_36dp);
        } else {
            bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_border_white_36dp);
        }

    }

    public void setBook(UserBook book) {
        this.book = book;
    }

    public void addToLibrary(){
        try {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).addBookToLibrary(book);
        } catch (FirebaseFirestoreException e) {
            e.printStackTrace();
        }
    }
}
