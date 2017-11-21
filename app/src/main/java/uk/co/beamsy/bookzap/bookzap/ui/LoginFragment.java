package uk.co.beamsy.bookzap.bookzap.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;


public class LoginFragment extends BaseFragment {

    public LoginFragment() {
    }

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.changeDrawerToBack();
        return rootView;
    }

}
