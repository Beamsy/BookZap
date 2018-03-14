package uk.co.beamsy.bookzap.connections;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uk.co.beamsy.bookzap.BookZapActivity;
import uk.co.beamsy.bookzap.model.Book;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.fragments.LibraryFragment;

/**
 * Created by bea17007261 on 23/01/2018.
 */

public class FirestoreControl {

    private CollectionReference userBooksRef;
    private CollectionReference booksRef;
    private DocumentReference userRef;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static FirestoreControl fs;

    public static final String USER_BOOK_DATA_FAVOURITE = "favourite";
    public static final String USER_BOOK_DATA_READ = "completed";
    public static final String USER_BOOK_DATA_PROGRESS = "progress";
    public static final String USER_BOOK_DATA_LAST_READ = "last_read";

    public static final int SORT_TYPE_ISBN = 0;
    public static final int SORT_TYPE_ISBN_REVERSE = 1;
    public static final int SORT_TYPE_TITLE = 2;
    public static final int SORT_TYPE_TITLE_REVERSE = 3;
    public static final int SORT_TYPE_LAST_READ = 4;
    public static final int SORT_TYPE_LAST_READ_REVERSE = 5;


    public static FirestoreControl getInstance(FirebaseUser _currentUser) {
        if (fs == null) {
            fs = new FirestoreControl();
            fs.init(_currentUser);
        }
        return fs;
    }


