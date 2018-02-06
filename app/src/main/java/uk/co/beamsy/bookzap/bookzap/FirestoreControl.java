package uk.co.beamsy.bookzap.bookzap;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.bookzap.ui.BookListListener;

/**
 * Created by bea17007261 on 23/01/2018.
 */

public class FirestoreControl {

    private CollectionReference userBooksRef;
    private CollectionReference booksRef;
    private DocumentReference userRef;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Query firstBookPage;
    private static FirestoreControl fs;
    public String USER_BOOK_DATA_FAVOURITE = "favourite";
    public String USER_BOOK_DATA_READ = "completed";
    public String USER_BOOK_DATA_PROGRESS = "progress";


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
        db.collection("user").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        firstBookPage  = userBooksRef.limit(25);


    }



    public void getBookPage(BookListListener bookListListener) {
        List<UserBook> bookList = new ArrayList<>();
        try {
            bookList = (List<UserBook>) (new GetBookPageTask()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        bookListListener.onBookListFetch(bookList);
    }

    private class GetBookPageTask extends  AsyncTask<Void, Void, List<UserBook>> {

        private QuerySnapshot getUserData() throws ExecutionException, InterruptedException {
            return Tasks.await(firstBookPage.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {

                        }
                    }));
        }

        private List<UserBook> getUsersBooks(QuerySnapshot documentSnapshots) throws ExecutionException, InterruptedException {
            List<UserBook> userBookList = new ArrayList<>();

            for(DocumentSnapshot dSnapshot: documentSnapshots) {
                Book book = Tasks.await(db.collection("books")
                        .document(String.valueOf(dSnapshot.getId()))
                        .get()).toObject(Book.class);
                UserBook uBook = new UserBook(book);
                uBook.setRead((boolean) dSnapshot.get("completed"));
                uBook.setReadTo((long) dSnapshot.get("progress"));
                uBook.setFavourite((boolean) dSnapshot.get("favourite"));
                userBookList.add(uBook);
            }
            return userBookList;
        }

        @Override
        protected void onPostExecute(List<UserBook> userBooks) {

        }

        @Override
        protected List<UserBook> doInBackground(Void... voids) {
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

 /*   private class GetBookPageTask extends AsyncTask<Void, Void, List<UserBook>> {
        private BookListListener listener;
        private final List<UserBook> userBookList;


        protected GetBookPageTask(BookListListener bookListListener) {
            this.listener = bookListListener;
            userBookList = new ArrayList<>();
        }

        @Override
        protected List<UserBook> doInBackground(Void... v) {
            try {
                Tasks.await(firstBookPage
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                for (final DocumentSnapshot userBook:documentSnapshots) {

                                    try {
                                        snapshot = Tasks.await(
                                        db.collection("books").document(String.valueOf(userBook.getId()))
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    }
                                                }));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                        Book book = snapshot.toObject(Book.class);
                                        UserBook uBook = new UserBook(book);
                                        uBook.setRead((boolean) userBook.get("completed"));
                                        uBook.setReadTo((long) userBook.get("progress"));
                                        uBook.setFavourite((boolean) userBook.get("favourite"));
                                        userBookList.add(uBook);
                                        listener.addBookToList(uBook);
                                }
                                listener.completeAddition();
                            }
                        }));
                return userBookList;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<UserBook> userBooks) {
            Log.d("oPostE", "");
            //listener.onBookListFetch(userBooks);
        }


    }*/
    //public

    public void addBook (UserBook book) {
        String isbn = String.format("%.0f", book.getISBN());
        booksRef.document(isbn).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void modifyBook(){

    }

    public void modifyUserBookData(UserBook uBook) {
        ArrayMap<String, Object> aM = new ArrayMap<>();
        String isbn = String.format("%.0f", uBook.getISBN());
        aM.put(USER_BOOK_DATA_FAVOURITE, uBook.isFavourite());
        aM.put(USER_BOOK_DATA_PROGRESS, uBook.getReadTo());
        aM.put(USER_BOOK_DATA_READ, uBook.isRead());
        userBooksRef.document(isbn).set(aM).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}
