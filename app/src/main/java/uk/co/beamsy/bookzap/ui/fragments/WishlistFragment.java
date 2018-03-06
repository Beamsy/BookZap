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
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.connections.FirestoreControl;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;

public class WishlistFragment
        extends Fragment
        implements BookListListener, SwipeRefreshLayout.OnRefreshListener {

    private BookCardAdaptor bookAdaptor;
    private List<UserBook> bookList;
    private static WishlistFragment readingListFragment;
    private SwipeRefreshLayout swipeRefreshLayout;

    public WishlistFragment(){

    }

    public static WishlistFragment getInstance(){
        if (readingListFragment == null) {
            readingListFragment = new WishlistFragment();
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
        final View rootView = inflator.inflate(R.layout.fragment_wishlist, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.wishlist_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        final BookZap mainActivity = (BookZap) getActivity();
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Wish List");
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchItemListener(
                this.getContext(), recyclerView,
                new RecyclerViewOnTouchItemListener.OnTouchListener() {
                    @Override
                    public void onTap(View view, int adaptorPosition) {
                        UserBook book = bookList.get(adaptorPosition);
                        BookFragment fragment = BookFragment.getInstance();
                        fragment.setBook(book).setWishList();
                        mainActivity.changeFragment(fragment, "book");
                    }

                    @Override
                    public void onHold(View view, int adaptorPosition) {

                    }
                }));
        swipeRefreshLayout = rootView.findViewById(R.id.wishlist_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    public void addCard(UserBook b) {
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }


    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                .getBookPage(this, bookList.get(bookList.size()-1), FirestoreControl.SORT_TYPE_ISBN);

    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        bookList = userBooks;
        bookAdaptor.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        refresh();
    }
}