    private void init(FirebaseUser _currentUser) {
        this.currentUser = _currentUser;
        db = FirebaseFirestore.getInstance();
        db.collection("user").document(currentUser.getUid()).get().addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot == null) {
                    Map<String, Object> user =  new HashMap<>();
                    user.put("userName", currentUser.getDisplayName());
                    db.collection("user").document(currentUser.getUid()).set(user);
                }
            }
        });
        userRef = db.collection("user").document(currentUser.getUid());
        userBooksRef = userRef.collection("userBooksData");
        booksRef = db.collection("books");


    }

    private void isBookPresent(Book book) throws FirebaseFirestoreException {
        final Book _book = book;
        booksRef.whereEqualTo("ISBN", book.getISBN()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                boolean isPresent = !documentSnapshots.isEmpty();
                if (isPresent) {
                    return;
                } else {
                    booksRef.document(_book.getISBNAsString()).set(_book.getCleanBookMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete() && ! task.isSuccessful()) {
                                Log.d("isBP", task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public void addBookToLibrary(UserBook userBook, OnCompleteListener<Void> listener) throws FirebaseFirestoreException {
        isBookPresent(userBook);
        HashMap<String, Object> hM = new HashMap<String, Object>();
        hM.put("ISBN", userBook.getISBNAsString());
        hM.put(USER_BOOK_DATA_READ, userBook.isRead());
        hM.put(USER_BOOK_DATA_FAVOURITE, userBook.isFavourite());
        hM.put(USER_BOOK_DATA_PROGRESS, userBook.getReadTo());
        hM.put("book_ref", booksRef.document(userBook.getISBNAsString()));
        userBook.setLastReadToNow();
        hM.put(USER_BOOK_DATA_LAST_READ, userBook.getLastRead());
        userBooksRef.document(userBook.getISBNAsString()).set(hM).addOnCompleteListener(listener);
    }

    public void getBookPage(BookListListener bookListListener, UserBook lastBook, int sortMethod) {

        (new GetBookPageTask()).execute(new GetBookPageArguments(bookListListener, lastBook, sortMethod));
    }

    public void getFirstBookPage(BookListListener bookListListener, int sortMethod) {
        (new GetBookPageTask()).execute(new GetBookPageArguments(bookListListener, null, sortMethod));
    }

    private class GetBookPageArguments {
        private BookListListener bookListListener;
        private UserBook lastBook;
        private int sortMethod;

        private GetBookPageArguments (BookListListener bookListListener, UserBook lastBook, int sortMethod) {
            this.bookListListener = bookListListener;
            this.lastBook = lastBook;
            this.sortMethod = sortMethod;
        }

        public BookListListener getBookListListener() {
            return bookListListener;
        }

        public UserBook getLastBook() {
            return lastBook;
        }

        public int getSortMethod() {
            return sortMethod;
        }
    }

    private class GetBookPageTask extends  AsyncTask<GetBookPageArguments, Void, List<UserBook>> {

        private BookListListener listener;
        private UserBook lastOfPrevious;
        private int sortType;

        private QuerySnapshot getUserData() throws ExecutionException, InterruptedException {
            Query page = userBooksRef;
            switch (sortType) {
                case SORT_TYPE_ISBN:
                    page = userBooksRef.orderBy("ISBN");
                    break;
                case SORT_TYPE_ISBN_REVERSE:
                    page = userBooksRef.orderBy("ISBN", Query.Direction.ASCENDING);
                    break;
                case SORT_TYPE_LAST_READ:
                    page = userBooksRef.orderBy(USER_BOOK_DATA_LAST_READ);
                    break;
                case SORT_TYPE_LAST_READ_REVERSE:
                    page = userBooksRef.orderBy(USER_BOOK_DATA_LAST_READ, Query.Direction.ASCENDING);
                    break;
                default:
                    Log.d("ASync", "sortType not found");
                    this.cancel(true);
                    break;
            }
            return Tasks.await(page.limit(25).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {

                        }
                    }));
        }

        private List<UserBook> getUsersBooks(QuerySnapshot documentSnapshots) throws ExecutionException, InterruptedException {
            List<UserBook> userBookList = new ArrayList<>();
            for(DocumentSnapshot dSnapshot: documentSnapshots) {
                if (Tasks.await
                        (dSnapshot.getDocumentReference("book_ref").get())
                        .exists()) {
                    Book book = Tasks.await(db.collection("books")
                            .document(String.valueOf(dSnapshot.getId()))
                            .get()).toObject(Book.class);
                    UserBook uBook = new UserBook(book);
                    uBook.setRead((boolean) dSnapshot.get(USER_BOOK_DATA_READ));
                    uBook.setReadTo((long) dSnapshot.get(USER_BOOK_DATA_PROGRESS));
                    uBook.setFavourite((boolean) dSnapshot.get(USER_BOOK_DATA_FAVOURITE));
                    uBook.setInLibrary(true);
                    userBookList.add(uBook);
                }
            }
            return userBookList;
        }

        @Override
        protected void onPostExecute(List<UserBook> userBooks) {
                listener.onBookListFetch(userBooks);
        }

        @Override
        protected List<UserBook> doInBackground(GetBookPageArguments... argumentsCollection) {
            GetBookPageArguments arguments = argumentsCollection[0];
            listener = arguments.getBookListListener();
            lastOfPrevious = arguments.getLastBook();
            sortType = arguments.getSortMethod();
            try {
                return getUsersBooks(getUserData());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void addBook (UserBook book) {

        booksRef.document(book.getISBNAsString()).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FireControl","It worked");
                }else{
                    Log.d("FireControl","It didn't" + task.getException().getMessage());
                }
            }
        });
    }

    public void modifyUserBookData(UserBook userBook) {
        ArrayMap<String, Object> aM = new ArrayMap<>();
        aM.put(USER_BOOK_DATA_FAVOURITE, userBook.isFavourite());
        aM.put(USER_BOOK_DATA_PROGRESS, userBook.getReadTo());
        aM.put(USER_BOOK_DATA_READ, userBook.isRead());
        aM.put("ISBN", userBook.getISBNAsString());
        aM.put(USER_BOOK_DATA_LAST_READ, userBook.getLastRead());
        aM.put("book_ref", booksRef.document(userBook.getISBNAsString()));
        userBooksRef.document(userBook.getISBNAsString()).set(aM);
    }

    public void getWishlist () {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<DocumentReference> references =
                        (ArrayList<DocumentReference>)documentSnapshot.get("wishlist");
                final List<Book> list = new ArrayList<>();
                for (DocumentReference ref: references) {
                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            list.add(documentSnapshot.toObject(Book.class));
                        }
                    });
                }
            }
        });
    }

    public void removeUserBookData (final UserBook userBook, final BookZapActivity mainActivity) {
        userBooksRef.document(userBook.getISBNAsString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                List<UserBook> books = mainActivity.getBookList();
                books.remove(userBook);
                mainActivity.onBookListFetch(books);
                mainActivity.getFragmentManager()
                        .popBackStack("library", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mainActivity.changeFragment(LibraryFragment.getInstance(), "library");
            }
        });
    }
}
