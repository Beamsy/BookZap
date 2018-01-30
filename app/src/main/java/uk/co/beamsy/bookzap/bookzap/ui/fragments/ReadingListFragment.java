package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.bookzap.ui.BookCardAdaptor;

public class ReadingListFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<UserBook> bookList;
    private boolean isFabMenuOpen = false;

    public ReadingListFragment(){

    }

    public static ReadingListFragment getInstance(){
        ReadingListFragment ReadingListFragment = new ReadingListFragment();
        ReadingListFragment.init();
        return ReadingListFragment;
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
        recyclerView = (RecyclerView)rootView.findViewById(R.id.reading_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Reading List");
        bookList.addAll(((BookZap)getActivity()).getBookList());
        return rootView;
    }

    public void addCard(UserBook b) {
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

    public void setBookList(List<UserBook> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
        bookAdaptor.notifyDataSetChanged();
    }
}
