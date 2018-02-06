package uk.co.beamsy.bookzap.bookzap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.bookzap.ui.fragments.LibraryFragment;
import uk.co.beamsy.bookzap.bookzap.ui.fragments.LoginFragment;
import uk.co.beamsy.bookzap.bookzap.ui.fragments.ReadingListFragment;


public class BookZap extends AppCompatActivity {
    private static String[] bookTitles = {"Leviathan's Wake", "Abbadon's Gate", "Absolution Gap"};
    private DrawerLayout drawerLayout;
    private NavigationView drawerNav;
    private ActionBarDrawerToggle drawerToggle;
    private LibraryFragment libraryFragment;
    private FirebaseAuth auth;
    private FirestoreControl fs;
    private FirebaseUser currentUser;
    private TextView logoutText;
    private List<UserBook> bookList = new ArrayList<>();
    private Toolbar bookZapBar;

    @Override
    public void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_zap);

        //Setting up the user interface
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerNav = (NavigationView)findViewById(R.id.nav_view);
        logoutText = (TextView) drawerNav.getHeaderView(0).findViewById(R.id.log_out);
        TextView userText = (TextView)  drawerNav.getHeaderView(0).findViewById(R.id.user_greet);
        bookZapBar = (Toolbar) findViewById(R.id.bookZapBar);
        setSupportActionBar(bookZapBar);

        //Setup ActionBarDrawerToggle object to control left navbar drawer
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, bookZapBar, R.string.drawer_open, R.string.drawer_close
        ){
            public void onDrawerClosed(View vi){
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View dVi){
                invalidateOptionsMenu();
            }
        };

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();

                LoginFragment loginFragment = LoginFragment.getInstance();
                getFragmentManager().beginTransaction().replace(R.id.inner_frame, loginFragment).commit();
            }
        });


        //Auth and user objects to interact with the Firebase services
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //Create library fragment
        libraryFragment = LibraryFragment.getInstance();

        /* Check if the currentUser object is null
         * If it is not null then the load the library fragment
         *
         * If it is null, then there is no user logged in
         * so the login fragment is loaded instead
         */
        if (currentUser != null) {
            changeFragment(libraryFragment, "library");
            if (currentUser.getDisplayName() != null) {
                userText.setText("Hello " + currentUser.getDisplayName());
            }
            fs = FirestoreControl.getInstance(currentUser);
            //fs.getBookPage(libraryFragment);
        } else {
            //Login fragment is created
            LoginFragment loginFragment = LoginFragment.getInstance();
            getFragmentManager().beginTransaction().replace(R.id.inner_frame, loginFragment).commit();
        }
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if(!fragmentManager.findFragmentByTag("library").isInLayout()) {
                    fragmentManager.popBackStack();
                }
            }
        });
        drawerLayout.addDrawerListener(drawerToggle);

        drawerNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_library:
                        if (!item.isChecked()) {
                            changeFragment(libraryFragment, "library");
                            item.setChecked(true);
                        }

                    case R.id.menu_reading_list:
                        if (!item.isChecked()) {
                            changeFragment(ReadingListFragment.getInstance(), "readingList");
                            item.setChecked(true);
                        }
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });


        drawerToggle.syncState();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isOpen = drawerLayout.isDrawerOpen(drawerNav);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Overrides needed for ActionBarDrawerToggle
     *
     */

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!getFragmentManager().findFragmentByTag("library").isInLayout()) {
                    getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void selectItem(int position) {
        Toast.makeText(this, bookTitles[position], Toast.LENGTH_SHORT).show();
    }



    public void changeFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        if(!tag.equals("library")) {
            fragmentManager.beginTransaction().replace(R.id.inner_frame, fragment, tag).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.inner_frame, fragment, tag).commit();
        }
    }

    public void hideHome() {
        drawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    public void changeDrawerBack(boolean direction) {
        if (direction) {
            drawerToggle.setDrawerIndicatorEnabled(!direction);
            getSupportActionBar().setHomeButtonEnabled(direction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(direction);
        } else {
            getSupportActionBar().setHomeButtonEnabled(direction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(direction);
            drawerToggle.setDrawerIndicatorEnabled(!direction);
        }
    }

    public FirebaseAuth getAuthObject() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser mUser) {
        currentUser = mUser;
    }

    public void postLogin() {
        FirestoreControl fs = FirestoreControl.getInstance(currentUser);
        changeFragment(libraryFragment, "library");
    }

    public void update() {
        fs.getBookPage(libraryFragment);
    }

    public List<UserBook> getBookList() {
        return bookList;
    }

    public void setBookList(List<UserBook> bookList) {
        this.bookList = bookList;
    }
}


