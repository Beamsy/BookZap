package uk.co.beamsy.bookzap.bookzap.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import uk.co.beamsy.bookzap.bookzap.BookZap;

/**
 * Created by Jake on 21/11/2017.
 */

public class BaseFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    public BaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getFragmentManager().addOnBackStackChangedListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fManager = getChildFragmentManager();
        if(fManager.getBackStackEntryCount() != 0){
            fManager.popBackStack();

        }
    }



}
