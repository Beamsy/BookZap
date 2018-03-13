package uk.co.beamsy.bookzap.ui.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.connections.FirestoreControl;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.Book;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.model.WishlistBook;
import uk.co.beamsy.bookzap.ui.UpdateProgressDialog;


public class BookFragment extends Fragment implements UpdateProgressDialog.UpdateProgressListener,
        OnCompleteListener<Void>
{
    private UserBook userBook = new UserBook();
    private WishlistBook wishlistBook = new WishlistBook();
    private boolean isWishlist;

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
    public View onCreateView(LayoutInflater inflator,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_book,
                container,
                false);
        BookZap mainActivity = (BookZap) getActivity();
        bookBar = rootView.findViewById(R.id.book_toolbar);
        bookBar.inflateMenu(R.menu.book_toolbar_menu);

        if (!isWishlist) {
            if (!userBook.isInLibrary()) {
                bookBar.getMenu().removeItem(R.id.menu_favourite);
                bookBar.getMenu().removeItem(R.id.menu_delete);
            } else {
                bookBar.getMenu().removeItem(R.id.menu_add);
                if (userBook.isFavourite()) {
                    bookBar.getMenu().findItem(R.id.menu_favourite)
                            .setIcon(R.drawable.ic_favorite_white_36dp);
                } else {
                    bookBar.getMenu().findItem(R.id.menu_favourite)
                            .setIcon(R.drawable.ic_favorite_border_white_36dp);
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
                            return true;
                        case R.id.menu_delete:
                            removeBook();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            TextView bookTitle = rootView.findViewById(R.id.book_title);
            bookTitle.setText(userBook.getTitle());
            TextView authorName = rootView.findViewById(R.id.author_name);
            authorName.setText(userBook.getAuthor());
            ImageView bookCover = rootView.findViewById(R.id.book_cover);
            Glide
                    .with(container.getContext())
                    .load(userBook.getCoverUri())
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
            progressRead.setMax(((int) userBook.getPageCount()));
            updateProgress(userBook.getReadTo());
            mainActivity.changeDrawerBack(true);
            mainActivity.setTitle(userBook.getTitle());
            TextView description = rootView.findViewById(R.id.book_description);
            description.setMovementMethod(new ScrollingMovementMethod());
            description.setText(userBook.getDescription());
            return rootView;
        } else {
            return rootView;
        }
    }

    private void showProgressDialog() {
        UpdateProgressDialog dialog = UpdateProgressDialog.getInstance(userBook.getReadTo(),
                userBook.getPageCount(), this);
        dialog.show(getFragmentManager(), "UpdateProgressDialogFragment");
    }


    public void updateProgress(long readTo) {
        userBook.setReadTo(readTo);
        if (readTo == userBook.getPageCount()) {
            userBook.setRead(true);
            isRead.setVisibility(View.VISIBLE);
        } else {
            userBook.setRead(false);
            isRead.setVisibility(View.GONE);
        }
        userBook.setLastReadToNow();
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).modifyUserBookData(userBook);
        progressRead.setProgress(((int) userBook.getReadTo()));
        String progressTextString = userBook.getReadTo()+"/"+ userBook.getPageCount();
        progressText.setText(progressTextString);
    }

    private void removeBook() {
        BookZap mainActivity = (BookZap) getActivity();
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                .removeUserBookData(userBook, mainActivity);
    }

    private void toggleFavourite(){
        userBook.setFavourite(!userBook.isFavourite());
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser()).modifyUserBookData(userBook);
        if (userBook.isFavourite()) {
            bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_white_36dp);
        } else {
            bookBar.getMenu().findItem(R.id.menu_favourite).setIcon(R.drawable.ic_favorite_border_white_36dp);
        }

    }

    public BookFragment setBook(Book book) {
        if (book.getClass() == UserBook.class) {
            this.userBook = (UserBook) book;
            isWishlist = false;
        } else if (book.getClass() == WishlistBook.class) {
            this.wishlistBook = (WishlistBook) book;
            isWishlist = true;
        }
        return this;
    }


    private void addToLibrary(){
        try {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                    .addBookToLibrary(userBook, this);
        } catch (FirebaseFirestoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(Task<Void> task) {
        if (task.isSuccessful()) {
            BookZap mainActivity = (BookZap) getActivity();
            List<UserBook> books = mainActivity.getBookList();
            books.add(userBook);
            mainActivity.setBookList(books);
            mainActivity.getFragmentManager()
                    .popBackStack("library", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mainActivity.changeFragment(LibraryFragment.getInstance(), "library");
        } else {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


}
