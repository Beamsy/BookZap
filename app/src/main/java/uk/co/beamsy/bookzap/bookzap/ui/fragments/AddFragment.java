package uk.co.beamsy.bookzap.bookzap.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.beamsy.bookzap.bookzap.BookZap;
import uk.co.beamsy.bookzap.bookzap.R;
import uk.co.beamsy.bookzap.bookzap.model.UserBook;
import uk.co.beamsy.bookzap.bookzap.ui.BookCardAdaptor;
import uk.co.beamsy.bookzap.bookzap.ui.RecyclerViewOnTouchItemListener;


public class AddFragment extends Fragment {

    private static AddFragment fragment;
    private RecyclerView recyclerView;
    private BookCardAdaptor bookAdaptor;
    private List<UserBook> searchBookList;
    private Button searchButton;
    private boolean isSearched;
    private static String SEARCH_ISBN = "isbn";
    private static String SEARCH_TITLE = "intitle";
    private static String SEARCH_AUTHOR = "inauthor";
    private EditText isbnText, authorText, titleText;
    private ConstraintLayout constraintLayout;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment getInstance() {
        if(fragment == null) {
            fragment = new AddFragment();
            fragment.init();
        }
        return fragment;
    }

    private void init() {
        searchBookList = new ArrayList<>();
        bookAdaptor = new BookCardAdaptor(this.getContext(), searchBookList, false);
        isSearched = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflator.inflate(R.layout.fragment_add, container, false);
        constraintLayout = (ConstraintLayout)rootView.findViewById(R.id.search_constraint_layout);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bookAdaptor);
        titleText = (EditText)rootView.findViewById(R.id.add_title_edit);
        authorText = (EditText)rootView.findViewById(R.id.add_author_edit);
        isbnText = (EditText)rootView.findViewById(R.id.add_isbn_edit);
        searchButton = (Button)rootView.findViewById(R.id.add_search_button);
        toggleView();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearched) {
                    ArrayMap<String,String> searchTerms = new ArrayMap<>();
                    searchTerms.put(SEARCH_ISBN, isbnText.getText().toString());
                    searchTerms.put(SEARCH_AUTHOR, authorText.getText().toString());
                    searchTerms.put(SEARCH_TITLE, titleText.getText().toString());
                    populate(searchTerms);
                    isSearched = true;
                    toggleView();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(constraintLayout.getWindowToken(), 0);
                } else {
                    searchBookList.clear();
                    bookAdaptor.notifyDataSetChanged();
                    isSearched = false;
                    toggleView();
                }
            }
        });
        final BookZap mainActivity = (BookZap) getActivity();
        mainActivity.setTitle("Add a book");
        mainActivity.changeDrawerBack(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchItemListener(
                this.getContext(), recyclerView,
                new RecyclerViewOnTouchItemListener.OnTouchListener() {
                    @Override
                    public void onTap(View view, int adaptorPosition) {
                        UserBook book = searchBookList.get(adaptorPosition);
                        BookFragment fragment = BookFragment.getInstance();
                        fragment.setBook(book);
                        mainActivity.changeFragment(fragment, "book");
                    }

                    @Override
                    public void onHold(View view, int adaptorPosition) {

                    }
                }));
        return rootView;
    }

    private void toggleView() {
        if (isSearched) {
            constraintLayout.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
        }
    }

    private void populate(ArrayMap<String, String> searchTerms) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String Url = "https://www.googleapis.com/books/v1/volumes?q=";
        for(int i = 0; i < searchTerms.size(); i++) {
            if (!searchTerms.valueAt(i).isEmpty()){
                Url = Url + searchTerms.keyAt(i) + ":" + searchTerms.valueAt(i) + "+";
            }
        }
        final String fUrl = Url.replaceAll(" ", "%20");
        Log.d ("URL: " ,fUrl);
        JsonObjectRequest jORequest = new JsonObjectRequest(Request.Method.GET, fUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("URL: ", fUrl);
                    apiAdaptor(response);
                } catch (JSONException e) {
                    Log.e("A/F", "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Search Volley", error.getLocalizedMessage());
            }
        });
        queue.add(jORequest);
        bookAdaptor.notifyDataSetChanged();
    }

    private void apiAdaptor(JSONObject apiResponse) throws JSONException {
        JSONArray items = apiResponse.getJSONArray("items");
        searchBookList.clear();
        for (int i = 0; i < items.length();i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.getString("kind").equals("books#volume")
                && (item.getJSONObject("volumeInfo").has("printType") && item.getJSONObject("volumeInfo").getString("printType").equals("BOOK") )
                && (item.getJSONObject("volumeInfo").has("language") && item.getJSONObject("volumeInfo").getString("language").equals("en") )
                && item.getJSONObject("volumeInfo").has("pageCount")
                && item.getJSONObject("volumeInfo").has("industryIdentifiers")
                && (item.getJSONObject("volumeInfo").has("imageLinks") && item.getJSONObject("volumeInfo").getJSONObject("imageLinks").has("thumbnail"))) {
                searchBookList.add(jsonToBook(item));
            }
        }
        bookAdaptor.notifyDataSetChanged();
    }

    private UserBook jsonToBook(JSONObject bookObject) throws JSONException{
        Double isbn = 0d;
        JSONArray jA = bookObject.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers");
        for (int i = 0; i < jA.length(); i++) {
            if (jA.getJSONObject(i).getString("type").equals("ISBN_13")) {
                isbn = jA.getJSONObject(i).getDouble("identifier");
                break;
            }
        }
        UserBook book = new UserBook(
                bookObject.getJSONObject("volumeInfo").getString("title"),
                bookObject.getJSONObject("volumeInfo").getJSONArray("authors").getString(0),
                isbn,
                Uri.parse(bookObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail").replace("&edge=curl","")),
                bookObject.getJSONObject("volumeInfo").getInt("pageCount")
        );
        return book;
    }

}
