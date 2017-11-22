package uk.co.beamsy.bookzap.bookzap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import uk.co.beamsy.bookzap.bookzap.model.Author;
import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.ui.LibraryFragment;

public class BookZap extends AppCompatActivity {
    private static String[] bookTitles = {"Leviathan's Wake", "Abbadon's Gate", "Absolution Gap"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private LibraryFragment libraryFragment;

    public static final int PERMISSION_REQUEST_CAMERA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_zap);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, bookTitles
        ));
        drawerList.setOnItemClickListener(new DrawerItemClickListeneer());

        Toolbar bookZapBar = (Toolbar) findViewById(R.id.bookZapBar);
        setSupportActionBar(bookZapBar);

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
        drawerLayout.addDrawerListener(drawerToggle);

        libraryFragment = new LibraryFragment();
        prepareData();
        changeFragment(libraryFragment, "library");



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
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "case", Toast.LENGTH_SHORT);
                if(!getFragmentManager().findFragmentByTag("library").isVisible()) {
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

    private class DrawerItemClickListeneer implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View vi, int pos, long id) {
            selectItem(pos);
        }
    }

    public void changeFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.inner_frame, fragment).addToBackStack(null).commit();
    }

    public void changeDrawerToBack() {
        drawerToggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}


