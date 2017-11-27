package uk.co.beamsy.bookzap.bookzap.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.Author;
import uk.co.beamsy.bookzap.bookzap.model.Book;


public class AddFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<Book> bookList;
    private Button searchButton;
    private boolean isSearched = false;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment getInstance() {
        AddFragment fragment = new AddFragment();
        fragment.init();
        return fragment;
    }

    private void init() {
        bookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), bookList, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_add, container, false);
        final ConstraintLayout constraintLayout = (ConstraintLayout)rootView.findViewById(R.id.search_constraint_layout);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        searchButton = (Button)rootView.findViewById(R.id.add_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearched) {
                    populate();
                    constraintLayout.setVisibility(View.GONE);
                    isSearched = true;
                } else {
                    bookList.clear();
                    bookAdaptor.notifyDataSetChanged();
                    constraintLayout.setVisibility(View.VISIBLE);
                    isSearched = false;
                }

            }
        });
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.setTitle("Add a book");
        mainActivity.changeDrawerBack(true);
        return rootView;
    }

    private void populate() {
        //TODO: Add method to search Google Book api

        Author a = new Author("Brandon", "Sanderson", 0);
        Book b = new Book("Oathbringer", a, 0, R.drawable.oath, 1242);
        bookList.add(b);
        a = new Author("James", "Corey", 1);
        b = new Book("Leviathan Wakes", a, 0, R.drawable.lev, 561);
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

}
