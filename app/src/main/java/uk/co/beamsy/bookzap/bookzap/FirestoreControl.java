package uk.co.beamsy.bookzap.bookzap;

import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;

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

    public static FirestoreControl makeInstance(FirebaseUser _currentUser) {
        fs = new FirestoreControl();
        fs.init(_currentUser);
        return fs;
    }

    public static FirestoreControl getInstance() {
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

    public List<UserBook> getBookPage(int pointer) {
        final List<UserBook> bookPage = new ArrayList<>();
        firstBookPage
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final DocumentSnapshot userBook : task.getResult()) {
                            db.collection("books").document(String.valueOf(userBook.getId()))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            Log.d("FireControl: ", "task complete");
                                            if (task.isSuccessful()) {

                                                Book book = task.getResult().toObject(Book.class);
                                                UserBook uBook = new UserBook(book);
                                                uBook.setRead((boolean) userBook.get("completed"));
                                                uBook.setReadTo((long) userBook.get("progress"));
                                                uBook.setFavourite((boolean) userBook.get("favourite"));
                                                bookPage.add(uBook);
                                            } else {
                                                Log.d("Firecontrol: ", task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                    } else {
                        Log.d("FireControl: ", task.getException().getMessage());
                    }
                }
            });
        return bookPage;
    }

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
