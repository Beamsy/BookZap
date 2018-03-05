package uk.co.beamsy.bookzap.connections;

import android.content.Context;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;

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

import uk.co.beamsy.bookzap.model.UserBook;

/**
 * Created by Jake on 05/03/2018.
 */

public class GoogleBooksConnection {

    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public interface SearchResultListener{
        void onSearchResult(ArrayList<UserBook> userBooks);
    }

    public interface SingleSearchResultListener {
        void onSingleSearchResult(UserBook userBook);
    }

    public static void searchSingle(String ISBN, Context context, final SingleSearchResultListener listener) {
        String Url = BASE_URL + "isbn:" + ISBN;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jORequest = new JsonObjectRequest(
                Request.Method.GET,
                Url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listener.onSingleSearchResult(apiAdaptor(response).get(0));
                        } catch (JSONException e) {
                            listener.onSingleSearchResult(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Search Volley", error.getLocalizedMessage());
                    }
                });
        queue.add(jORequest);
    }


    public static void search(
            ArrayMap<String, String> searchTerms, Context context, final SearchResultListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String Url = BASE_URL;
        for(int i = 0; i < searchTerms.size(); i++) {
            if (!searchTerms.valueAt(i).isEmpty()){
                Url = Url + searchTerms.keyAt(i) + ":" + searchTerms.valueAt(i) + "+";
            }
        }
        String fUrl = Url.replaceAll(" ", "%20");
        JsonObjectRequest jORequest = new JsonObjectRequest(
                Request.Method.GET,
                fUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listener.onSearchResult(apiAdaptor(response));
                        } catch (JSONException e) {
                            Log.e( "search", e.getLocalizedMessage());
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Search Volley", error.getLocalizedMessage());
                    }
                });
        queue.add(jORequest);

    }

    private static ArrayList<UserBook> apiAdaptor(JSONObject apiResponse) throws JSONException {
        JSONArray items = apiResponse.getJSONArray("items");
        ArrayList<UserBook> bookList = new ArrayList<>();
        for (int i = 0; i < items.length();i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.getString("kind").equals("books#volume")
                    && (item.getJSONObject("volumeInfo").has("printType") && item.getJSONObject("volumeInfo").getString("printType").equals("BOOK") )
                    && (item.getJSONObject("volumeInfo").has("language") && item.getJSONObject("volumeInfo").getString("language").equals("en") )
                    && item.getJSONObject("volumeInfo").has("industryIdentifiers") && item.getJSONObject("volumeInfo").has("description")
                    && (item.getJSONObject("volumeInfo").has("imageLinks") && item.getJSONObject("volumeInfo").getJSONObject("imageLinks").has("thumbnail"))
                    && item.getJSONObject("volumeInfo").has("authors")) {
                bookList.add(jsonToBook(item));
            }
        }
        return bookList;
    }


    private static UserBook jsonToBook(JSONObject bookObject) throws JSONException {
        double isbn = 0d;
        JSONArray jA = bookObject.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers");
        for (int i = 0; i < jA.length(); i++) {
            if (jA.getJSONObject(i).getString("type").equals("ISBN_13")) {
                isbn = jA.getJSONObject(i).getDouble("identifier");
                break;
            }
        }
        int pageCount;
        if (!bookObject.getJSONObject("volumeInfo").has("pageCount")){
            pageCount = 0;
        } else {
            pageCount = bookObject.getJSONObject("volumeInfo").getInt("pageCount");
        }
        return new UserBook(
                bookObject.getJSONObject("volumeInfo").getString("title"),
                bookObject.getJSONObject("volumeInfo").getJSONArray("authors").getString(0),
                isbn,
                Uri.parse(bookObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail").replace("&edge=curl","")),
                pageCount,
                bookObject.getString("id"),
                bookObject.getJSONObject("volumeInfo").getString("description")
        );
    }

}
