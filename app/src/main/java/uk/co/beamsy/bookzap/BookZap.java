package uk.co.beamsy.bookzap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


import uk.co.beamsy.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.ui.BookListListener;
import uk.co.beamsy.bookzap.ui.fragments.LibraryFragment;
import uk.co.beamsy.bookzap.ui.fragments.LoginFragment;
import uk.co.beamsy.bookzap.ui.fragments.ReadingListFragment;


public class BookZap extends AppCompatActivity implements BookListListener {
    private DrawerLayout drawerLayout;
    private NavigationView drawerNav;
    private ActionBarDrawerToggle drawerToggle;
    private LibraryFragment libraryFragment;
    private FirebaseAuth auth;
    private FirestoreControl fs;
    private FirebaseUser currentUser;
    private List<UserBook> bookList = new ArrayList<>();
    private ProgressBar loadingBar;
    private Boolean isLoggedIn;

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
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerNav = findViewById(R.id.nav_view);
        TextView logoutText = drawerNav.getHeaderView(0).findViewById(R.id.log_out);
        TextView userText = drawerNav.getHeaderView(0).findViewById(R.id.user_greet);
        Toolbar bookZapBar = findViewById(R.id.bookZapBar);
        setSupportActionBar(bookZapBar);
        loadingBar = findViewById(R.id.centre_load);

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
                drawerLayout.closeDrawers();
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
            isLoggedIn = true;
            changeFragment(libraryFragment, "library");
            if (currentUser.getDisplayName() != null) {
                userText.setText("Hello " + currentUser.getDisplayName());
            }
            fs = FirestoreControl.getInstance(currentUser);
            //fs.getBookPage(libraryFragment);
        } else {
            //Login fragment is created
            isLoggedIn = false;
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
        if(isLoggedIn) {
            if (bookList.size() == 0) {
                loadingBar.setVisibility(View.VISIBLE);
            }
            fs.getBookPage(this, null, FirestoreControl.SORT_TYPE_ISBN);
        }
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
        //This appears to be redundant code duplication, but the order of method invocation is
        //necessary.

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
        loadingBar.setVisibility(View.VISIBLE);
        fs = FirestoreControl.getInstance(currentUser);
        changeFragment(libraryFragment, "library");
        isLoggedIn = true;
        fs.getBookPage(this, null, FirestoreControl.SORT_TYPE_ISBN);
    }

    public void update() {
        fs.getBookPage(libraryFragment, bookList.get(bookList.size()-1), FirestoreControl.SORT_TYPE_ISBN);
    }

    public List<UserBook> getBookList() {
        return bookList;
    }

    public void setBookList(List<UserBook> bookList) {
        this.bookList = bookList;
    }

    @Override
    public void onBookListFetch(List<UserBook> userBooks) {
        setBookList(userBooks);
        libraryFragment.setBookList(userBooks);
        loadingBar.setVisibility(View.GONE);
    }
}


