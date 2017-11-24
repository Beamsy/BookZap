package uk.co.beamsy.bookzap.bookzap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.beamsy.bookzap.bookzap.model.Author;
import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.ui.LibraryFragment;
import uk.co.beamsy.bookzap.bookzap.ui.LoginFragment;


public class BookZap extends AppCompatActivity {
    private static String[] bookTitles = {"Leviathan's Wake", "Abbadon's Gate", "Absolution Gap"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private LibraryFragment libraryFragment;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    public static final int PERMISSION_REQUEST_CAMERA = 100;

    @Override
    public void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_zap);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, bookTitles
        ));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        Toolbar bookZapBar = (Toolbar) findViewById(R.id.bookZapBar);
        setSupportActionBar(bookZapBar);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
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
        libraryFragment = LibraryFragment.getInstance();
        prepareData();
        if (currentUser != null) {
            changeFragment(libraryFragment, "library");
        } else {
            LoginFragment loginFragment = LoginFragment.getInstance();
            getFragmentManager().beginTransaction().replace(R.id.inner_frame, loginFragment).commit();

        }
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "case", Toast.LENGTH_SHORT);
                FragmentManager fragmentManager = getFragmentManager();
                if(!fragmentManager.findFragmentByTag("library").isInLayout()) {
                    fragmentManager.popBackStack();
                    Toast.makeText(getApplicationContext(), "if", Toast.LENGTH_SHORT);
                }
            }
        });
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();


    }

    private void prepareData(){
        Author a = new Author("Brandon", "Sanderson", 0);
        Book b = new Book("Oathbringer", a, 0, R.drawable.oath);
        libraryFragment.addCard(b);
        a = new Author("James", "Corey", 1);
        b = new Book("Leviathan Wakes", a, 0, R.drawable.lev);
        libraryFragment.addCard(b);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isOpen = drawerLayout.isDrawerOpen(drawerList);
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
        Log.d("onOIS", "outside switch");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("onOIS", "case home");
                Toast.makeText(this, "case", Toast.LENGTH_SHORT);
                if(!getFragmentManager().findFragmentByTag("library").isInLayout()) {
                    getFragmentManager().popBackStack();
                    Toast.makeText(this, "if", Toast.LENGTH_SHORT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void selectItem(int position) {
        Toast.makeText(this, bookTitles[position], Toast.LENGTH_LONG).show();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View vi, int pos, long id) {
            selectItem(pos);
        }
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        if(!tag.equals("library")) {
            fragmentManager.beginTransaction().replace(R.id.inner_frame, fragment).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.inner_frame, fragment).commit();
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
        changeFragment(libraryFragment, "library");
    }
}


