package com.rmtheis.imagesearch;

import java.util.Map;

import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;

public abstract class CustomRequest<T> extends Request<T> {

    private static final String TAG = CustomRequest.class.getSimpleName();

    public static final String DEFAULT_CONTENT_CHARSET = "UTF-8";

    protected String cacheKey;

    public CustomRequest(int method, String url, ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    public String getCacheKey() {
        if (cacheKey == null) {
            Log.w(TAG, "Making request with no cacheidentifier - using base URL");
        }
        return cacheKey;
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or our custom default (UTF-8) if none can be found. This prevents 
     * decoding errors when we retrieve translations from Volley's cache.
     * 
     * (The original implementation in HttpHeaderParser.java had used 
     * ISO-8859-1 as the default charset.)
     */
    public static String parseCharset(Map<String, String> headers) {
        String contentType = headers.get(HTTP.CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return DEFAULT_CONTENT_CHARSET; // had been HTTP.DEFAULT_CONTENT_CHARSET;
    }

}
