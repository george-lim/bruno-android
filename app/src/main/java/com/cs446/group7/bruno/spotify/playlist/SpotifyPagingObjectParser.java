package com.cs446.group7.bruno.spotify.playlist;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.cs446.group7.bruno.MainActivity;
import com.cs446.group7.bruno.utils.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to handle the paging objects which are returned from the Web API
 * Subclasses are capable of parsing the paging object into lists of different types
 * The recursive parsing logic is performed between getPagingObject() and parsePagingObject()
 */
public abstract class SpotifyPagingObjectParser<T>  {

    private String token;
    private DefaultRetryPolicy retryPolicy;
    private List<T> output;
    private final String TAG = this.getClass().getSimpleName();

    public SpotifyPagingObjectParser(final String token, final DefaultRetryPolicy retryPolicy) {
        this.token = token;
        this.retryPolicy = retryPolicy;
        this.output = new ArrayList<T>();
    }

    protected abstract List<T> parsePagingItems(JSONArray pagingItems) throws JSONException;

    // Given a paging object, process the items using a specific type of parser, and then get the next paging object
    // if there is one available.
    public void parsePagingObject(final JSONObject pagingObj, final Callback<List<T>, Exception> callback) {
        try {
            List<T> pagingResult = this.parsePagingItems(pagingObj.getJSONArray("items"));
            output.addAll(pagingResult);

            String nextUrl = pagingObj.getString("next");
            if (nextUrl != "null") {
                getPagingObject(nextUrl, new Callback<JSONObject, Exception>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        parsePagingObject(result, callback);
                    }

                    @Override
                    public void onFailed(Exception result) {
                        callback.onFailed(result);
                    }
                });
            } else {
                callback.onSuccess(output);
            }
        } catch (JSONException e) {
            Log.e(TAG, "parsePagingObject: JSONException: " + e.getMessage());
            callback.onFailed(e);
        }
    }

    // Sends a request to the Spotify Web API to get a paging object, and then parses it.
    private void getPagingObject(final String url, final Callback<JSONObject, Exception> callback) {
        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                response -> {
                    try {
                        final JSONObject pagingJson = new JSONObject(response);
                        callback.onSuccess(pagingJson);
                    } catch (JSONException e) {
                        Log.e(TAG, "getPagingObject: JSON parsing failure: " + e.getMessage());
                        callback.onFailed(e);
                    }
                }, error -> {
            Log.e(TAG, "getPagingObject: Error with sending the request: " + error.getMessage());
            callback.onFailed(new Exception(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(retryPolicy);
        MainActivity.getVolleyRequestQueue().add(stringRequest);
    }
}
