package uk.co.beamsy.bookzap.bookzap.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.R;

/**
 * Created by Jake on 19/11/2017.
 */

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<Book> bookList;
    private boolean isFabMenuOpen = false;

    public LibraryFragment(){

    }

    public static LibraryFragment getInstance(){
        LibraryFragment libraryFragment = new LibraryFragment();
        libraryFragment.init();
        return libraryFragment;
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
        Log.d("LibraryFragment onCreateView", "Entry");
        final View rootView = inflator.inflate(R.layout.fragment_library, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.library_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
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
        FloatingActionButton addScan = (FloatingActionButton)rootView.findViewById(R.id.fab_scan);
        addScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if(isFabMenuOpen) {
                    BookZap mainActivity = (BookZap) getActivity();
                    if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                rootView.getContext().getResources().getInteger(R.integer.PERMISSION_REQUEST_CAMERA));
                    } else {
                        mainActivity.changeFragment(ScannerFragment.getInstance(), "scanner");
                        isFabMenuOpen = false;
                    }
                }
            }
        });
        FloatingActionButton addManually = (FloatingActionButton)rootView.findViewById((R.id.fab_add));
        addManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                if(isFabMenuOpen) {
                    BookZap mainActivity = (BookZap) getActivity();
                    mainActivity.changeFragment(AddFragment.getInstance(), "login");
                    isFabMenuOpen = false;
                }
            }
        });
        //TODO: Implement OnItemTouchLogic
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        Log.d("LibraryFragment onCreateView", "Exit");
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Library");
        setBookList(mainActivity.getBookList());
        return rootView;
    }

    public void addCard(Book b) {
        Log.d("LibraryFragment addCard", "entry");
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

    public void setBookList(List<Book> _bookList) {
        this.bookList.clear();
        this.bookList.addAll(_bookList);
        bookAdaptor.notifyDataSetChanged();
    }
}
