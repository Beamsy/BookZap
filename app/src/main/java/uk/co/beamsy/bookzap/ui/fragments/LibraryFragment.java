package uk.co.beamsy.bookzap.ui.fragments;

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

import uk.co.beamsy.bookzap.BookZapActivity;
import uk.co.beamsy.bookzap.connections.FirestoreControl;
import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.RecyclerViewOnTouchItemListener;

public class LibraryFragment extends AbstractRefreshingFragment implements
        SwipeRefreshLayout.OnRefreshListener {

    private static LibraryFragment libraryFragment;
    private boolean isFabMenuOpen = false;

    public LibraryFragment(){

    }

    public static LibraryFragment getInstance(){
        if (libraryFragment == null) {
            libraryFragment = new LibraryFragment();
            libraryFragment.init();
        }
        return libraryFragment;
    }

    protected void init() {
        super.init(R.layout.fragment_library);
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
                    BookZapActivity mainActivity = (BookZapActivity) getActivity();
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
                    BookZapActivity mainActivity = (BookZapActivity) getActivity();
                    mainActivity.changeFragment(AddFragment.getInstance(), "login");
                    isFabMenuOpen = false;
                }
            }
        });
        mainActivity.changeDrawerBack(false);
        mainActivity.setTitle("Library");
        return rootView;
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (bookList.size() != 0) {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                    .getBookPage(this, bookList.get(bookList.size() - 1), FirestoreControl.SORT_TYPE_ISBN);
        } else {
            FirestoreControl.getInstance(FirebaseAuth.getInstance().getCurrentUser())
                    .getFirstBookPage(this, FirestoreControl.SORT_TYPE_ISBN);
        }

    }

}
