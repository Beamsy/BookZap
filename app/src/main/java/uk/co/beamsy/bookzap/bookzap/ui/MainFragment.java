package uk.co.beamsy.bookzap.bookzap.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.R;

/**
 * Created by Jake on 19/11/2017.
 */

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<Book> bookList;
    private boolean isFabMenuOpen = false;

    public MainFragment(){
        bookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), bookList);
        Log.d("MainFragment Constructor:", "Fragment Constructed");
    }


    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MainFragment onCreateView", "Entry");
        View rootView = inflator.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager _layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(_layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        FloatingActionButton addMain = (FloatingActionButton)rootView.findViewById(R.id.fab_main);
        addMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if(!isFabMenuOpen){
                    showMenu();
                }else{
                    closeMenu();
                }
            }
        });
        Log.d("MainFragment onCreateView", "Exit");
        return rootView;
    }

    public void addCard(Book b) {
        Log.d("MainFragment addCard", "entry");
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

    private void showMenu(){
        isFabMenuOpen = true;
        LinearLayout containerLayout = (LinearLayout)this.getActivity().findViewById(R.id.add_menu_layout);
        containerLayout.setVisibility(View.VISIBLE);
        containerLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_slide_in));
    }

    private void closeMenu(){
        isFabMenuOpen = false;
        LinearLayout containerLayout = (LinearLayout)this.getActivity().findViewById(R.id.add_menu_layout);
        containerLayout.setVisibility(View.GONE);
        containerLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_slide_out));
    }
}
