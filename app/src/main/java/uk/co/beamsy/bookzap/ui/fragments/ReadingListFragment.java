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

import uk.co.beamsy.bookzap.BookZapActivity;
import uk.co.beamsy.bookzap.connections.FirestoreControl;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;

public class ReadingListFragment
        extends AbstractRefreshingFragment {

    private static ReadingListFragment readingListFragment;

    public ReadingListFragment(){

    }

    public static ReadingListFragment getInstance(){
        if (readingListFragment == null) {
            readingListFragment = new ReadingListFragment();
            readingListFragment.init();
        }
        return readingListFragment;
    }


    protected void init() {
        super.init(R.layout.fragment_reading_list);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = super.onCreateView(inflator, container, savedInstanceState);
        final BookZapActivity mainActivity = (BookZapActivity) getActivity();
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Reading List");
        return rootView;
    }

    private void updateDataSet() {
        bookList.clear();
        List<UserBook> tempBookList = ((BookZapActivity) getActivity()).getBookList();
        for(int i = 0; i < tempBookList.size(); i++){
            if (tempBookList.get(i).isFavourite() && !bookList.contains(tempBookList.get(i))) {
                bookList.add(tempBookList.get(i));
            }
        }
        bookAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        BookZapActivity mainActivity = (BookZapActivity)getActivity();
        mainActivity.setBookList(userBooks);
        updateDataSet();
        swipeRefreshLayout.setRefreshing(false);
    }

}
