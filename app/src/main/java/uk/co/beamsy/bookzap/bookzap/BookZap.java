package uk.co.beamsy.bookzap.bookzap;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BookZap extends AppCompatActivity {
    private static String[] bookTitles = {"Leviathan's Wake", "Abbadon's Gate", "Absolution Gap"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<Book> bookList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_zap);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(
                this, R.layout.drawer_list_item, bookTitles
        ));

        Toolbar bookZapBar = (Toolbar) findViewById(R.id.bookZapBar);
        setSupportActionBar(bookZapBar);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.drawer_open, R.string.drawer_close
        ){
            public void onDrawerClosed(View vi){
                super.onDrawerClosed(vi);
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View dVi){
                super.onDrawerOpened(dVi);
                getActionBar().setTitle(R.string.book_zap_book);
                invalidateOptionsMenu();
            }
        };

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        bookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this, bookList);

        RecyclerView.LayoutManager _layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(_layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);

        prepareData();


    }

    private void prepareData(){
        Author a = new Author("Brandon", "Sanderson", 0);
        Book b = new Book("Oathbringer", a, 0);
        bookList.add(b);
        a = new Author("James", "Corey", 1);
        b = new Book("Leviathan Wakes", a, 0);
        bookList.add(b);
        bookAdaptor.notifyDataSetChanged();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isOpen = drawerLayout.isDrawerOpen(drawerList);
        //menu.findItem(R.id.)
        return super.onPrepareOptionsMenu(menu);
    }
}
