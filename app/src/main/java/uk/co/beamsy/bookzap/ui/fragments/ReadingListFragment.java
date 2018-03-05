package uk.co.beamsy.bookzap.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.BookZap;
import uk.co.beamsy.bookzap.FirestoreControl;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;

public class ReadingListFragment
        extends Fragment
        implements BookListListener, SwipeRefreshLayout.OnRefreshListener {

    private BookCardAdaptor bookAdaptor;
    private List<UserBook> bookList;
    private static ReadingListFragment readingListFragment;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ReadingListFragment(){

    }

    public static ReadingListFragment getInstance(){
        if (readingListFragment == null) {
            readingListFragment = new ReadingListFragment();
            readingListFragment.init();
        }
        return readingListFragment;
    }

    private void init() {
        bookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), bookList, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_reading_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.reading_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        final BookZap mainActivity = (BookZap) getActivity();
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Reading List");
        updateDataSet();
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchItemListener(
                this.getContext(), recyclerView,
                new RecyclerViewOnTouchItemListener.OnTouchListener() {
                    @Override
                    public void onTap(View view, int adaptorPosition) {
                        UserBook book = bookList.get(adaptorPosition);
                        BookFragment fragment = BookFragment.getInstance();
                        fragment.setBook(book);
                        mainActivity.changeFragment(fragment, "book");
                    }

                    @Override
                    public void onHold(View view, int adaptorPosition) {

                    }
                }));
        swipeRefreshLayout = rootView.findViewById(R.id.reading_list_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    public void addCard(UserBook b) {
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

    private void updateDataSet() {
        bookList.clear();
        List<UserBook> tempBookList = ((BookZap) getActivity()).getBookList();
        for(int i = 0; i < tempBookList.size(); i++){
            if (tempBookList.get(i).isFavourite() && !bookList.contains(tempBookList.get(i))) {
                bookList.add(tempBookList.get(i));
            }
        }
        bookAdaptor.notifyDataSetChanged();
    }

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                .getBookPage(this, bookList.get(bookList.size()-1), FirestoreControl.SORT_TYPE_ISBN);

    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        BookZap mainActivity = (BookZap)getActivity();
        mainActivity.setBookList(userBooks);
        updateDataSet();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
