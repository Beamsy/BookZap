package uk.co.beamsy.bookzap;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private FirebaseAuth auth;
    private boolean isInCreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        constraintLayout = findViewById(R.id.outer_login_layout);
        TextView createLink = findViewById(R.id.text_account_create_link);
        createLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!isInCreate) {
                    enterCreate();
                }
            }
        });
        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInCreate) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
                    leaveCreate();
                }
            }
        });
        Button loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
                if (isInCreate) {
                    createAccount();
                } else {
                    login();
                }
            }
        });
        super.onCreate(savedInstanceState);
    }


    private void enterCreate() {
        isInCreate = true;
        findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.button_login)).setText(R.string.create);
        findViewById(R.id.text_account_create_link).setVisibility(View.INVISIBLE);
    }

    private void leaveCreate() {
        isInCreate = false;
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        ((Button)findViewById(R.id.button_login)).setText(R.string.login);
        findViewById(R.id.text_account_create_link).setVisibility(View.VISIBLE);
    }

    private void createAccount() {

        auth.createUserWithEmailAndPassword(
                ((EditText)findViewById(R.id.login_email)).getText().toString(),
                ((EditText)findViewById(R.id.login_password)).getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {

        auth.signInWithEmailAndPassword(
                ((EditText)findViewById(R.id.login_email)).getText().toString(),
                ((EditText)findViewById(R.id.login_password)).getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}
