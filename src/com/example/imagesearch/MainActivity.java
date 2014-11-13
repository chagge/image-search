package com.example.imagesearch;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /** Minimum number of characters needed in the search string before a request is sent */
    private static final int MINIMUM_SEARCH_STRING_LENGTH = 1;

    /** Delay to wait after search text changes before the search request is sent */
    private static final int SEARCH_DELAY_MS = 900;

    private AsyncTask<?,?,?> searchTask;

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
    }

    /** Handle a change in the search term text */
    private void textChanged(String searchString) {
        if (searchString != null && searchString.length() >= MINIMUM_SEARCH_STRING_LENGTH) {
            // Perform a new search
            
            setProgressBarIndeterminateVisibility(true);
            
            // TODO show results view
            
            // Cancel any enqueued but not-yet-executed search, say, for a partially-typed word 
            if (searchTask != null) {
                searchTask.cancel(true);
            }
            
            // Enqueue another search to be sent after a delay
            searchTask = new SearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchString);
            
        } else {
            // TODO Hide the existing search results
            // TODO Set empty view text?
        }        
    }

    /** Task to delay sending of search requests, during which delay the task may be canceled */
    private final class SearchTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... searchString) {
            
            try {
                Thread.sleep(SEARCH_DELAY_MS);
            } catch (InterruptedException e) {
                // We've been canceled, so return without sending the search request
                return null;
            }

            // Perform the search
            Log.d(TAG, "Sending search=" + searchString[0]);
            
            // TODO request the search
            // TODO cancel any outstanding searches

            return null;
        }
    }
    
    // TODO receive completed search

}
