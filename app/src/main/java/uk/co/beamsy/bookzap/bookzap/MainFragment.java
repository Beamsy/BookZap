package uk.co.beamsy.bookzap.bookzap;

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

/**
 * Created by Jake on 19/11/2017.
 */

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<Book> bookList;

    public MainFragment(){
        bookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), bookList);
    }


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflator.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager _layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(_layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        return rootView;
    }

    public void addCard(Book b) {
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }
}
