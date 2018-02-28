package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.FirestoreControl;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.bookzap.ui.RecyclerViewOnTouchItemListener;

public class LibraryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BookListListener {

    private static LibraryFragment libraryFragment;
    private BookCardAdaptor bookAdaptor;
    private List<UserBook> bookList;
    private boolean isFabMenuOpen = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    public LibraryFragment(){

    }

    public static LibraryFragment getInstance(){
        if (libraryFragment == null) {
            libraryFragment = new LibraryFragment();
            libraryFragment.init();
        }
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
        final View rootView = inflator.inflate(R.layout.fragment_library, container, false);
        final BookZap mainActivity = (BookZap) getActivity();

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_lib);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = rootView.findViewById(R.id.library_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);


        FloatingActionButton addMain = rootView.findViewById(R.id.fab_main);
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
        FloatingActionButton addScan = rootView.findViewById(R.id.fab_scan);
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
        FloatingActionButton addManually = rootView.findViewById((R.id.fab_add));
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
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Library");
//        if (bookList.size() == 0) {
//            refresh();
//        }
        return rootView;
    }



    public void addCard(UserBook b) {
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

    private void showMenu(){
        isFabMenuOpen = true;
        LinearLayout containerLayout = this.getActivity().findViewById(R.id.add_menu_layout);
        containerLayout.setVisibility(View.VISIBLE);
        containerLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_slide_in));
    }

    private void closeMenu(){
        isFabMenuOpen = false;
        LinearLayout containerLayout = this.getActivity().findViewById(R.id.add_menu_layout);
        containerLayout.setVisibility(View.GONE);
        containerLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_slide_out));
    }

    public void setBookList(List<UserBook> bookList) {
        if (this.bookList == null) {
            this.bookList = new ArrayList<>();
            bookAdaptor = new BookCardAdaptor(this.getContext(), this.bookList, true);
        }
        this.bookList.clear();
        this.bookList.addAll(bookList);
        bookAdaptor.notifyDataSetChanged();
    }

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (bookList.size() != 0) {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                    .getBookPage(this, bookList.get(bookList.size() - 1), FirestoreControl.SORT_TYPE_ISBN);
        } else {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                    .getFirstBookPage(this, FirestoreControl.SORT_TYPE_ISBN);
        }

    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        setBookList(userBooks);
        BookZap mainActivity = (BookZap)getActivity();
        mainActivity.setBookList(userBooks);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
