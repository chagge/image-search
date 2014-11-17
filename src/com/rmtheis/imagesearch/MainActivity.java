package com.rmtheis.imagesearch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.rmtheis.imagesearch.GoogleSearchResponseData.GoogleSearchResult;
import com.rmtheis.imagesearch.GridViewAdapter.PageListener;

public class MainActivity extends Activity implements Response.ErrorListener, Listener<GoogleSearchResponseData>, PageListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /** Google Custom Search API credentials */
    private static final String API_KEY = "AIzaSyDIqfOxbfY6TV8-1-JWmKpx8QU9iyQTlPM";
    
    // Alternate credentials, tied to package name and developer certificate fingerprint. Usable when app signed with release key.  
    //private static final String API_KEY = "AIzaSyCk03iAcPPhb8ind-wFXgTDST6aoDyu2ak";

    public static final String CUSTOM_SEARCH_ENGINE_ID = "001795296313896293509:ik1awfcdefo";

    /** Minimum number of characters needed in the search string before a request is sent */
    private static final int MINIMUM_SEARCH_STRING_LENGTH = 1;

    /** The number of results to request for a single image search request */
    private static final int PAGE_SIZE = 10;

    /** Delay to wait after search text changes before the search request is sent */
    private static final int SEARCH_DELAY_MS = 900;

    /** Whether to cache responses to image search requests */
    private static final boolean SHOULD_CACHE = true;

    private AsyncTask<?,?,?> searchTask;
    private RequestQueue requestQueue;

    /** The previously-searched search term */
    private String lastSearchString;

    /** The page number to request, so we can request a starting index for search results */
    private int page = 0;

    private GridView gridView;
    private GridViewAdapter adapter;
    private ArrayList<GoogleSearchResult> searchResults = new ArrayList<GoogleSearchResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        EditText searchField = (EditText) findViewById(R.id.editText1);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                textChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Intentionally left blank
            }
        });

        searchField.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Empty click listener, placed here so we get a button click sound.
            }});

        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setOnScrollListener(new ScrollListener(this));
        adapter = new GridViewAdapter(this, searchResults, this);
        gridView.setAdapter(adapter);
        
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked at position=" + position);
                // Launch the ImageDetailActivity with the search result that was clicked
                Intent intent = new Intent(getBaseContext(), ImageDetailActivity.class);
                intent.putExtra(ImageDetailActivity.SEARCH_RESULT_EXTRA, adapter.getItem(position));
                startActivity(intent);
            }});
    }

    @Override
    public void onResume(){
        super.onResume();

        if (requestQueue == null) {
            requestQueue = Volley.getSingletonRequestQueue(this);
        }
    }

    /** Handle a change in the search term text */
    private void textChanged(String searchString) {
        // Cancel any enqueued but not-yet-executed search, say, for a partially-typed word 
        if (searchTask != null) {
            searchTask.cancel(true);
        }

        if (searchString != null && searchString.length() >= MINIMUM_SEARCH_STRING_LENGTH) {
            // Enqueue another search to be sent after a delay
            searchTask = new SearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchString);
        } else {
            searchResults.clear();
        }        
    }

    /** Task to delay sending of search requests, during which delay the task may be canceled */
    private final class SearchTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... searchString) {

            // Cancel any outstanding searches for which we don't yet have a response
            if (requestQueue != null) {
                requestQueue.cancelAll(new RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        // Don't be selective on which requests to cancel--just cancel everything
                        return true;
                    }
                });
            }

            try {
                Thread.sleep(SEARCH_DELAY_MS);
            } catch (InterruptedException e) {
                // We've been canceled, so return without sending the search request
                return null;
            }

            // We made it here without being interrupted, so return keyword to start request
            return searchString[0];
        }

        @Override
        protected void onPostExecute(String result) {
            // Send the search request on the main thread
            if (result != null) {
                sendSearchRequest(result);
            }
        }
    }

    /** Perform the search */
    public void sendSearchRequest(final String searchString) {
        if (searchString == null) {
            throw new IllegalArgumentException("Search string may not be null");
        }
        setProgressBarIndeterminateVisibility(true);

        // If this search is the same as the last one, adjust the start index for search results. 
        if (searchString.equals(lastSearchString)) {
            gridView.setVisibility(View.VISIBLE);
            page++;
        } else {
            gridView.setVisibility(View.INVISIBLE);
            page = 0;
            searchResults.clear();
        }
        int startIndex = page * PAGE_SIZE + 1;

        try { 
            GoogleImageSearchRequest request = new GoogleImageSearchRequest(API_KEY, searchString, startIndex, PAGE_SIZE, this, this);
            request.setShouldCache(SHOULD_CACHE);
            requestQueue = Volley.getSingletonRequestQueue(this);
            requestQueue.add(request);
            lastSearchString = searchString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestNextPage() {
        sendSearchRequest(lastSearchString);
    }

    @Override
    public void onResponse(GoogleSearchResponseData response) {
        setProgressBarIndeterminateVisibility(false);

        if (response != null) {
            ArrayList<GoogleSearchResult> newResults = new ArrayList<GoogleSearchResult>(Arrays.asList(response.getSearchResults()));
            searchResults.addAll(newResults);
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Null response in onResponse");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        setProgressBarIndeterminateVisibility(false);
        String errorText = "Search failed";
        if (error != null && error.getMessage() != null) {
            errorText = errorText + ". error=" + error.getMessage();
        }
        Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 150);
        toast.show();
    }

}
