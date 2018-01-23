package uk.co.beamsy.bookzap.bookzap;

import android.support.annotation.NonNull;
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

    public static FirestoreControl getInstance(FirebaseUser _currentUser) {
        FirestoreControl fs = new FirestoreControl();
        fs.init(_currentUser);
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

    public List<Book> getBookPage(int pointer) {
        final List<Book> bookPage = new ArrayList<>();
        firstBookPage
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                        for (final DocumentSnapshot userBook : documentSnapshots) {
                            db.collection("books").document(userBook.getId())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Book book = documentSnapshot.toObject(Book.class);
                                        book.setRead((boolean) userBook.get("completed"));
                                        book.setReadTo((int) userBook.get("progress"));
                                        bookPage.add(book);
                                    }
                                });

                    }
                }
            });
        return bookPage;
    }

    public void addBook (Book book) {
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
}
