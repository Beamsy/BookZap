package uk.co.beamsy.bookzap.bookzap.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;


public class LoginFragment extends BaseFragment {

    private boolean isInCreate = false;
    public LoginFragment() {
    }

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.hideHome();
        TextView createLink = (TextView) rootView.findViewById(R.id.text_account_create_link);
        createLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!isInCreate) {
                   enterCreate();
                }
            }
        });
        Button cancelButton = (Button) rootView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInCreate) {
                    leaveCreate();
                }
            }
        });
        Button loginButton = (Button) rootView.findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInCreate) {
                    createAccount();
                } else {
                    login();
                }
            }
        });
        mainActivity.setTitle("BookZap");
        return rootView;
    }

    private void enterCreate() {
        isInCreate = true;
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
        ((Button)mainActivity.findViewById(R.id.button_login)).setText(R.string.create);
        mainActivity.findViewById(R.id.text_account_create_link).setVisibility(View.INVISIBLE);
    }

    private void leaveCreate() {
        isInCreate = false;
        BookZap mainActivity = (BookZap) getActivity();
        mainActivity.findViewById(R.id.button_cancel).setVisibility(View.GONE);
        ((Button)mainActivity.findViewById(R.id.button_login)).setText(R.string.login);
        mainActivity.findViewById(R.id.text_account_create_link).setVisibility(View.VISIBLE);
    }

    private void createAccount() {
        BookZap mainActivity = (BookZap)getActivity();
        mainActivity.getAuthObject().createUserWithEmailAndPassword(
                ((EditText)mainActivity.findViewById(R.id.login_email)).getText().toString(),
                ((EditText)mainActivity.findViewById(R.id.login_password)).getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    BookZap mainActivity = (BookZap)getActivity();
                    mainActivity.setCurrentUser(mainActivity.getAuthObject().getCurrentUser());
                    mainActivity.postLogin();
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void login() {
        BookZap mainActivity = (BookZap)getActivity();
        mainActivity.getAuthObject().signInWithEmailAndPassword(
                ((EditText)mainActivity.findViewById(R.id.login_email)).getText().toString(),
                ((EditText)mainActivity.findViewById(R.id.login_password)).getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    BookZap mainActivity = (BookZap)getActivity();
                    mainActivity.setCurrentUser(mainActivity.getAuthObject().getCurrentUser());
                    mainActivity.postLogin();
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT);
                }
            }
        });
    }


}
