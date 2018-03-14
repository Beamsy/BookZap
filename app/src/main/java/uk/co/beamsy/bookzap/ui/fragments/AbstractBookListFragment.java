package uk.co.beamsy.bookzap.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.BookZapActivity;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;

/**
 * Created by bea17007261 on 14/03/2018.
 */

abstract class AbstractBookListFragment extends Fragment {

    protected BookCardAdaptor bookAdaptor;
    protected List<UserBook> bookList;
    private int viewId;

    protected AbstractBookListFragment () {

    }

    protected void init(int viewId) {
        this.viewId = viewId;
        bookList = new ArrayList<>();
        boolean isLibrary = false;
        if (this.getClass().isInstance(LibraryFragment.getInstance())) {
            isLibrary = true;
        }
        bookAdaptor = new BookCardAdaptor(this.getContext(), bookList, isLibrary);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(viewId, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.booklist_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        final BookZapActivity mainActivity = (BookZapActivity) getActivity();
        mainActivity.changeDrawerBack(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchItemListener(
                this.getContext(), recyclerView,
                new RecyclerViewOnTouchItemListener.OnTouchListener() {
                    @Override
                    public void onTap(View view, int adaptorPosition) {
                        UserBook book = bookList.get(adaptorPosition);
                        mainActivity.changeFragment(BookFragment.getInstance().setBook(book), "book");
                    }

                    @Override
                    public void onHold(View view, int adaptorPosition) {

                    }
                }));
        return rootView;
    }

    public void setDataSet(List<UserBook> books) {
        bookList.clear();
        bookList.addAll(books);
        bookAdaptor.notifyDataSetChanged();
    }
}
