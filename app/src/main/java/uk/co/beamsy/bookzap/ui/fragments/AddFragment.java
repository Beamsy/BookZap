package uk.co.beamsy.bookzap.ui.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.connections.GoogleBooksConnection;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;


public class AddFragment extends Fragment implements GoogleBooksConnection.SearchResultListener {

    private static AddFragment fragment;
    private BookCardAdaptor bookAdaptor;
    private List<UserBook> searchBookList;
    private boolean isSearched;
    private static String SEARCH_ISBN = "isbn";
    private static String SEARCH_TITLE = "intitle";
    private static String SEARCH_AUTHOR = "inauthor";
    private EditText isbnText, authorText, titleText;
    private ConstraintLayout constraintLayout;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment getInstance() {
        if(fragment == null) {
            fragment = new AddFragment();
            fragment.init();
        }
        return fragment;
    }

    private void init() {
        searchBookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), searchBookList, false);
        isSearched = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_add, container, false);
        constraintLayout = rootView.findViewById(R.id.search_constraint_layout);
        RecyclerView recyclerView = rootView.findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        titleText = rootView.findViewById(R.id.add_title_edit);
        authorText = rootView.findViewById(R.id.add_author_edit);
        isbnText = rootView.findViewById(R.id.add_isbn_edit);
        Button searchButton = rootView.findViewById(R.id.add_search_button);
        toggleView();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearched) {
                    ArrayMap<String,String> searchTerms = new ArrayMap<>();
                    searchTerms.put(SEARCH_ISBN, isbnText.getText().toString());
                    searchTerms.put(SEARCH_AUTHOR, authorText.getText().toString());
                    searchTerms.put(SEARCH_TITLE, titleText.getText().toString());
                    GoogleBooksConnection.search(searchTerms, getContext(), fragment);
                    isSearched = true;
                    toggleView();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    try {
                        imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
                    } catch (NullPointerException e) {
                        Log.e("Add: ", "keyboard hide failed");
                    }
                } else {
                    searchBookList.clear();
                    bookAdaptor.notifyDataSetChanged();
                    isSearched = false;
                    toggleView();
                }
            }
        });
        final BookZap mainActivity = (BookZap) getActivity();
        mainActivity.setTitle("Add a book");
        mainActivity.changeDrawerBack(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchItemListener(
                this.getContext(), recyclerView,
                new RecyclerViewOnTouchItemListener.OnTouchListener() {
                    @Override
                    public void onTap(View view, int adaptorPosition) {
                        UserBook book = searchBookList.get(adaptorPosition);
                        BookFragment fragment = BookFragment.getInstance();
                        fragment.setBook(book);
                        mainActivity.changeFragment(fragment, "book");
                    }

                    @Override
                    public void onHold(View view, int adaptorPosition) {

                    }
                }));
        return rootView;
    }

    private void toggleView() {
        if (isSearched) {
            constraintLayout.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchResult(ArrayList<UserBook> userBooks) {
        searchBookList.clear();
        searchBookList.addAll(userBooks);
        bookAdaptor.notifyDataSetChanged();
    }
}
