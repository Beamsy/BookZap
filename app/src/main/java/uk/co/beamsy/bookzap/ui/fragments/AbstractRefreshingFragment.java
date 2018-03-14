package uk.co.beamsy.bookzap.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import uk.co.beamsy.bookzap.BookZapActivity;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.connections.FirestoreControl;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookListListener;

/**
 * Created by bea17007261 on 14/03/2018.
 */

abstract class AbstractRefreshingFragment extends AbstractBookListFragment
        implements SwipeRefreshLayout.OnRefreshListener, BookListListener{


    protected SwipeRefreshLayout swipeRefreshLayout;

    AbstractRefreshingFragment () {
        super();
    }

    @Override
    protected void init(int viewId) {
        super.init(viewId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout = rootView.findViewById(R.id.booklist_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                .getBookPage(this,
                        bookList.get(bookList.size()-1), FirestoreControl.SORT_TYPE_ISBN);
    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        BookZapActivity mainActivity = (BookZapActivity)getActivity();
        mainActivity.setBookList(userBooks);
        setDataSet(userBooks);
        swipeRefreshLayout.setRefreshing(false);
    }
}
