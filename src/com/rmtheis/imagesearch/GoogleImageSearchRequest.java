package com.rmtheis.imagesearch;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

public class GoogleImageSearchRequest extends CustomRequest<GoogleSearchResponseData> {

    private static final String TAG = GoogleImageSearchRequest.class.getSimpleName();

    // For request parameters details see:
    // https://developers.google.com/custom-search/docs/xml_results#wsRequestParameters
    private static final String URL = "https://www.googleapis.com/customsearch/v1?";
    private static final String PARAM_KEY = "&key=",
            PARAM_CLIENT = "&client=",
            PARAM_CSE_ID = "&cx=",
            PARAM_SEARCH_TERM = "&q=",
            PARAM_SEARCH_TYPE = "&searchType=",
            PARAM_INPUT_ENCODING = "&ie=",
            PARAM_OUTPUT_ENCODING = "&oe=",
            PARAM_SAFE_SEARCH = "&safe=",
            PARAM_STARTING_INDEX = "&start=",
            PARAM_NUM_RESULTS = "&num=";
    private static final String CLIENT_GOOGLE_CSBE = "google-csbe";
    private static final String SEARCH_TYPE_IMAGE = "image";
    private static final String SAFE_SEARCH_LEVEL_HIGH = "high";
    
    private Listener<GoogleSearchResponseData> listener;

    public GoogleImageSearchRequest(String apiKey, String cseId, String searchTerm, int startingIndex, int numResults, Listener<GoogleSearchResponseData> listener, ErrorListener errorListener) throws UnsupportedEncodingException {
        super(Method.GET, URL
                + PARAM_KEY + apiKey
                + PARAM_CSE_ID + Uri.encode(cseId)
                + PARAM_CLIENT + CLIENT_GOOGLE_CSBE
                + PARAM_SEARCH_TERM + escapeString(searchTerm)
                + PARAM_SEARCH_TYPE + SEARCH_TYPE_IMAGE
                + PARAM_INPUT_ENCODING + DEFAULT_CONTENT_CHARSET
                + PARAM_OUTPUT_ENCODING + DEFAULT_CONTENT_CHARSET
                + PARAM_SAFE_SEARCH + SAFE_SEARCH_LEVEL_HIGH
                + PARAM_STARTING_INDEX + startingIndex
                + PARAM_NUM_RESULTS + numResults
                , errorListener);

        this.listener = listener;
        this.cacheKey = searchTerm;
    }

    @Override
    public Map<String, String> getHeaders() {
        // Set a referer in the header as required by our API key.
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("referer", "www.rmtheis.com");
        return params;
    }

    @Override
    protected Response<GoogleSearchResponseData> parseNetworkResponse(NetworkResponse response) {
        try {
            // Parse the response, using our custom character set parsing
            String responseString = new String(response.data, parseCharset(response.headers));

            Gson gson = new Gson();
            GoogleSearchResponseData searchResults = null;
            try {
                searchResults = gson.fromJson(responseString, GoogleSearchResponseData.class);
            } catch (com.google.gson.JsonSyntaxException e) {
                // Catch weird intermittent HTML response from API.
                Log.e(TAG, "Caught JsonSyntaxException when parsing network response. Non-JSON response?");
                return Response.error(new VolleyError("JsonSyntaxException"));
            }

            // Return the response using our custom header parsing
            return Response.success(searchResults, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(GoogleSearchResponseData response) {
        listener.onResponse(response);
    }

    /**
     * Return a string that is properly encoded to be sent as a Google Search query term.
     * https://developers.google.com/custom-search/docs/xml_results#urlEscaping
     */
    private static String escapeString(String originalString) {
        if (originalString == null) {
            return null;
        }
        if (originalString == "") {
            return "";
        }

        String returnString = originalString.trim();
        returnString.replaceAll("\\s+", "+");
        returnString = Uri.encode(returnString);
        return returnString;
    }

}